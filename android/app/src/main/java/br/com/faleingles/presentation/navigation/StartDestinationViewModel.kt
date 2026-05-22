package br.com.faleingles.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.faleingles.data.preferences.UserPreferences
import br.com.faleingles.domain.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface StartDestination {
    data object Loading : StartDestination
    data object Onboarding : StartDestination
    data object Auth : StartDestination
    data object Home : StartDestination
}

@HiltViewModel
class StartDestinationViewModel @Inject constructor(
    userPreferences: UserPreferences,
    private val authRepository: AuthRepository,
) : ViewModel() {

    val startDestination: StateFlow<StartDestination> = userPreferences.onboardingDone
        .map { done ->
            when {
                !done -> StartDestination.Onboarding
                authRepository.isSignedIn() -> StartDestination.Home
                else -> StartDestination.Auth
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StartDestination.Loading)
}
