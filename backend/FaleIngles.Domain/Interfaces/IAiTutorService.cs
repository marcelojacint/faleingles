using FaleIngles.Domain.Common;

namespace FaleIngles.Domain.Interfaces;

public interface IAiTutorService
{
    Task<Result<AiTutorResponse>> SendMessageAsync(AiTutorRequest request, CancellationToken cancellationToken = default);
}

public sealed record AiTutorRequest(
    string ScenarioSystemPrompt,
    IReadOnlyList<AiMessage> History,
    string UserMessage,
    int UserPhaseLevel
);

public sealed record AiTutorResponse(
    string Content,
    GrammarCorrectionDto Correction
);

public sealed record AiMessage(string Role, string Content);

public sealed record GrammarCorrectionDto(string Original, string Corrected, string Explanation, string Type);
