package br.com.faleingles.presentation.progress.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.faleingles.presentation.progress.viewmodel.PhaseProgress
import br.com.faleingles.presentation.progress.viewmodel.ProgressUiState
import br.com.faleingles.presentation.progress.viewmodel.ProgressViewModel

private val phaseNames = mapOf(
    1 to "Identity & Being",
    2 to "Things Around You",
    3 to "Talking About People",
    4 to "Real Expressions",
    5 to "Real Conversation",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    onBack: () -> Unit,
    viewModel: ProgressViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu progresso") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
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

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { StatsGrid(state) }
            item { SectionTitle("Jornada de aprendizado") }
            items(state.phaseProgressList) { phase -> PhaseCard(phase) }
            if (state.recentlyCompleted.isNotEmpty()) {
                item { SectionTitle("Concluídas recentemente") }
                items(state.recentlyCompleted) { lesson ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Icon(Icons.Filled.CheckCircle, null, tint = MaterialTheme.colorScheme.secondary)
                            Column {
                                Text(lesson.title, style = MaterialTheme.typography.titleMedium)
                                Text("Fase ${lesson.phase}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsGrid(state: ProgressUiState) {
    val progress = state.userProgress ?: return
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatCard("🔥", "${progress.currentStreakDays}", "dias seguidos", Modifier.weight(1f))
        StatCard("📖", "${progress.completedLessonIds.size}", "lições", Modifier.weight(1f))
        StatCard("⏱️", "${progress.totalMinutesStudied}", "minutos", Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(emoji: String, value: String, label: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(emoji, style = MaterialTheme.typography.titleLarge)
            Text(value, style = MaterialTheme.typography.displayMedium.copy(fontSize = MaterialTheme.typography.headlineMedium.fontSize), color = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
}

@Composable
private fun PhaseCard(phase: PhaseProgress) {
    val isUnlocked = phase.phase == 1 || phase.completedLessons > 0 ||
            (phase.phase > 1)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (phase.isComplete)
                MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (phase.isComplete) MaterialTheme.colorScheme.secondary
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "${phase.phase}",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (phase.isComplete) MaterialTheme.colorScheme.onSecondary
                            else MaterialTheme.colorScheme.primary,
                        )
                    }
                    Column {
                        Text("Fase ${phase.phase}", style = MaterialTheme.typography.titleMedium)
                        Text(phaseNames[phase.phase] ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f))
                    }
                }
                if (phase.totalLessons > 0) {
                    Text(
                        "${phase.completedLessons}/${phase.totalLessons}",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (phase.isComplete) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    )
                }
            }

            if (phase.totalLessons > 0) {
                LinearProgressIndicator(
                    progress = { phase.percentage },
                    modifier = Modifier.fillMaxWidth(),
                    color = if (phase.isComplete) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            } else {
                Text(
                    "Em breve",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                )
            }
        }
    }
}
