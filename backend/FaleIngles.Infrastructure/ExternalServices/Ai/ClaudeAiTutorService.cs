using FaleIngles.Domain.Common;
using FaleIngles.Domain.Interfaces;
using Microsoft.Extensions.Logging;
using System.Net.Http.Headers;
using System.Net.Http.Json;
using System.Text.Json;
using System.Text.Json.Serialization;
using DomainError = FaleIngles.Domain.Common.Error;

namespace FaleIngles.Infrastructure.ExternalServices.Ai;

internal sealed class ClaudeAiTutorService : IAiTutorService
{
    private readonly HttpClient _httpClient;
    private readonly ILogger<ClaudeAiTutorService> _logger;

    private const string Model = "claude-sonnet-4-6";
    private const int MaxTokens = 1024;
    private const string ApiUrl = "https://api.anthropic.com/v1/messages";

    public ClaudeAiTutorService(HttpClient httpClient, ILogger<ClaudeAiTutorService> logger)
    {
        _httpClient = httpClient;
        _logger = logger;
    }

    public async Task<Result<AiTutorResponse>> SendMessageAsync(AiTutorRequest request, CancellationToken cancellationToken = default)
    {
        try
        {
            var messages = request.History
                .Select(m => new AnthropicMessage(m.Role, m.Content))
                .Append(new AnthropicMessage("user", request.UserMessage))
                .ToList();

            var payload = new AnthropicRequest(
                Model: Model,
                MaxTokens: MaxTokens,
                System: BuildSystemPrompt(request.ScenarioSystemPrompt, request.UserPhaseLevel),
                Messages: messages
            );

            var response = await _httpClient.PostAsJsonAsync(ApiUrl, payload, cancellationToken);
            response.EnsureSuccessStatusCode();

            var rawBody = await response.Content.ReadAsStringAsync(cancellationToken);
            var apiResponse = JsonSerializer.Deserialize<AnthropicResponse>(rawBody);
            var text = apiResponse?.Content?.FirstOrDefault()?.Text ?? string.Empty;

            return Result.Success(ParseResponse(text));
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Failed to get response from Claude AI");
            return Result.Failure<AiTutorResponse>(
                new DomainError("AI.ServiceUnavailable", "AI tutor is temporarily unavailable. Please try again.")
            );
        }
    }

    private static string BuildSystemPrompt(string scenarioPrompt, int userLevel) =>
        $"""
        You are FaleInglês AI Tutor — an encouraging English teacher for Brazilian Portuguese speakers.

        SCENARIO: {scenarioPrompt}

        USER LEVEL: Phase {userLevel} of 5 (1=beginner, 5=advanced).

        RULES:
        1. Always respond in English appropriate for the user's level.
        2. Keep responses concise (1-3 sentences).
        3. Correct ONLY the most important grammar error per turn.
        4. Always validate what the user got right before correcting.
        5. Never break conversation flow to correct — finish your response first.
        6. Respond ONLY in JSON with two possible shapes:
           Shape A (with correction): {JsonShapeWithCorrection}
           Shape B (no correction):   {JsonShapeWithoutCorrection}
        """;

    private const string JsonShapeWithCorrection =
        """{"content":"<reply>","correction":{"original":"<user text>","corrected":"<corrected>","explanation":"<why>","type":"grammar|naturalness"}}""";

    private const string JsonShapeWithoutCorrection =
        """{"content":"<reply>","correction":null}""";

    private static AiTutorResponse ParseResponse(string rawContent)
    {
        try
        {
            var json = JsonDocument.Parse(rawContent);
            var content = json.RootElement.GetProperty("content").GetString() ?? rawContent;

            GrammarCorrectionDto correction = null;
            if (json.RootElement.TryGetProperty("correction", out var correctionElement) &&
                correctionElement.ValueKind != JsonValueKind.Null)
            {
                correction = new GrammarCorrectionDto(
                    Original: correctionElement.GetProperty("original").GetString(),
                    Corrected: correctionElement.GetProperty("corrected").GetString(),
                    Explanation: correctionElement.GetProperty("explanation").GetString(),
                    Type: correctionElement.GetProperty("type").GetString()
                );
            }

            return new AiTutorResponse(content, correction);
        }
        catch
        {
            return new AiTutorResponse(rawContent, null);
        }
    }

    private sealed record AnthropicRequest(
        [property: JsonPropertyName("model")] string Model,
        [property: JsonPropertyName("max_tokens")] int MaxTokens,
        [property: JsonPropertyName("system")] string System,
        [property: JsonPropertyName("messages")] List<AnthropicMessage> Messages
    );

    private sealed record AnthropicMessage(
        [property: JsonPropertyName("role")] string Role,
        [property: JsonPropertyName("content")] string Content
    );

    private sealed record AnthropicResponse(
        [property: JsonPropertyName("content")] List<AnthropicContent> Content
    );

    private sealed record AnthropicContent(
        [property: JsonPropertyName("type")] string Type,
        [property: JsonPropertyName("text")] string Text
    );
}
