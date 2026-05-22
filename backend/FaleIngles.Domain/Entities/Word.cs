using FaleIngles.Domain.Enums;

namespace FaleIngles.Domain.Entities;

public sealed class Word
{
    private Word() { }

    public Word(string text, string translation, GrammaticalType grammaticalType, string roleInPhrase)
    {
        Text = text;
        Translation = translation;
        GrammaticalType = grammaticalType;
        RoleInPhrase = roleInPhrase;
    }

    public string Text { get; private set; }
    public string Translation { get; private set; }
    public GrammaticalType GrammaticalType { get; private set; }
    public string RoleInPhrase { get; private set; }
}
