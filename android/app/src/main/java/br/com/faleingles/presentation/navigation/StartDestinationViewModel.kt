package br.com.faleingles.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.faleingles.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface StartDestination {
    data object Loading : StartDestination
    data object Onboarding : StartDestination
    data object Home : StartDestination
}

@HiltViewModel
class StartDestinationViewModel @Inject constructor(
    userPreferences: UserPreferences,
) : ViewModel() {

    val startDestination: StateFlow<StartDestination> = userPreferences.onboardingDone
        .map { done ->
            if (done) StartDestination.Home else StartDestination.Onboarding
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StartDestination.Loading)
}
