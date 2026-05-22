package br.com.faleingles.domain.usecase

import br.com.faleingles.domain.model.Lesson
import br.com.faleingles.domain.repository.LessonRepository
import br.com.faleingles.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetLessonsUseCase @Inject constructor(
    private val lessonRepository: LessonRepository,
    private val progressRepository: ProgressRepository,
) {
    operator fun invoke(): Flow<List<Lesson>> =
        combine(
            lessonRepository.getLessons(),
            progressRepository.getUserProgress(),
        ) { lessons, progress ->
            lessons.map { lesson ->
                lesson.copy(
                    isUnlocked = !lesson.isPremium || progress.isPro ||
                            lesson.orderInPhase <= 3 && lesson.phase == 1
                )
            }
        }
}
