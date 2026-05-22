using FaleIngles.Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace FaleIngles.Infrastructure.Persistence.Configurations;

public sealed class UserProgressConfiguration : IEntityTypeConfiguration<UserProgress>
{
    public void Configure(EntityTypeBuilder<UserProgress> builder)
    {
        builder.ToTable("user_progresses");
        builder.HasKey(u => u.Id);
        builder.Property(u => u.Id).HasColumnName("id");
        builder.Property(u => u.UserId).HasColumnName("user_id").HasMaxLength(200).IsRequired();
        builder.HasIndex(u => u.UserId).IsUnique();
        builder.Property(u => u.CurrentStreakDays).HasColumnName("current_streak_days");
        builder.Property(u => u.TotalMinutesStudied).HasColumnName("total_minutes_studied");
        builder.Property(u => u.TodayMinutesStudied).HasColumnName("today_minutes_studied");
        builder.Property(u => u.DailyGoalMinutes).HasColumnName("daily_goal_minutes");
        builder.Property(u => u.IsPro).HasColumnName("is_pro");
        builder.Property(u => u.LastStudyDate).HasColumnName("last_study_date");

        builder.Property(u => u.CompletedLessonIds)
               .HasColumnName("completed_lesson_ids")
               .HasConversion(
                   ids => string.Join(',', ids),
                   raw => raw.Split(',', StringSplitOptions.RemoveEmptyEntries).ToList().AsReadOnly()
               );

        builder.Property(u => u.MasteredPhraseIds)
               .HasColumnName("mastered_phrase_ids")
               .HasConversion(
                   ids => string.Join(',', ids),
                   raw => raw.Split(',', StringSplitOptions.RemoveEmptyEntries).ToList().AsReadOnly()
               );
    }
}
