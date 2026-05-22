using FaleIngles.Api.Extensions;
using FaleIngles.Application.Conversation.Commands.SendMessage;
using MediatR;

namespace FaleIngles.Api.Endpoints;

public static class ConversationEndpoints
{
    public static IEndpointRouteBuilder MapConversationEndpoints(this IEndpointRouteBuilder app)
    {
        var group = app.MapGroup("/api/conversation").WithTags("Conversation").RequireAuthorization();

        group.MapPost("/message", SendMessage)
            .WithName("SendConversationMessage")
            .WithSummary("Sends a message to the AI tutor and receives a response with optional grammar correction.");

        return app;
    }

    private static async Task<IResult> SendMessage(
        SendMessageRequest request,
        HttpContext http,
        ISender sender,
        CancellationToken cancellationToken)
    {
        var userId = http.User.FindFirst("sub")?.Value ?? string.Empty;

        var command = new SendMessageCommand(
            UserId: userId,
            ScenarioId: request.ScenarioId,
            ScenarioSystemPrompt: request.ScenarioSystemPrompt,
            History: request.History.Select(m => new MessageDto(m.Role, m.Content)).ToList(),
            UserMessage: request.UserMessage
        );

        var result = await sender.Send(command, cancellationToken);
        return result.ToHttpResult();
    }
}

public sealed record SendMessageRequest(
    string ScenarioId,
    string ScenarioSystemPrompt,
    IReadOnlyList<MessageRequest> History,
    string UserMessage
);

public sealed record MessageRequest(string Role, string Content);
