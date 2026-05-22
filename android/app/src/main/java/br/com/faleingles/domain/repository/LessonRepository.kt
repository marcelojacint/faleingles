package br.com.faleingles.domain.repository

import br.com.faleingles.domain.model.Lesson
import kotlinx.coroutines.flow.Flow

interface LessonRepository {
    fun getLessons(): Flow<List<Lesson>>
    suspend fun getLessonById(id: String): Lesson?
    suspend fun syncLessons()
}
