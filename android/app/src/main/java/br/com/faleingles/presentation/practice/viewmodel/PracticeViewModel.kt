package br.com.faleingles.presentation.practice.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.faleingles.domain.repository.LessonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WordSlot(val word: String, val id: Int)

enum class PracticeResult { NONE, CORRECT, WRONG }

data class PracticeUiState(
    val lessonTitle: String = "",
    val currentExerciseIndex: Int = 0,
    val totalExercises: Int = 0,
    val targetPhrase: String = "",
    val targetWords: List<WordSlot> = emptyList(),
    val wordBank: List<WordSlot> = emptyList(),
    val placedWords: List<WordSlot?> = emptyList(),
    val result: PracticeResult = PracticeResult.NONE,
    val hintsUsed: Int = 0,
    val correctCount: Int = 0,
    val isFinished: Boolean = false,
    val isLoading: Boolean = true,
)

data class Exercise(
    val phrase: String,
    val words: List<String>,
    val distractors: List<String>,
)

@HiltViewModel
class PracticeViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PracticeUiState())
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()

    private var exercises: List<Exercise> = emptyList()
    private var wordIdCounter = 0

    fun loadLesson(lessonId: String) {
        viewModelScope.launch {
            val lesson = lessonRepository.getLessonById(lessonId)
            if (lesson == null) return@launch

            exercises = lesson.phrases.map { phrase ->
                val words = phrase.affirmative.trimEnd('.').split(" ")
                val distractors = generateDistractors(words)
                Exercise(phrase.affirmative, words, distractors)
            }

            _uiState.update {
                it.copy(
                    lessonTitle = lesson.title,
                    totalExercises = exercises.size,
                    isLoading = false,
                )
            }
            loadExercise(0)
        }
    }

    private fun loadExercise(index: Int) {
        if (index >= exercises.size) {
            _uiState.update { it.copy(isFinished = true) }
            return
        }

        val exercise = exercises[index]
        val targetSlots = exercise.words.mapIndexed { i, w -> WordSlot(w, ++wordIdCounter * 100 + i) }
        val bankWords = (exercise.words + exercise.distractors)
            .shuffled()
            .mapIndexed { i, w -> WordSlot(w, ++wordIdCounter * 100 + i) }

        _uiState.update {
            it.copy(
                currentExerciseIndex = index,
                targetPhrase = exercise.phrase,
                targetWords = targetSlots,
                wordBank = bankWords,
                placedWords = List(exercise.words.size) { null },
                result = PracticeResult.NONE,
                hintsUsed = 0,
            )
        }
    }

    fun placeWord(slot: WordSlot) {
        val state = _uiState.value
        if (state.result != PracticeResult.NONE) return

        val firstEmpty = state.placedWords.indexOfFirst { it == null }
        if (firstEmpty == -1) return

        _uiState.update {
            it.copy(
                wordBank = it.wordBank - slot,
                placedWords = it.placedWords.toMutableList().also { list -> list[firstEmpty] = slot },
            )
        }
    }

    fun removeWord(index: Int) {
        val state = _uiState.value
        if (state.result != PracticeResult.NONE) return
        val removed = state.placedWords[index] ?: return

        _uiState.update {
            it.copy(
                wordBank = it.wordBank + removed,
                placedWords = it.placedWords.toMutableList().also { list -> list[index] = null },
            )
        }
    }

    fun checkAnswer() {
        val state = _uiState.value
        if (state.placedWords.any { it == null }) return

        val answer = state.placedWords.mapNotNull { it?.word }
        val correct = exercises[state.currentExerciseIndex].words

        val isCorrect = answer == correct
        _uiState.update {
            it.copy(
                result = if (isCorrect) PracticeResult.CORRECT else PracticeResult.WRONG,
                correctCount = if (isCorrect) it.correctCount + 1 else it.correctCount,
            )
        }
    }

    fun nextExercise() {
        val next = _uiState.value.currentExerciseIndex + 1
        loadExercise(next)
    }

    fun useHint() {
        val state = _uiState.value
        if (state.hintsUsed >= 3 || state.result != PracticeResult.NONE) return

        val exercise = exercises[state.currentExerciseIndex]
        val firstEmpty = state.placedWords.indexOfFirst { it == null }
        if (firstEmpty == -1) return

        val correctWord = exercise.words[firstEmpty]
        val wordInBank = state.wordBank.firstOrNull { it.word == correctWord } ?: return

        _uiState.update {
            it.copy(
                wordBank = it.wordBank - wordInBank,
                placedWords = it.placedWords.toMutableList().also { list -> list[firstEmpty] = wordInBank },
                hintsUsed = it.hintsUsed + 1,
            )
        }
    }

    private fun generateDistractors(words: List<String>): List<String> {
        val pool = listOf("the", "a", "is", "are", "was", "not", "very", "really", "always", "never", "every", "some")
        return pool.filter { it !in words }.shuffled().take(3)
    }
}
