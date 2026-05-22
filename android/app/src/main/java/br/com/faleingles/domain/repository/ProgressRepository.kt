package br.com.faleingles.domain.repository

import br.com.faleingles.domain.model.PracticeResult
import br.com.faleingles.domain.model.UserProgress
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun getUserProgress(): Flow<UserProgress>
    suspend fun recordPracticeResult(result: PracticeResult)
    suspend fun completeLesson(lessonId: String)
    suspend fun getPhrasesForReview(): List<String>
}
