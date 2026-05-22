package br.com.faleingles.presentation.lesson.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.faleingles.domain.model.Lesson
import br.com.faleingles.domain.model.Word
import br.com.faleingles.domain.repository.LessonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LessonUiState(
    val lesson: Lesson? = null,
    val currentPhraseIndex: Int = 0,
    val showTranslation: Boolean = false,
    val selectedWord: Word? = null,
    val isLoading: Boolean = true,
)

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

    fun loadLesson(lessonId: String) {
        viewModelScope.launch {
            val lesson = lessonRepository.getLessonById(lessonId)
            _uiState.update { it.copy(lesson = lesson, isLoading = false) }
        }
    }

    fun nextPhrase() {
        _uiState.update { state ->
            val nextIndex = (state.currentPhraseIndex + 1)
                .coerceAtMost(state.lesson?.phrases?.lastIndex ?: 0)
            state.copy(
                currentPhraseIndex = nextIndex,
                showTranslation = false,
                selectedWord = null,
            )
        }
    }

    fun toggleTranslation() {
        _uiState.update { it.copy(showTranslation = !it.showTranslation) }
    }

    fun selectWord(word: Word) {
        _uiState.update { state ->
            val newSelection = if (state.selectedWord == word) null else word
            state.copy(selectedWord = newSelection)
        }
    }

    fun playAudio(audioUrl: String) {
        // Delegado ao AudioPlayer (injetado futuramente)
    }
}
