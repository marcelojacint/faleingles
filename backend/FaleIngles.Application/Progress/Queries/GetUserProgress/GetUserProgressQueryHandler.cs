using FaleIngles.Domain.Common;
using FaleIngles.Domain.Errors;
using FaleIngles.Domain.Interfaces;
using MediatR;

namespace FaleIngles.Application.Progress.Queries.GetUserProgress;

internal sealed class GetUserProgressQueryHandler : IRequestHandler<GetUserProgressQuery, Result<UserProgressDto>>
{
    private readonly IUserProgressRepository _progressRepository;

    public GetUserProgressQueryHandler(IUserProgressRepository progressRepository) =>
        _progressRepository = progressRepository;

    public async Task<Result<UserProgressDto>> Handle(GetUserProgressQuery request, CancellationToken cancellationToken)
    {
        var progress = await _progressRepository.GetByUserIdAsync(request.UserId, cancellationToken);
        if (progress is null) return Result.Failure<UserProgressDto>(UserErrors.NotFound);

        return Result.Success(new UserProgressDto(
            UserId: progress.UserId,
            CurrentStreakDays: progress.CurrentStreakDays,
            TotalMinutesStudied: progress.TotalMinutesStudied,
            TodayMinutesStudied: progress.TodayMinutesStudied,
            DailyGoalMinutes: progress.DailyGoalMinutes,
            IsPro: progress.IsPro,
            CompletedLessonsCount: progress.CompletedLessonIds.Count,
            MasteredPhrasesCount: progress.MasteredPhraseIds.Count
        ));
    }
}
