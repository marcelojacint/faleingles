package br.com.faleingles.presentation.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.faleingles.domain.model.Lesson
import br.com.faleingles.presentation.home.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLessonClick: (String) -> Unit,
    onConversationClick: () -> Unit,
    onReviewClick: () -> Unit = {},
    onProgressClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FaleInglês", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = onReviewClick) {
                        Icon(Icons.Outlined.Refresh, contentDescription = "Revisar")
                    }
                    IconButton(onClick = onProgressClick) {
                        Icon(Icons.Outlined.Leaderboard, contentDescription = "Progresso")
                    }
                    IconButton(onClick = onConversationClick) {
                        Icon(Icons.Outlined.Chat, contentDescription = "Conversar com IA")
                    }
                },
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                StreakCard(
                    streak = uiState.streakDays,
                    todayMinutes = uiState.todayMinutes,
                    goalMinutes = uiState.goalMinutes,
                )
            }

            item {
                Text(
                    "Suas lições",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }

            items(uiState.lessons, key = { it.id }) { lesson ->
                LessonCard(
                    lesson = lesson,
                    onClick = { if (lesson.isUnlocked) onLessonClick(lesson.id) },
                )
            }
        }
    }
}

@Composable
private fun StreakCard(streak: Int, todayMinutes: Int, goalMinutes: Int) {
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
                    "$streak dias seguidos",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    "$todayMinutes / $goalMinutes min hoje",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                )
            }
            Text("🔥", style = MaterialTheme.typography.displayMedium)
        }
    }
}

@Composable
private fun LessonCard(lesson: Lesson, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (lesson.isUnlocked) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        if (lesson.isUnlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (lesson.isUnlocked) Icons.Filled.PlayArrow else Icons.Filled.Lock,
                    contentDescription = null,
                    tint = if (lesson.isUnlocked) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline,
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Fase ${lesson.phase} · Lição ${lesson.orderInPhase}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    lesson.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    lesson.situationContext,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
