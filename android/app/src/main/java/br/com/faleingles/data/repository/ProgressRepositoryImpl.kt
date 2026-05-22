package br.com.faleingles.data.repository

import br.com.faleingles.domain.model.PracticeResult
import br.com.faleingles.domain.model.UserProgress
import br.com.faleingles.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ProgressRepositoryImpl @Inject constructor() : ProgressRepository {

    override fun getUserProgress(): Flow<UserProgress> = flowOf(
        UserProgress(
            userId = "local",
            completedLessonIds = emptySet(),
            masteredPhraseIds = emptySet(),
            currentStreakDays = 0,
            totalMinutesStudied = 0,
            isPro = false,
            dailyGoalMinutes = 10,
            todayMinutesStudied = 0,
        )
    )

    override suspend fun recordPracticeResult(result: PracticeResult) = Unit

    override suspend fun completeLesson(lessonId: String) = Unit

    override suspend fun getPhrasesForReview(): List<String> = emptyList()
}
