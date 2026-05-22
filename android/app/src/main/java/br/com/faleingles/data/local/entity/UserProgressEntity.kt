package br.com.faleingles.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val userId: String,
    val currentStreakDays: Int = 0,
    val totalMinutesStudied: Int = 0,
    val todayMinutesStudied: Int = 0,
    val dailyGoalMinutes: Int = 10,
    val isPro: Boolean = false,
    val lastStudyDateEpochDay: Long = 0,
    val completedLessonIds: String = "",
    val masteredPhraseIds: String = "",
)
