using FaleIngles.Domain.Common;
using FaleIngles.Domain.Interfaces;
using MediatR;

namespace FaleIngles.Application.Lessons.Queries.GetLessons;

internal sealed class GetLessonsQueryHandler : IRequestHandler<GetLessonsQuery, Result<IReadOnlyList<LessonSummaryDto>>>
{
    private readonly ILessonRepository _lessonRepository;
    private readonly IUserProgressRepository _progressRepository;

    public GetLessonsQueryHandler(ILessonRepository lessonRepository, IUserProgressRepository progressRepository)
    {
        _lessonRepository = lessonRepository;
        _progressRepository = progressRepository;
    }

    public async Task<Result<IReadOnlyList<LessonSummaryDto>>> Handle(GetLessonsQuery request, CancellationToken cancellationToken)
    {
        var lessons = await _lessonRepository.GetAllAsync(cancellationToken);
        var progress = await _progressRepository.GetByUserIdAsync(request.UserId, cancellationToken);

        var dtos = lessons
            .OrderBy(l => l.Phase)
            .ThenBy(l => l.OrderInPhase)
            .Select(l => new LessonSummaryDto(
                Id: l.Id,
                Phase: l.Phase,
                OrderInPhase: l.OrderInPhase,
                Title: l.Title,
                SituationContext: l.SituationContext,
                IsPremium: l.IsPremium,
                IsUnlocked: !l.IsPremium || progress.IsPro || (l.Phase == 1 && l.OrderInPhase <= 3),
                PhraseCount: l.Phrases.Count
            ))
            .ToList()
            .AsReadOnly();

        return Result.Success<IReadOnlyList<LessonSummaryDto>>(dtos);
    }
}
