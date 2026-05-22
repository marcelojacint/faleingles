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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.faleingles.domain.model.GrammaticalType
import br.com.faleingles.domain.model.Phrase
import br.com.faleingles.domain.model.Word
import br.com.faleingles.presentation.theme.FaleInglesTheme

private val previewWords = listOf(
    Word("He", "Ele", GrammaticalType.PRONOUN, "Sujeito da frase"),
    Word("is", "está / é", GrammaticalType.VERB, "Verbo to be na 3ª pessoa"),
    Word("coding", "programando", GrammaticalType.VERB, "Gerúndio — ação em progresso"),
    Word("every", "todo", GrammaticalType.ADJECTIVE, "Determina frequência"),
    Word("single", "único / cada", GrammaticalType.ADJECTIVE, "Reforça 'every' (ênfase)"),
    Word("day", "dia", GrammaticalType.OBJECT, "Complemento de tempo"),
)

private val previewPhrase = Phrase(
    id = "p1",
    affirmative = "He is coding every single day.",
    negative = "He is not coding every single day.",
    interrogative = "Is he coding every single day?",
    audioUrl = "",
    words = previewWords,
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Lesson — sem palavra selecionada", showBackground = true, showSystemUi = true)
@Composable
private fun LessonScreenNoSelectionPreview() {
    FaleInglesTheme {
        LessonContent(
            title = "He is coding",
            situationContext = "Você está descrevendo o que um amigo programador faz todos os dias",
            phrase = previewPhrase,
            showTranslation = false,
            selectedWord = null,
            onBack = {},
            onPlayAudio = {},
            onWordClick = {},
            onToggleTranslation = {},
            onPractice = {},
            onNextPhrase = {},
            isLastPhrase = false,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Lesson — com palavra selecionada", showBackground = true, showSystemUi = true)
@Composable
private fun LessonScreenWordSelectedPreview() {
    FaleInglesTheme {
        LessonContent(
            title = "He is coding",
            situationContext = "Você está descrevendo o que um amigo programador faz todos os dias",
            phrase = previewPhrase,
            showTranslation = true,
            selectedWord = previewWords[2],
            onBack = {},
            onPlayAudio = {},
            onWordClick = {},
            onToggleTranslation = {},
            onPractice = {},
            onNextPhrase = {},
            isLastPhrase = false,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Lesson — Dark", showBackground = true, showSystemUi = true)
@Composable
private fun LessonScreenDarkPreview() {
    FaleInglesTheme(darkTheme = true) {
        LessonContent(
            title = "He is coding",
            situationContext = "Você está descrevendo o que um amigo programador faz todos os dias",
            phrase = previewPhrase,
            showTranslation = false,
            selectedWord = previewWords[1],
            onBack = {},
            onPlayAudio = {},
            onWordClick = {},
            onToggleTranslation = {},
            onPractice = {},
            onNextPhrase = {},
            isLastPhrase = false,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LessonContent(
    title: String,
    situationContext: String,
    phrase: Phrase,
    showTranslation: Boolean,
    selectedWord: Word?,
    onBack: () -> Unit,
    onPlayAudio: () -> Unit,
    onWordClick: (Word) -> Unit,
    onToggleTranslation: () -> Unit,
    onPractice: () -> Unit,
    onNextPhrase: () -> Unit,
    isLastPhrase: Boolean,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "Situação: $situationContext",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
            item {
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
                                            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface)
                                            .clickable { onWordClick(word) }
                                            .padding(horizontal = 4.dp, vertical = 2.dp),
                                    ) {
                                        Text(
                                            word.text,
                                            style = MaterialTheme.typography.titleLarge,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                }
                            }
                            IconButton(onClick = onPlayAudio) {
                                Icon(Icons.Filled.VolumeUp, null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        if (showTranslation) {
                            Spacer(Modifier.height(12.dp))
                            Text(phrase.words.joinToString(" ") { it.translation }, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                        Spacer(Modifier.height(16.dp))
                        TextButton(onClick = onToggleTranslation) { Text(if (showTranslation) "Ocultar tradução" else "Ver tradução") }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row { Text("Afirmativa", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.width(100.dp)); Text(phrase.affirmative, style = MaterialTheme.typography.bodyLarge) }
                            Row { Text("Negativa", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.width(100.dp)); Text(phrase.negative, style = MaterialTheme.typography.bodyLarge) }
                            Row { Text("Interrogativa", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.width(100.dp)); Text(phrase.interrogative, style = MaterialTheme.typography.bodyLarge) }
                        }
                    }
                }
            }
            if (selectedWord != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(selectedWord.text, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)) {
                                    Text(selectedWord.grammaticalType.name.lowercase(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(selectedWord.translation, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                            Spacer(Modifier.height(4.dp))
                            Text(selectedWord.roleInPhrase, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f))
                        }
                    }
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (!isLastPhrase) OutlinedButton(onClick = onNextPhrase, modifier = Modifier.weight(1f)) { Text("Próxima frase") }
                    Button(onClick = onPractice, modifier = Modifier.weight(1f)) { Text("Praticar") }
                }
            }
        }
    }
}
