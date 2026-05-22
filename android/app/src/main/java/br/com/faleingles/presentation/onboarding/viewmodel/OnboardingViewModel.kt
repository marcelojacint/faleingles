package br.com.faleingles.presentation.onboarding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.faleingles.data.preferences.UserPreferences
import br.com.faleingles.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class OnboardingUiState(
    val selectedLevelIndex: Int = -1,
    val selectedGoalMinutes: Int = 10,
    val isSaving: Boolean = false,
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun selectLevel(index: Int) = _uiState.update { it.copy(selectedLevelIndex = index) }

    fun selectGoal(minutes: Int) = _uiState.update { it.copy(selectedGoalMinutes = minutes) }

    fun completeOnboarding(onDone: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val userId = UUID.randomUUID().toString()
            userPreferences.saveUserId(userId)
            userPreferences.saveDailyGoal(_uiState.value.selectedGoalMinutes)
            userPreferences.markOnboardingDone()

            _uiState.update { it.copy(isSaving = false) }
            onDone()
        }
    }
}
