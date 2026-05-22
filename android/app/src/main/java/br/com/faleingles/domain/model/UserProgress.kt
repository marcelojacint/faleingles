package br.com.faleingles.domain.model

data class UserProgress(
    val userId: String,
    val completedLessonIds: Set<String>,
    val masteredPhraseIds: Set<String>,
    val currentStreakDays: Int,
    val totalMinutesStudied: Int,
    val isPro: Boolean,
    val dailyGoalMinutes: Int,
    val todayMinutesStudied: Int,
)

data class PracticeResult(
    val phraseId: String,
    val isCorrect: Boolean,
    val timeTakenMillis: Long,
    val nextReviewDate: Long,
    val easeFactor: Float,
)
