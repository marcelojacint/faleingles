using FaleIngles.Domain.Common;
using FaleIngles.Domain.Enums;
using MediatR;

namespace FaleIngles.Application.Lessons.Queries.GetLessonById;

public sealed record GetLessonByIdQuery(Guid LessonId, string UserId) : IRequest<Result<LessonDetailDto>>;

public sealed record LessonDetailDto(
    Guid Id,
    int Phase,
    int OrderInPhase,
    string Title,
    string SituationContext,
    bool IsPremium,
    IReadOnlyList<PhraseDto> Phrases
);

public sealed record PhraseDto(
    Guid Id,
    string Affirmative,
    string Negative,
    string Interrogative,
    string AudioUrl,
    IReadOnlyList<WordDto> Words
);

public sealed record WordDto(
    string Text,
    string Translation,
    GrammaticalType GrammaticalType,
    string RoleInPhrase
);
