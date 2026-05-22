package br.com.faleingles.presentation.lesson.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.faleingles.domain.model.Phrase
import br.com.faleingles.domain.model.Word
import br.com.faleingles.presentation.lesson.viewmodel.LessonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    lessonId: String,
    onPracticeClick: () -> Unit,
    onAudioClick: () -> Unit = {},
    onBack: () -> Unit,
    viewModel: LessonViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(lessonId) { viewModel.loadLesson(lessonId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.lesson?.title ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        }
    ) { padding ->
        if (uiState.lesson == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val lesson = uiState.lesson!!
        val phrase = lesson.phrases.getOrNull(uiState.currentPhraseIndex) ?: return@Scaffold

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                SituationCard(lesson.situationContext)
            }
            item {
                PhraseCard(
                    phrase = phrase,
                    showTranslation = uiState.showTranslation,
                    selectedWord = uiState.selectedWord,
                    onPlayAudio = { viewModel.playAudio(phrase.audioUrl) },
                    onWordClick = { viewModel.selectWord(it) },
                    onToggleTranslation = { viewModel.toggleTranslation() },
                )
            }
            item {
                uiState.selectedWord?.let { word ->
                    WordDetailCard(word)
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (uiState.currentPhraseIndex < lesson.phrases.lastIndex) {
                        OutlinedButton(
                            onClick = { viewModel.nextPhrase() },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Próxima frase")
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutlinedButton(
                            onClick = onAudioClick,
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(Icons.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Áudio")
                        }
                        Button(
                            onClick = onPracticeClick,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Praticar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SituationCard(context: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Situação: $context",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Composable
private fun PhraseCard(
    phrase: Phrase,
    showTranslation: Boolean,
    selectedWord: Word?,
    onPlayAudio: () -> Unit,
    onWordClick: (Word) -> Unit,
    onToggleTranslation: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(phrase.words) { word ->
                        val isSelected = word == selectedWord
                        Box(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.small)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.surface
                                )
                                .clickable { onWordClick(word) }
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                        ) {
                            Text(
                                text = word.text,
                                style = MaterialTheme.typography.titleLarge,
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
                IconButton(onClick = onPlayAudio) {
                    Icon(
                        Icons.Filled.VolumeUp,
                        contentDescription = "Ouvir",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            if (showTranslation) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = phrase.words.joinToString(" ") { it.translation },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = onToggleTranslation) {
                Text(if (showTranslation) "Ocultar tradução" else "Ver tradução")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PhraseFormRow("Afirmativa", phrase.affirmative)
                PhraseFormRow("Negativa", phrase.negative)
                PhraseFormRow("Interrogativa", phrase.interrogative)
            }
        }
    }
}

@Composable
private fun PhraseFormRow(label: String, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(100.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun WordDetailCard(word: Word) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = word.text,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                ) {
                    Text(
                        text = word.grammaticalType.name.lowercase().replace("_", " "),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = word.translation,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = word.roleInPhrase,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
            )
        }
    }
}
