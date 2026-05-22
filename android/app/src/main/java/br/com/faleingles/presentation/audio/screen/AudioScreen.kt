package br.com.faleingles.presentation.audio.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.faleingles.presentation.audio.viewmodel.AudioUiState
import br.com.faleingles.presentation.audio.viewmodel.AudioViewModel
import br.com.faleingles.presentation.audio.viewmodel.PlaybackSpeed
import br.com.faleingles.presentation.audio.viewmodel.RecordingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioScreen(
    lessonId: String,
    onBack: () -> Unit,
    viewModel: AudioViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(lessonId) { viewModel.loadLesson(lessonId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ouvir e repetir") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        }
    ) { padding ->
        if (state.isLoading || state.phrases.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val phrase = state.phrases[state.currentPhraseIndex]

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                PhraseCounter(state)

                PhraseDisplay(phrase.affirmative)

                NativePlaybackCard(
                    isPlaying = state.isPlaying,
                    progress = state.playbackProgress,
                    speed = state.playbackSpeed,
                    onPlay = { viewModel.playPhrase() },
                    onSpeedChange = { viewModel.setSpeed(it) },
                )

                RecordingCard(
                    recordingState = state.recordingState,
                    hasRecording = state.hasRecording,
                    onStartRecord = { viewModel.startRecording() },
                    onStopRecord = { viewModel.stopRecording() },
                    onPlayRecording = { viewModel.playRecording() },
                )
            }

            NavigationRow(
                hasPrev = state.currentPhraseIndex > 0,
                hasNext = state.currentPhraseIndex < state.phrases.lastIndex,
                onPrev = { viewModel.previousPhrase() },
                onNext = { viewModel.nextPhrase() },
            )
        }
    }
}

@Composable
private fun PhraseCounter(state: AudioUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            "${state.currentPhraseIndex + 1} / ${state.phrases.size}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            state.lessonTitle,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
        )
    }
}

@Composable
private fun PhraseDisplay(phrase: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Text(
            text = phrase,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
        )
    }
}

@Composable
private fun NativePlaybackCard(
    isPlaying: Boolean,
    progress: Float,
    speed: PlaybackSpeed,
    onPlay: () -> Unit,
    onSpeedChange: (PlaybackSpeed) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "Voz nativa",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PlaybackSpeedChips(speed, onSpeedChange)

                Spacer(Modifier.weight(1f))

                FilledIconButton(
                    onClick = onPlay,
                    modifier = Modifier.size(56.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Icon(
                        if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pausar" else "Reproduzir",
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaybackSpeedChips(current: PlaybackSpeed, onChange: (PlaybackSpeed) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        PlaybackSpeed.entries.forEach { speed ->
            val selected = speed == current
            FilterChip(
                selected = selected,
                onClick = { onChange(speed) },
                label = { Text(speed.label, style = MaterialTheme.typography.labelLarge) },
            )
        }
    }
}

@Composable
private fun RecordingCard(
    recordingState: RecordingState,
    hasRecording: Boolean,
    onStartRecord: () -> Unit,
    onStopRecord: () -> Unit,
    onPlayRecording: () -> Unit,
) {
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val scale by pulseAnim.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            tween(600, easing = FastOutSlowInEasing),
            RepeatMode.Reverse,
        ),
        label = "scale",
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (recordingState) {
                RecordingState.RECORDING -> MaterialTheme.colorScheme.errorContainer
                RecordingState.RECORDED -> MaterialTheme.colorScheme.secondaryContainer
                RecordingState.IDLE -> MaterialTheme.colorScheme.surface
            },
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                when (recordingState) {
                    RecordingState.IDLE -> "Sua vez — grave e compare"
                    RecordingState.RECORDING -> "Gravando..."
                    RecordingState.RECORDED -> "Gravação pronta!"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilledIconButton(
                    onClick = if (recordingState == RecordingState.RECORDING) onStopRecord else onStartRecord,
                    modifier = Modifier
                        .size(56.dp)
                        .then(if (recordingState == RecordingState.RECORDING) Modifier.scale(scale) else Modifier),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (recordingState == RecordingState.RECORDING)
                            MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.secondary,
                    ),
                ) {
                    Icon(
                        if (recordingState == RecordingState.RECORDING) Icons.Filled.Stop else Icons.Filled.Mic,
                        contentDescription = "Gravar",
                        modifier = Modifier.size(28.dp),
                        tint = Color.White,
                    )
                }

                if (hasRecording) {
                    OutlinedButton(onClick = onPlayRecording) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Ouvir minha voz")
                    }
                }
            }

            if (recordingState == RecordingState.RECORDED) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                    ),
                ) {
                    Text(
                        "Compare sua pronúncia com a voz nativa acima",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationRow(
    hasPrev: Boolean,
    hasNext: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (hasPrev) {
            OutlinedButton(
                onClick = onPrev,
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Filled.ChevronLeft, contentDescription = null)
                Text("Anterior")
            }
        } else {
            Spacer(Modifier.weight(1f))
        }

        Button(
            onClick = onNext,
            enabled = hasNext,
            modifier = Modifier.weight(1f),
        ) {
            Text(if (hasNext) "Próxima" else "Concluído")
            if (hasNext) Icon(Icons.Filled.ChevronRight, contentDescription = null)
        }
    }
}
