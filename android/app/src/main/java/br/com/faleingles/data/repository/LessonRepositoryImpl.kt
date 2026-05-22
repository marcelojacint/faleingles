package br.com.faleingles.data.repository

import br.com.faleingles.data.local.dao.LessonDao
import br.com.faleingles.data.local.mapper.toDomain
import br.com.faleingles.data.local.mapper.toEntity
import br.com.faleingles.data.preferences.UserPreferences
import br.com.faleingles.domain.model.Lesson
import br.com.faleingles.domain.repository.LessonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class LessonRepositoryImpl @Inject constructor(
    private val lessonDao: LessonDao,
    private val userPreferences: UserPreferences,
) : LessonRepository {

    override fun getLessons(): Flow<List<Lesson>> =
        combine(
            lessonDao.observeAll(),
            userPreferences.isPro,
        ) { lessons, isPro ->
            lessons.map { it.toDomain(isPro) }
        }

    override suspend fun getLessonById(id: String): Lesson? {
        val isPro = userPreferences.isProSnapshot()
        return lessonDao.getById(id)?.toDomain(isPro)
    }

    override suspend fun syncLessons() {
        // Chamado pela API quando backend responde — implementado no ApiLessonRepository
    }

    suspend fun saveLessons(lessons: List<Lesson>) {
        val entities = lessons.map { it.toEntity() }
        val phraseEntities = lessons.flatMap { lesson ->
            lesson.phrases.mapIndexed { idx, p -> p.toEntity(lesson.id, idx) }
        }
        val wordEntities = lessons.flatMap { lesson ->
            lesson.phrases.flatMap { phrase ->
                phrase.words.mapIndexed { idx, w -> w.toEntity(phrase.id, idx) }
            }
        }
        lessonDao.upsertLessons(entities)
        lessonDao.upsertPhrases(phraseEntities)
        lessonDao.upsertWords(wordEntities)
    }
}
