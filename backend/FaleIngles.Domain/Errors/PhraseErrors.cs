using FaleIngles.Domain.Common;

namespace FaleIngles.Domain.Errors;

public static class PhraseErrors
{
    public static readonly Error EmptyAffirmative = Error.Validation("Phrase.EmptyAffirmative", "Affirmative form cannot be empty.");
    public static readonly Error NotFound = Error.NotFound("Phrase.NotFound", "The phrase was not found.");
}
