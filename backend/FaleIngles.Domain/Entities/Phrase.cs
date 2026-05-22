using FaleIngles.Domain.Common;
using FaleIngles.Domain.Errors;

namespace FaleIngles.Domain.Entities;

public sealed class Phrase : Entity
{
    private readonly List<Word> _words = new();

    private Phrase() : base(Guid.Empty) { }

    private Phrase(Guid id, string affirmative, string negative, string interrogative, string audioUrl)
        : base(id)
    {
        Affirmative = affirmative;
        Negative = negative;
        Interrogative = interrogative;
        AudioUrl = audioUrl;
    }

    public string Affirmative { get; private set; }
    public string Negative { get; private set; }
    public string Interrogative { get; private set; }
    public string AudioUrl { get; private set; }
    public Guid LessonId { get; private set; }
    public IReadOnlyCollection<Word> Words => _words.AsReadOnly();

    public static Result<Phrase> Create(string affirmative, string negative, string interrogative, string audioUrl)
    {
        if (string.IsNullOrWhiteSpace(affirmative))
            return Result.Failure<Phrase>(PhraseErrors.EmptyAffirmative);

        return Result.Success(new Phrase(Guid.NewGuid(), affirmative, negative, interrogative, audioUrl));
    }

    public void AddWord(Word word) => _words.Add(word);
}
