package br.com.faleingles.presentation.audio.viewmodel

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import br.com.faleingles.domain.model.Phrase
import br.com.faleingles.domain.repository.LessonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

enum class PlaybackSpeed(val factor: Float, val label: String) {
    SLOW(0.75f, "0.75×"),
    NORMAL(1f, "1×"),
    FAST(1.25f, "1.25×"),
}

enum class RecordingState { IDLE, RECORDING, RECORDED }

data class AudioUiState(
    val lessonTitle: String = "",
    val phrases: List<Phrase> = emptyList(),
    val currentPhraseIndex: Int = 0,
    val isPlaying: Boolean = false,
    val playbackProgress: Float = 0f,
    val playbackSpeed: PlaybackSpeed = PlaybackSpeed.NORMAL,
    val recordingState: RecordingState = RecordingState.IDLE,
    val hasRecording: Boolean = false,
    val isLoading: Boolean = true,
)

@HiltViewModel
class AudioViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AudioUiState())
    val uiState: StateFlow<AudioUiState> = _uiState.asStateFlow()

    private var player: ExoPlayer? = null
    private var recorder: MediaRecorder? = null
    private var recordingFile: File? = null

    fun loadLesson(lessonId: String) {
        viewModelScope.launch {
            val lesson = lessonRepository.getLessonById(lessonId) ?: return@launch
            _uiState.update {
                it.copy(
                    lessonTitle = lesson.title,
                    phrases = lesson.phrases,
                    isLoading = false,
                )
            }
        }
    }

    fun playPhrase() {
        val phrase = currentPhrase() ?: return
        val audioUrl = phrase.audioUrl

        if (audioUrl.isBlank()) {
            simulatePlayback()
            return
        }

        player?.release()
        player = ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(audioUrl))
            prepare()
            play()
        }
        _uiState.update { it.copy(isPlaying = true) }

        viewModelScope.launch {
            while (player?.isPlaying == true) {
                val duration = player?.duration?.takeIf { it > 0 } ?: 1L
                val pos = player?.currentPosition ?: 0L
                _uiState.update { it.copy(playbackProgress = pos.toFloat() / duration) }
                delay(100)
            }
            _uiState.update { it.copy(isPlaying = false, playbackProgress = 0f) }
        }
    }

    fun setSpeed(speed: PlaybackSpeed) {
        player?.setPlaybackSpeed(speed.factor)
        _uiState.update { it.copy(playbackSpeed = speed) }
    }

    fun startRecording() {
        recordingFile = File(context.cacheDir, "rec_${System.currentTimeMillis()}.m4a")
        recorder = (if (Build.VERSION.SDK_INT >= 31)
            MediaRecorder(context) else @Suppress("DEPRECATION") MediaRecorder()
        ).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(recordingFile!!.absolutePath)
            prepare()
            start()
        }
        _uiState.update { it.copy(recordingState = RecordingState.RECORDING) }
    }

    fun stopRecording() {
        runCatching {
            recorder?.stop()
            recorder?.release()
        }
        recorder = null
        _uiState.update { it.copy(recordingState = RecordingState.RECORDED, hasRecording = true) }
    }

    fun playRecording() {
        val file = recordingFile ?: return
        player?.release()
        player = ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(file.toURI().toString()))
            prepare()
            play()
        }
    }

    fun nextPhrase() {
        val next = (_uiState.value.currentPhraseIndex + 1)
            .coerceAtMost(_uiState.value.phrases.lastIndex)
        _uiState.update {
            it.copy(
                currentPhraseIndex = next,
                isPlaying = false,
                playbackProgress = 0f,
                recordingState = RecordingState.IDLE,
                hasRecording = false,
            )
        }
        player?.stop()
    }

    fun previousPhrase() {
        val prev = (_uiState.value.currentPhraseIndex - 1).coerceAtLeast(0)
        _uiState.update {
            it.copy(
                currentPhraseIndex = prev,
                isPlaying = false,
                playbackProgress = 0f,
                recordingState = RecordingState.IDLE,
                hasRecording = false,
            )
        }
        player?.stop()
    }

    private fun currentPhrase() = _uiState.value.phrases.getOrNull(_uiState.value.currentPhraseIndex)

    private fun simulatePlayback() {
        _uiState.update { it.copy(isPlaying = true) }
        viewModelScope.launch {
            repeat(20) { i ->
                delay(100)
                _uiState.update { it.copy(playbackProgress = (i + 1) / 20f) }
            }
            _uiState.update { it.copy(isPlaying = false, playbackProgress = 0f) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        player?.release()
        recorder?.release()
    }
}
