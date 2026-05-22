using FaleIngles.Domain.Common;
using MediatR;

namespace FaleIngles.Application.Progress.Commands.CompleteLesson;

public sealed record CompleteLessonCommand(string UserId, Guid LessonId) : IRequest<Result>;
