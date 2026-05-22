package br.com.faleingles.data.repository

import br.com.faleingles.domain.model.Lesson
import br.com.faleingles.domain.repository.LessonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class LessonRepositoryImpl @Inject constructor() : LessonRepository {

    override fun getLessons(): Flow<List<Lesson>> = flowOf(emptyList())

    override suspend fun getLessonById(id: String): Lesson? = null

    override suspend fun syncLessons() = Unit
}
