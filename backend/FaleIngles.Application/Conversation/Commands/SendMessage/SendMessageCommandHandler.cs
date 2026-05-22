using FaleIngles.Domain.Common;
using FaleIngles.Domain.Errors;
using FaleIngles.Domain.Interfaces;
using MediatR;

namespace FaleIngles.Application.Conversation.Commands.SendMessage;

internal sealed class SendMessageCommandHandler : IRequestHandler<SendMessageCommand, Result<AiResponseDto>>
{
    private readonly IAiTutorService _aiTutorService;
    private readonly IUserProgressRepository _progressRepository;

    public SendMessageCommandHandler(IAiTutorService aiTutorService, IUserProgressRepository progressRepository)
    {
        _aiTutorService = aiTutorService;
        _progressRepository = progressRepository;
    }

    public async Task<Result<AiResponseDto>> Handle(SendMessageCommand request, CancellationToken cancellationToken)
    {
        var progress = await _progressRepository.GetByUserIdAsync(request.UserId, cancellationToken);
        if (progress is null) return Result.Failure<AiResponseDto>(UserErrors.NotFound);
        if (!progress.IsPro) return Result.Failure<AiResponseDto>(UserErrors.ProRequired);

        var aiRequest = new AiTutorRequest(
            ScenarioSystemPrompt: request.ScenarioSystemPrompt,
            History: request.History.Select(m => new AiMessage(m.Role, m.Content)).ToList(),
            UserMessage: request.UserMessage,
            UserPhaseLevel: GetUserPhaseLevel(progress.CompletedLessonIds.Count)
        );

        var result = await _aiTutorService.SendMessageAsync(aiRequest, cancellationToken);
        if (result.IsFailure) return Result.Failure<AiResponseDto>(result.Error);

        var response = result.Value;
        var dto = new AiResponseDto(
            Content: response.Content,
            Correction: response.Correction is null ? null : new CorrectionDto(
                Original: response.Correction.Original,
                Corrected: response.Correction.Corrected,
                Explanation: response.Correction.Explanation,
                Type: response.Correction.Type
            )
        );

        return Result.Success(dto);
    }

    private static int GetUserPhaseLevel(int completedCount) => completedCount switch
    {
        < 8 => 1,
        < 16 => 2,
        < 24 => 3,
        < 32 => 4,
        _ => 5,
    };
}
