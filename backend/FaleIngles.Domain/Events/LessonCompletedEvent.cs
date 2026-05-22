using FaleIngles.Domain.Common;

namespace FaleIngles.Domain.Events;

public sealed record LessonCompletedEvent(string UserId, string LessonId) : IDomainEvent;
