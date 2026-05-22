package br.com.faleingles.presentation.practice.screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.faleingles.presentation.practice.viewmodel.PracticeResult
import br.com.faleingles.presentation.practice.viewmodel.PracticeUiState
import br.com.faleingles.presentation.practice.viewmodel.PracticeViewModel
import br.com.faleingles.presentation.practice.viewmodel.WordSlot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    lessonId: String,
    onFinish: () -> Unit,
    viewModel: PracticeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(lessonId) { viewModel.loadLesson(lessonId) }

    if (state.isFinished) {
        PracticeFinishScreen(
            correct = state.correctCount,
            total = state.totalExercises,
            onFinish = onFinish,
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.lessonTitle) },
                navigationIcon = {
                    IconButton(onClick = onFinish) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Sair")
                    }
                },
                actions = {
                    if (state.hintsUsed < 3 && state.result == PracticeResult.NONE) {
                        TextButton(onClick = { viewModel.useHint() }) {
                            Text("💡 Dica (${3 - state.hintsUsed})")
                        }
                    }
                },
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ProgressHeader(state)
                InstructionCard()
                AnswerSlots(
                    placedWords = state.placedWords,
                    result = state.result,
                    onRemove = { viewModel.removeWord(it) },
                )
                ResultFeedback(state.result, state.targetPhrase)
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                WordBank(
                    words = state.wordBank,
                    enabled = state.result == PracticeResult.NONE,
                    onPlace = { viewModel.placeWord(it) },
                )

                when (state.result) {
                    PracticeResult.NONE -> {
                        Button(
                            onClick = { viewModel.checkAnswer() },
                            enabled = state.placedWords.none { it == null },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = MaterialTheme.shapes.medium,
                        ) { Text("Verificar", style = MaterialTheme.typography.labelLarge) }
                    }
                    PracticeResult.CORRECT, PracticeResult.WRONG -> {
                        Button(
                            onClick = { viewModel.nextExercise() },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Text(
                                if (state.currentExerciseIndex + 1 >= state.totalExercises) "Ver resultado" else "Próxima frase",
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ProgressHeader(state: PracticeUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                "Frase ${state.currentExerciseIndex + 1} de ${state.totalExercises}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                "✓ ${state.correctCount} acertos",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
        LinearProgressIndicator(
            progress = { (state.currentExerciseIndex + 1f) / state.totalExercises.coerceAtLeast(1) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun InstructionCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        Text(
            text = "Monte a frase na ordem correta tocando nas palavras",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        )
    }
}

@Composable
private fun AnswerSlots(
    placedWords: List<WordSlot?>,
    result: PracticeResult,
    onRemove: (Int) -> Unit,
) {
    val borderColor = when (result) {
        PracticeResult.CORRECT -> MaterialTheme.colorScheme.secondary
        PracticeResult.WRONG -> MaterialTheme.colorScheme.error
        PracticeResult.NONE -> MaterialTheme.colorScheme.outlineVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 80.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(borderColor),
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 80.dp)
                .padding(12.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (placedWords.all { it == null }) {
                Text(
                    "Toque nas palavras abaixo para montar a frase",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center,
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    placedWords.forEachIndexed { index, slot ->
                        if (slot != null) {
                            WordChip(
                                word = slot.word,
                                style = WordChipStyle.PLACED,
                                enabled = result == PracticeResult.NONE,
                                onClick = { onRemove(index) },
                            )
                        } else {
                            EmptySlot()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultFeedback(result: PracticeResult, targetPhrase: String) {
    AnimatedVisibility(
        visible = result != PracticeResult.NONE,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (result == PracticeResult.CORRECT)
                    MaterialTheme.colorScheme.secondaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer,
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    if (result == PracticeResult.CORRECT) "✅" else "❌",
                    style = MaterialTheme.typography.headlineMedium,
                )
                Column {
                    Text(
                        if (result == PracticeResult.CORRECT) "Correto!" else "Quase lá!",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (result == PracticeResult.CORRECT)
                            MaterialTheme.colorScheme.onSecondaryContainer
                        else
                            MaterialTheme.colorScheme.onErrorContainer,
                    )
                    if (result == PracticeResult.WRONG) {
                        Text(
                            "Resposta: $targetPhrase",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WordBank(
    words: List<WordSlot>,
    enabled: Boolean,
    onPlace: (WordSlot) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Palavras disponíveis",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            ),
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            ) {
                words.forEach { slot ->
                    WordChip(
                        word = slot.word,
                        style = WordChipStyle.BANK,
                        enabled = enabled,
                        onClick = { onPlace(slot) },
                    )
                }
            }
        }
    }
}

enum class WordChipStyle { BANK, PLACED }

@Composable
private fun WordChip(
    word: String,
    style: WordChipStyle,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val containerColor = when (style) {
        WordChipStyle.BANK -> MaterialTheme.colorScheme.primary
        WordChipStyle.PLACED -> MaterialTheme.colorScheme.primaryContainer
    }
    val contentColor = when (style) {
        WordChipStyle.BANK -> MaterialTheme.colorScheme.onPrimary
        WordChipStyle.PLACED -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(containerColor.copy(alpha = if (enabled) 1f else 0.5f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = word,
            style = MaterialTheme.typography.titleMedium,
            color = contentColor,
        )
    }
}

@Composable
private fun EmptySlot() {
    Box(
        modifier = Modifier
            .height(40.dp)
            .width(64.dp)
            .clip(MaterialTheme.shapes.small)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant,
                MaterialTheme.shapes.small,
            ),
    )
}

@Composable
private fun PracticeFinishScreen(correct: Int, total: Int, onFinish: () -> Unit) {
    val pct = if (total > 0) correct * 100 / total else 0
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(if (pct >= 80) "🎉" else "💪", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(16.dp))
        Text(
            if (pct == 100) "Perfeito!" else if (pct >= 80) "Muito bem!" else "Continue praticando!",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "$correct de $total frases corretas ($pct%)",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            Text("Concluir lição", style = MaterialTheme.typography.labelLarge)
        }
    }
}
