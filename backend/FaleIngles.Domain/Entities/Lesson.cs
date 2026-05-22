using FaleIngles.Domain.Common;
using FaleIngles.Domain.Errors;

namespace FaleIngles.Domain.Entities;

public sealed class Lesson : Entity
{
    private readonly List<Phrase> _phrases = new();

    private Lesson() : base(Guid.Empty) { }

    private Lesson(Guid id, int phase, int orderInPhase, string title, string situationContext, bool isPremium)
        : base(id)
    {
        Phase = phase;
        OrderInPhase = orderInPhase;
        Title = title;
        SituationContext = situationContext;
        IsPremium = isPremium;
    }

    public int Phase { get; private set; }
    public int OrderInPhase { get; private set; }
    public string Title { get; private set; }
    public string SituationContext { get; private set; }
    public bool IsPremium { get; private set; }
    public IReadOnlyCollection<Phrase> Phrases => _phrases.AsReadOnly();

    public static Result<Lesson> Create(int phase, int orderInPhase, string title, string situationContext, bool isPremium)
    {
        if (phase is < 1 or > 5)
            return Result.Failure<Lesson>(LessonErrors.InvalidPhase);

        if (string.IsNullOrWhiteSpace(title))
            return Result.Failure<Lesson>(LessonErrors.EmptyTitle);

        if (string.IsNullOrWhiteSpace(situationContext))
            return Result.Failure<Lesson>(LessonErrors.EmptySituationContext);

        return Result.Success(new Lesson(Guid.NewGuid(), phase, orderInPhase, title, situationContext, isPremium));
    }

    public Result AddPhrase(Phrase phrase)
    {
        if (phrase is null)
            return Result.Failure(Error.NullValue);

        _phrases.Add(phrase);
        return Result.Success();
    }
}
