package br.com.faleingles.presentation.auth.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.faleingles.data.preferences.UserPreferences
import br.com.faleingles.domain.auth.AuthRepository
import br.com.faleingles.domain.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isSignUp: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, error = null) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, error = null) }
    fun toggleMode() = _uiState.update { it.copy(isSignUp = !it.isSignUp, error = null) }

    fun signInWithGoogle(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.signInWithGoogle(context)) {
                is AuthResult.Success -> {
                    persistUser(result.userId)
                    onSuccess()
                }
                is AuthResult.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }

    fun signInWithEmail(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (!validate(state.email, state.password)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = if (state.isSignUp)
                authRepository.signUpWithEmail(state.email, state.password)
            else
                authRepository.signInWithEmail(state.email, state.password)

            when (result) {
                is AuthResult.Success -> {
                    persistUser(result.userId)
                    onSuccess()
                }
                is AuthResult.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }

    private suspend fun persistUser(userId: String) {
        val token = authRepository.getIdToken()
        userPreferences.saveUserId(userId)
        if (token != null) userPreferences.saveAuthToken(token)
        _uiState.update { it.copy(isLoading = false) }
    }

    private fun validate(email: String, password: String): Boolean {
        return when {
            email.isBlank() -> { _uiState.update { it.copy(error = "Informe seu e-mail") }; false }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _uiState.update { it.copy(error = "E-mail inválido") }; false
            }
            password.length < 6 -> { _uiState.update { it.copy(error = "Senha deve ter ao menos 6 caracteres") }; false }
            else -> true
        }
    }
}
