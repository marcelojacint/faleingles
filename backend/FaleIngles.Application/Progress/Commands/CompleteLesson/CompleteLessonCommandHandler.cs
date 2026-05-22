using FaleIngles.Domain.Common;
using FaleIngles.Domain.Errors;
using FaleIngles.Domain.Interfaces;
using MediatR;

namespace FaleIngles.Application.Progress.Commands.CompleteLesson;

internal sealed class CompleteLessonCommandHandler : IRequestHandler<CompleteLessonCommand, Result>
{
    private readonly IUserProgressRepository _progressRepository;
    private readonly ILessonRepository _lessonRepository;

    public CompleteLessonCommandHandler(IUserProgressRepository progressRepository, ILessonRepository lessonRepository)
    {
        _progressRepository = progressRepository;
        _lessonRepository = lessonRepository;
    }

    public async Task<Result> Handle(CompleteLessonCommand request, CancellationToken cancellationToken)
    {
        var lesson = await _lessonRepository.GetByIdAsync(request.LessonId, cancellationToken);
        if (lesson is null) return Result.Failure(LessonErrors.NotFound);

        var progress = await _progressRepository.GetByUserIdAsync(request.UserId, cancellationToken);
        if (progress is null) return Result.Failure(UserErrors.NotFound);

        progress.CompleteLesson(request.LessonId.ToString());
        progress.RecordStudySession(minutes: 10);

        _progressRepository.Update(progress);
        return Result.Success();
    }
}
