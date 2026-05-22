package br.com.faleingles.presentation.review.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.faleingles.domain.model.Lesson
import br.com.faleingles.domain.model.Phrase
import br.com.faleingles.domain.model.PracticeResult
import br.com.faleingles.domain.repository.LessonRepository
import br.com.faleingles.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ReviewCardState { QUESTION, REVEALED }

data class ReviewCard(
    val phrase: Phrase,
    val lessonTitle: String,
)

data class ReviewUiState(
    val cards: List<ReviewCard> = emptyList(),
    val currentIndex: Int = 0,
    val cardState: ReviewCardState = ReviewCardState.QUESTION,
    val easyCount: Int = 0,
    val hardCount: Int = 0,
    val isLoading: Boolean = true,
    val isEmpty: Boolean = false,
    val isFinished: Boolean = false,
)

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val lessonRepository: LessonRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    init {
        loadReview()
    }

    private fun loadReview() {
        viewModelScope.launch {
            val phraseIds = progressRepository.getPhrasesForReview()
            if (phraseIds.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, isEmpty = true) }
                return@launch
            }

            val lessons = lessonRepository.getLessons().first()
            val cards = buildCards(phraseIds, lessons)

            _uiState.update {
                it.copy(cards = cards.shuffled(), isLoading = false, isEmpty = cards.isEmpty())
            }
        }
    }

    private fun buildCards(phraseIds: List<String>, lessons: List<Lesson>): List<ReviewCard> {
        val cards = mutableListOf<ReviewCard>()
        lessons.forEach { lesson ->
            lesson.phrases.forEach { phrase ->
                if (phrase.id in phraseIds) {
                    cards.add(ReviewCard(phrase, lesson.title))
                }
            }
        }
        return cards
    }

    fun revealCard() {
        _uiState.update { it.copy(cardState = ReviewCardState.REVEALED) }
    }

    fun markEasy() = recordAndAdvance(isCorrect = true, isEasy = true)
    fun markHard() = recordAndAdvance(isCorrect = true, isEasy = false)
    fun markAgain() = recordAndAdvance(isCorrect = false, isEasy = false)

    private fun recordAndAdvance(isCorrect: Boolean, isEasy: Boolean) {
        val state = _uiState.value
        val card = state.cards.getOrNull(state.currentIndex) ?: return

        viewModelScope.launch {
            progressRepository.recordPracticeResult(
                PracticeResult(
                    phraseId = card.phrase.id,
                    isCorrect = isCorrect,
                    timeTakenMillis = 0,
                    nextReviewDate = 0,
                    easeFactor = if (isEasy) 3f else 2f,
                )
            )
        }

        val next = state.currentIndex + 1
        _uiState.update {
            it.copy(
                currentIndex = next,
                cardState = ReviewCardState.QUESTION,
                easyCount = if (isEasy) it.easyCount + 1 else it.easyCount,
                hardCount = if (!isEasy && isCorrect) it.hardCount + 1 else it.hardCount,
                isFinished = next >= it.cards.size,
            )
        }
    }
}
