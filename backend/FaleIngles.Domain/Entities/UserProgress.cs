using FaleIngles.Domain.Common;
using FaleIngles.Domain.Events;

namespace FaleIngles.Domain.Entities;

public sealed class UserProgress : Entity
{
    private readonly List<string> _completedLessonIds = new();
    private readonly List<string> _masteredPhraseIds = new();

    private UserProgress() : base(Guid.Empty) { }

    private UserProgress(Guid id, string userId, int dailyGoalMinutes) : base(id)
    {
        UserId = userId;
        DailyGoalMinutes = dailyGoalMinutes;
    }

    public string UserId { get; private set; }
    public int CurrentStreakDays { get; private set; }
    public int TotalMinutesStudied { get; private set; }
    public int TodayMinutesStudied { get; private set; }
    public int DailyGoalMinutes { get; private set; }
    public bool IsPro { get; private set; }
    public DateOnly LastStudyDate { get; private set; }
    public IReadOnlyCollection<string> CompletedLessonIds => _completedLessonIds.AsReadOnly();
    public IReadOnlyCollection<string> MasteredPhraseIds => _masteredPhraseIds.AsReadOnly();

    public static UserProgress Create(string userId, int dailyGoalMinutes = 10) =>
        new(Guid.NewGuid(), userId, dailyGoalMinutes);

    public void RecordStudySession(int minutes)
    {
        var today = DateOnly.FromDateTime(DateTime.UtcNow);

        if (LastStudyDate == today)
        {
            TodayMinutesStudied += minutes;
        }
        else if (LastStudyDate == today.AddDays(-1))
        {
            CurrentStreakDays++;
            TodayMinutesStudied = minutes;
        }
        else
        {
            CurrentStreakDays = 1;
            TodayMinutesStudied = minutes;
        }

        TotalMinutesStudied += minutes;
        LastStudyDate = today;
    }

    public void CompleteLesson(string lessonId)
    {
        if (_completedLessonIds.Contains(lessonId)) return;
        _completedLessonIds.Add(lessonId);
        RaiseDomainEvent(new LessonCompletedEvent(UserId, lessonId));
    }

    public void ActivatePro() => IsPro = true;

    public void DeactivatePro() => IsPro = false;
}
