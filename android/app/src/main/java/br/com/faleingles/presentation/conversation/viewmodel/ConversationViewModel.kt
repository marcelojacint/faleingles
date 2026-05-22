package br.com.faleingles.presentation.conversation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.faleingles.domain.model.ConversationMessage
import br.com.faleingles.domain.model.MessageRole
import br.com.faleingles.domain.usecase.SendConversationMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ConversationUiState(
    val messages: List<ConversationMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

private const val DEFAULT_SCENARIO_ID = "free_conversation"

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val sendMessage: SendConversationMessageUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConversationUiState())
    val uiState: StateFlow<ConversationUiState> = _uiState.asStateFlow()

    fun sendMessage(text: String) {
        val userMessage = ConversationMessage(
            id = UUID.randomUUID().toString(),
            role = MessageRole.USER,
            content = text,
            timestampMillis = System.currentTimeMillis(),
        )

        _uiState.update { it.copy(messages = it.messages + userMessage, isLoading = true) }

        viewModelScope.launch {
            sendMessage(
                scenarioId = DEFAULT_SCENARIO_ID,
                history = _uiState.value.messages,
                userMessage = text,
            ).onSuccess { aiMessage ->
                _uiState.update {
                    it.copy(messages = it.messages + aiMessage, isLoading = false)
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, error = error.message)
                }
            }
        }
    }
}
