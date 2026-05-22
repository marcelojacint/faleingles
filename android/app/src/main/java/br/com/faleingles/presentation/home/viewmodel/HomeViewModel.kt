package br.com.faleingles.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.faleingles.domain.model.Lesson
import br.com.faleingles.domain.repository.ProgressRepository
import br.com.faleingles.domain.usecase.GetLessonsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val lessons: List<Lesson> = emptyList(),
    val streakDays: Int = 0,
    val todayMinutes: Int = 0,
    val goalMinutes: Int = 10,
    val isLoading: Boolean = true,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getLessons: GetLessonsUseCase,
    progressRepository: ProgressRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        getLessons(),
        progressRepository.getUserProgress(),
    ) { lessons, progress ->
        HomeUiState(
            lessons = lessons,
            streakDays = progress.currentStreakDays,
            todayMinutes = progress.todayMinutesStudied,
            goalMinutes = progress.dailyGoalMinutes,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(),
    )
}
