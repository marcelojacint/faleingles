using FaleIngles.Domain.Common;
using MediatR;

namespace FaleIngles.Application.Progress.Queries.GetUserProgress;

public sealed record GetUserProgressQuery(string UserId) : IRequest<Result<UserProgressDto>>;

public sealed record UserProgressDto(
    string UserId,
    int CurrentStreakDays,
    int TotalMinutesStudied,
    int TodayMinutesStudied,
    int DailyGoalMinutes,
    bool IsPro,
    int CompletedLessonsCount,
    int MasteredPhrasesCount
);
