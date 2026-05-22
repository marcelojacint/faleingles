using FaleIngles.Domain.Common;
using MediatR;

namespace FaleIngles.Application.Conversation.Commands.SendMessage;

public sealed record SendMessageCommand(
    string UserId,
    string ScenarioId,
    string ScenarioSystemPrompt,
    IReadOnlyList<MessageDto> History,
    string UserMessage
) : IRequest<Result<AiResponseDto>>;

public sealed record MessageDto(string Role, string Content);

public sealed record AiResponseDto(
    string Content,
    CorrectionDto Correction
);

public sealed record CorrectionDto(
    string Original,
    string Corrected,
    string Explanation,
    string Type
);
