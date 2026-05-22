using FaleIngles.Domain.Common;
using FaleIngles.Domain.Errors;
using FaleIngles.Domain.Interfaces;
using MediatR;

namespace FaleIngles.Application.Lessons.Queries.GetLessonById;

internal sealed class GetLessonByIdQueryHandler : IRequestHandler<GetLessonByIdQuery, Result<LessonDetailDto>>
{
    private readonly ILessonRepository _lessonRepository;
    private readonly IUserProgressRepository _progressRepository;

    public GetLessonByIdQueryHandler(ILessonRepository lessonRepository, IUserProgressRepository progressRepository)
    {
        _lessonRepository = lessonRepository;
        _progressRepository = progressRepository;
    }

    public async Task<Result<LessonDetailDto>> Handle(GetLessonByIdQuery request, CancellationToken cancellationToken)
    {
        var lesson = await _lessonRepository.GetByIdAsync(request.LessonId, cancellationToken);
        if (lesson is null) return Result.Failure<LessonDetailDto>(LessonErrors.NotFound);

        if (lesson.IsPremium)
        {
            var progress = await _progressRepository.GetByUserIdAsync(request.UserId, cancellationToken);
            var isFreeLesson = lesson.Phase == 1 && lesson.OrderInPhase <= 3;
            if (!progress.IsPro && !isFreeLesson)
                return Result.Failure<LessonDetailDto>(LessonErrors.Locked);
        }

        var dto = new LessonDetailDto(
            Id: lesson.Id,
            Phase: lesson.Phase,
            OrderInPhase: lesson.OrderInPhase,
            Title: lesson.Title,
            SituationContext: lesson.SituationContext,
            IsPremium: lesson.IsPremium,
            Phrases: lesson.Phrases.Select(p => new PhraseDto(
                Id: p.Id,
                Affirmative: p.Affirmative,
                Negative: p.Negative,
                Interrogative: p.Interrogative,
                AudioUrl: p.AudioUrl,
                Words: p.Words.Select(w => new WordDto(
                    Text: w.Text,
                    Translation: w.Translation,
                    GrammaticalType: w.GrammaticalType,
                    RoleInPhrase: w.RoleInPhrase
                )).ToList().AsReadOnly()
            )).ToList().AsReadOnly()
        );

        return Result.Success(dto);
    }
}
