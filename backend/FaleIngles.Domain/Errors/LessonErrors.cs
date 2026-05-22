using FaleIngles.Domain.Common;

namespace FaleIngles.Domain.Errors;

public static class LessonErrors
{
    public static readonly Error InvalidPhase = Error.Validation("Lesson.InvalidPhase", "Phase must be between 1 and 5.");
    public static readonly Error EmptyTitle = Error.Validation("Lesson.EmptyTitle", "Title cannot be empty.");
    public static readonly Error EmptySituationContext = Error.Validation("Lesson.EmptySituationContext", "Situation context cannot be empty.");
    public static readonly Error NotFound = Error.NotFound("Lesson.NotFound", "The lesson was not found.");
    public static readonly Error Locked = Error.Forbidden("Lesson.Locked", "This lesson requires an active Pro subscription.");
}
