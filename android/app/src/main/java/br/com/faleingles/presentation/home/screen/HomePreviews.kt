package br.com.faleingles.presentation.home.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.faleingles.domain.model.Lesson
import br.com.faleingles.domain.model.Phrase
import br.com.faleingles.presentation.theme.FaleInglesTheme

private val previewLessons = listOf(
    Lesson(
        id = "1", phase = 1, orderInPhase = 1,
        title = "Meeting someone new",
        situationContext = "Você acaba de chegar em uma festa",
        phrases = emptyList(), isUnlocked = true, isPremium = false,
    ),
    Lesson(
        id = "2", phase = 1, orderInPhase = 2,
        title = "Talking about yourself",
        situationContext = "Alguém te pergunta quem você é",
        phrases = emptyList(), isUnlocked = true, isPremium = false,
    ),
    Lesson(
        id = "3", phase = 1, orderInPhase = 3,
        title = "How are you?",
        situationContext = "Cumprimento casual com um colega",
        phrases = emptyList(), isUnlocked = true, isPremium = false,
    ),
    Lesson(
        id = "4", phase = 1, orderInPhase = 4,
        title = "Where are you from?",
        situationContext = "Conversa inicial com estrangeiro",
        phrases = emptyList(), isUnlocked = false, isPremium = true,
    ),
    Lesson(
        id = "5", phase = 2, orderInPhase = 1,
        title = "There is something here",
        situationContext = "Descrevendo o ambiente ao redor",
        phrases = emptyList(), isUnlocked = false, isPremium = true,
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Home — Light", showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    FaleInglesTheme(darkTheme = false) {
        HomeScreenContent(
            lessons = previewLessons,
            streakDays = 7,
            todayMinutes = 8,
            goalMinutes = 10,
            onLessonClick = {},
            onConversationClick = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Home — Dark", showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenDarkPreview() {
    FaleInglesTheme(darkTheme = true) {
        HomeScreenContent(
            lessons = previewLessons,
            streakDays = 21,
            todayMinutes = 10,
            goalMinutes = 10,
            onLessonClick = {},
            onConversationClick = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreenContent(
    lessons: List<Lesson>,
    streakDays: Int,
    todayMinutes: Int,
    goalMinutes: Int,
    onLessonClick: (String) -> Unit,
    onConversationClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FaleInglês", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = onConversationClick) {
                        Icon(Icons.Outlined.Chat, contentDescription = "Conversar com IA")
                    }
                },
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text(
                                "$streakDays dias seguidos",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                            Text(
                                "$todayMinutes / $goalMinutes min hoje",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                            )
                        }
                        Text("🔥", style = MaterialTheme.typography.displayMedium)
                    }
                }
            }
            item {
                Text("Suas lições", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 8.dp))
            }
            items(lessons) { lesson ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { if (lesson.isUnlocked) onLessonClick(lesson.id) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (lesson.isUnlocked) MaterialTheme.colorScheme.surface
                        else MaterialTheme.colorScheme.surfaceVariant,
                    ),
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(48.dp).clip(MaterialTheme.shapes.medium)
                                .background(if (lesson.isUnlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = if (lesson.isUnlocked) Icons.Filled.PlayArrow else Icons.Filled.Lock,
                                contentDescription = null,
                                tint = if (lesson.isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Fase ${lesson.phase} · Lição ${lesson.orderInPhase}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Text(lesson.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(lesson.situationContext, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }
    }
}
