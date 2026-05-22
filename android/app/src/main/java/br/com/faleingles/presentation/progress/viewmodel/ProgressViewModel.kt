package br.com.faleingles.presentation.progress.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.faleingles.domain.model.Lesson
import br.com.faleingles.domain.model.UserProgress
import br.com.faleingles.domain.repository.LessonRepository
import br.com.faleingles.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class PhaseProgress(
    val phase: Int,
    val totalLessons: Int,
    val completedLessons: Int,
) {
    val percentage: Float get() = if (totalLessons == 0) 0f else completedLessons / totalLessons.toFloat()
    val isComplete: Boolean get() = completedLessons >= totalLessons && totalLessons > 0
}

data class ProgressUiState(
    val userProgress: UserProgress? = null,
    val phaseProgressList: List<PhaseProgress> = emptyList(),
    val recentlyCompleted: List<Lesson> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    progressRepository: ProgressRepository,
    lessonRepository: LessonRepository,
) : ViewModel() {

    val uiState: StateFlow<ProgressUiState> = combine(
        progressRepository.getUserProgress(),
        lessonRepository.getLessons(),
    ) { progress, lessons ->
        val phaseList = (1..5).map { phase ->
            val phaseLessons = lessons.filter { it.phase == phase }
            val completed = phaseLessons.count { it.id in progress.completedLessonIds }
            PhaseProgress(phase, phaseLessons.size, completed)
        }
        val recent = lessons
            .filter { it.id in progress.completedLessonIds }
            .takeLast(3)
            .reversed()

        ProgressUiState(
            userProgress = progress,
            phaseProgressList = phaseList,
            recentlyCompleted = recent,
            isLoading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProgressUiState())
}
