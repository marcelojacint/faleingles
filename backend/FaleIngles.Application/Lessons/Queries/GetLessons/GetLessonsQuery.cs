using FaleIngles.Domain.Common;
using MediatR;

namespace FaleIngles.Application.Lessons.Queries.GetLessons;

public sealed record GetLessonsQuery(string UserId) : IRequest<Result<IReadOnlyList<LessonSummaryDto>>>;

public sealed record LessonSummaryDto(
    Guid Id,
    int Phase,
    int OrderInPhase,
    string Title,
    string SituationContext,
    bool IsPremium,
    bool IsUnlocked,
    int PhraseCount
);
