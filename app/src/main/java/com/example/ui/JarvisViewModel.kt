package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.InteractionEntity
import com.example.data.JarvisDatabase
import com.example.network.GeminiClient
import com.example.system.DeviceTelemetry
import com.example.system.SystemDiagnostics
import com.example.voice.AssistantState
import com.example.voice.VoiceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class JarvisUiState(
    val currentQuery: String = "",
    val liveTranscript: String = "",
    val jarvisResponse: String = "Good day, Sir. Systems are fully calibrated. How may I be of service?",
    val latencyMs: Long = 0,
    val statusNotice: String = "Tap the Arc Reactor or voice button to issue a command.",
    val showDiagnostics: Boolean = false,
    val showSettings: Boolean = false,
    val showHistory: Boolean = false,
    val speechRate: Float = 1.0f,
    val speechPitch: Float = 0.95f,
    val autoSpeak: Boolean = true
)

class JarvisViewModel(application: Application) : AndroidViewModel(application) {

    private val db = JarvisDatabase.getDatabase(application)
    private val interactionDao = db.interactionDao()

    val interactionHistory: StateFlow<List<InteractionEntity>> = interactionDao
        .getAllInteractions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _uiState = MutableStateFlow(JarvisUiState())
    val uiState: StateFlow<JarvisUiState> = _uiState.asStateFlow()

    private val _telemetry = MutableStateFlow(SystemDiagnostics.getTelemetry(application))
    val telemetry: StateFlow<DeviceTelemetry> = _telemetry.asStateFlow()

    private val voiceManager = VoiceManager(
        context = application,
        onSpeechRecognized = { text ->
            processQuery(text, isVoice = true)
        },
        onError = { errorMsg ->
            _uiState.value = _uiState.value.copy(
                statusNotice = errorMsg
            )
        }
    )

    val assistantState: StateFlow<AssistantState> = voiceManager.assistantState
    val rmsLevel: StateFlow<Float> = voiceManager.rmsLevel

    init {
        // Collect live recognized partial words
        viewModelScope.launch {
            voiceManager.recognizedPartial.collect { partial ->
                if (partial.isNotBlank()) {
                    _uiState.value = _uiState.value.copy(liveTranscript = partial)
                }
            }
        }
        refreshTelemetry()
    }

    fun refreshTelemetry() {
        _telemetry.value = SystemDiagnostics.getTelemetry(getApplication())
    }

    fun onArcReactorClick() {
        when (voiceManager.assistantState.value) {
            AssistantState.LISTENING -> {
                voiceManager.stopListening()
            }
            AssistantState.SPEAKING -> {
                voiceManager.stopSpeaking()
            }
            AssistantState.PROCESSING -> {
                // Already busy
            }
            AssistantState.IDLE, AssistantState.ERROR -> {
                startVoiceInput()
            }
        }
    }

    fun startVoiceInput() {
        voiceManager.startListening()
        _uiState.value = _uiState.value.copy(
            statusNotice = "Listening to audio input, Sir..."
        )
    }

    fun stopVoiceInput() {
        voiceManager.stopListening()
    }

    fun stopSpeaking() {
        voiceManager.stopSpeaking()
    }

    fun submitTextQuery(text: String) {
        if (text.isBlank()) return
        processQuery(text.trim(), isVoice = false)
    }

    private fun processQuery(query: String, isVoice: Boolean) {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            _uiState.value = _uiState.value.copy(
                currentQuery = query,
                liveTranscript = query,
                statusNotice = "Processing query..."
            )

            // 1. Check local instant commands / diagnostics / easter eggs
            val localResponse = SystemDiagnostics.handleLocalCommand(query, getApplication())
            if (localResponse != null) {
                val latency = System.currentTimeMillis() - startTime
                handleSuccessfulResponse(query, localResponse, isVoice, latency)
                return@launch
            }

            // 2. Query Gemini 3.5 Flash via REST API
            val recentTurns = interactionHistory.value.take(3).map { it.query to it.response }
            val geminiResult = GeminiClient.askJarvis(query, recentTurns)

            val latency = System.currentTimeMillis() - startTime

            geminiResult.fold(
                onSuccess = { responseText ->
                    handleSuccessfulResponse(query, responseText, isVoice, latency)
                },
                onFailure = { error ->
                    val fallback = "Apologies, Sir. I encountered an issue accessing neural servers: ${error.message ?: "Network timeout"}. All local subroutines remain functional."
                    _uiState.value = _uiState.value.copy(
                        jarvisResponse = fallback,
                        latencyMs = latency,
                        statusNotice = "Error connecting to AI service."
                    )
                    voiceManager.speak(fallback)
                }
            )
        }
    }

    private suspend fun handleSuccessfulResponse(
        query: String,
        response: String,
        isVoice: Boolean,
        latency: Long
    ) {
        _uiState.value = _uiState.value.copy(
            jarvisResponse = response,
            latencyMs = latency,
            statusNotice = "Query completed in ${latency}ms."
        )

        // Speak aloud if enabled
        voiceManager.speak(response)

        // Store into Room database
        interactionDao.insertInteraction(
            InteractionEntity(
                query = query,
                response = response,
                isVoice = isVoice,
                latencyMs = latency
            )
        )
    }

    fun replayAudio(text: String) {
        voiceManager.speak(text)
    }

    fun testVoice() {
        voiceManager.speak("Testing vocal synthesizer, Sir. All audio subsystems are operating at peak efficiency.")
    }

    fun setSpeechRate(rate: Float) {
        voiceManager.speechRate = rate
        _uiState.value = _uiState.value.copy(speechRate = rate)
    }

    fun setSpeechPitch(pitch: Float) {
        voiceManager.speechPitch = pitch
        _uiState.value = _uiState.value.copy(speechPitch = pitch)
    }

    fun setAutoSpeak(enabled: Boolean) {
        voiceManager.autoSpeak = enabled
        _uiState.value = _uiState.value.copy(autoSpeak = enabled)
    }

    fun clearHistory() {
        viewModelScope.launch {
            interactionDao.clearAllInteractions()
        }
    }

    fun setShowDiagnostics(show: Boolean) {
        if (show) refreshTelemetry()
        _uiState.value = _uiState.value.copy(showDiagnostics = show)
    }

    fun setShowSettings(show: Boolean) {
        _uiState.value = _uiState.value.copy(showSettings = show)
    }

    fun setShowHistory(show: Boolean) {
        _uiState.value = _uiState.value.copy(showHistory = show)
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.destroy()
    }
}
