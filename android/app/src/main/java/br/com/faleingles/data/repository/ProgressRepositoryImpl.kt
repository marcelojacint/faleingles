package br.com.faleingles.data.repository

import br.com.faleingles.data.local.dao.PhraseReviewDao
import br.com.faleingles.data.local.dao.UserProgressDao
import br.com.faleingles.data.local.entity.PhraseReviewEntity
import br.com.faleingles.data.local.entity.UserProgressEntity
import br.com.faleingles.data.preferences.UserPreferences
import br.com.faleingles.domain.model.PracticeResult
import br.com.faleingles.domain.model.UserProgress
import br.com.faleingles.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class ProgressRepositoryImpl @Inject constructor(
    private val progressDao: UserProgressDao,
    private val reviewDao: PhraseReviewDao,
    private val userPreferences: UserPreferences,
) : ProgressRepository {

    override fun getUserProgress(): Flow<UserProgress> =
        combine(
            userPreferences.userId,
            userPreferences.isPro,
        ) { userId, _ -> userId }
            .filterNotNull()
            .map { userId ->
                val entity = progressDao.get(userId) ?: createDefault(userId)
                entity.toDomain()
            }

    override suspend fun recordPracticeResult(result: PracticeResult) {
        val userId = userPreferences.userIdSnapshot() ?: return
        val today = LocalDate.now().toEpochDay()

        val existing = reviewDao.getDueForReview(today)
            .firstOrNull { it.phraseId == result.phraseId }

        val (newInterval, newEase, newReps) = sm2(
            quality = if (result.isCorrect) 4 else 1,
            easeFactor = existing?.easeFactor ?: 2.5f,
            interval = existing?.interval ?: 1,
            repetitions = existing?.repetitions ?: 0,
        )

        reviewDao.upsert(
            PhraseReviewEntity(
                phraseId = result.phraseId,
                easeFactor = newEase,
                interval = newInterval,
                repetitions = newReps,
                nextReviewEpochDay = today + newInterval,
                lastReviewedAt = System.currentTimeMillis(),
            )
        )
    }

    override suspend fun completeLesson(lessonId: String) {
        val userId = userPreferences.userIdSnapshot() ?: return
        val entity = progressDao.get(userId) ?: createDefault(userId)
        val today = LocalDate.now().toEpochDay()

        val alreadyCompleted = entity.completedLessonIds
            .split(",")
            .filter { it.isNotBlank() }
            .contains(lessonId)
        if (alreadyCompleted) return

        val newCompleted = (entity.completedLessonIds
            .split(",")
            .filter { it.isNotBlank() } + lessonId)
            .joinToString(",")

        val streakDays = when {
            entity.lastStudyDateEpochDay == today -> entity.currentStreakDays
            entity.lastStudyDateEpochDay == today - 1 -> entity.currentStreakDays + 1
            else -> 1
        }

        progressDao.upsert(
            entity.copy(
                completedLessonIds = newCompleted,
                currentStreakDays = streakDays,
                lastStudyDateEpochDay = today,
                todayMinutesStudied = entity.todayMinutesStudied + 10,
                totalMinutesStudied = entity.totalMinutesStudied + 10,
            )
        )
    }

    override suspend fun getPhrasesForReview(): List<String> {
        val today = LocalDate.now().toEpochDay()
        return reviewDao.getDueForReview(today).map { it.phraseId }
    }

    private suspend fun createDefault(userId: String): UserProgressEntity {
        val entity = UserProgressEntity(userId = userId)
        progressDao.upsert(entity)
        return entity
    }

    private fun UserProgressEntity.toDomain() = UserProgress(
        userId = userId,
        completedLessonIds = completedLessonIds.split(",").filter { it.isNotBlank() }.toSet(),
        masteredPhraseIds = masteredPhraseIds.split(",").filter { it.isNotBlank() }.toSet(),
        currentStreakDays = currentStreakDays,
        totalMinutesStudied = totalMinutesStudied,
        isPro = isPro,
        dailyGoalMinutes = dailyGoalMinutes,
        todayMinutesStudied = todayMinutesStudied,
    )

    private fun sm2(quality: Int, easeFactor: Float, interval: Int, repetitions: Int): Triple<Int, Float, Int> {
        val newEase = (easeFactor + 0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f)).coerceAtLeast(1.3f)
        val newReps = if (quality >= 3) repetitions + 1 else 0
        val newInterval = when {
            newReps == 0 -> 1
            newReps == 1 -> 1
            newReps == 2 -> 6
            else -> (interval * newEase).toInt().coerceAtLeast(1)
        }
        return Triple(newInterval, newEase, newReps)
    }
}
