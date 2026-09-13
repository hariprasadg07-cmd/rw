package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class AssistantState {
    IDLE,
    LISTENING,
    PROCESSING,
    SPEAKING,
    ERROR
}

class VoiceManager(
    private val context: Context,
    private val onSpeechRecognized: (String) -> Unit,
    private val onError: (String) -> Unit
) {
    private val tag = "VoiceManager"

    private val _assistantState = MutableStateFlow(AssistantState.IDLE)
    val assistantState: StateFlow<AssistantState> = _assistantState.asStateFlow()

    private val _rmsLevel = MutableStateFlow(0f)
    val rmsLevel: StateFlow<Float> = _rmsLevel.asStateFlow()

    private val _recognizedPartial = MutableStateFlow("")
    val recognizedPartial: StateFlow<String> = _recognizedPartial.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    var speechRate: Float = 1.0f
        set(value) {
            field = value
            textToSpeech?.setSpeechRate(value)
        }

    var speechPitch: Float = 0.95f
        set(value) {
            field = value
            textToSpeech?.setPitch(value)
        }

    var autoSpeak: Boolean = true

    init {
        initTts()
    }

    private fun initTts() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady = true
                val result = textToSpeech?.setLanguage(Locale.UK)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech?.setLanguage(Locale.US)
                }
                textToSpeech?.setPitch(speechPitch)
                textToSpeech?.setSpeechRate(speechRate)

                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _assistantState.value = AssistantState.SPEAKING
                    }

                    override fun onDone(utteranceId: String?) {
                        _assistantState.value = AssistantState.IDLE
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _assistantState.value = AssistantState.IDLE
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        _assistantState.value = AssistantState.IDLE
                    }
                })
            } else {
                Log.e(tag, "TTS Initialization failed")
            }
        }
    }

    fun startListening() {
        stopSpeaking()

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _assistantState.value = AssistantState.ERROR
            onError("Speech recognition service is not available on this device.")
            return
        }

        try {
            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _assistantState.value = AssistantState.LISTENING
                        _recognizedPartial.value = ""
                    }

                    override fun onBeginningOfSpeech() {
                        _assistantState.value = AssistantState.LISTENING
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        // Normalize roughly 0 to 10 scale
                        _rmsLevel.value = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _assistantState.value = AssistantState.PROCESSING
                        _rmsLevel.value = 0f
                    }

                    override fun onError(error: Int) {
                        _assistantState.value = AssistantState.IDLE
                        _rmsLevel.value = 0f
                        val message = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                            SpeechRecognizer.ERROR_CLIENT -> "Client recognition error"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Audio permission required"
                            SpeechRecognizer.ERROR_NETWORK -> "Network connection error"
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected"
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Voice recognizer is busy"
                            SpeechRecognizer.ERROR_SERVER -> "Server error"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input detected"
                            else -> "Recognition issue ($error)"
                        }
                        if (error != SpeechRecognizer.ERROR_NO_MATCH && error != SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                            onError(message)
                        }
                    }

                    override fun onResults(results: Bundle?) {
                        _rmsLevel.value = 0f
                        _assistantState.value = AssistantState.PROCESSING
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()?.trim().orEmpty()
                        if (text.isNotEmpty()) {
                            _recognizedPartial.value = text
                            onSpeechRecognized(text)
                        } else {
                            _assistantState.value = AssistantState.IDLE
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        matches?.firstOrNull()?.let {
                            _recognizedPartial.value = it
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }

            speechRecognizer?.startListening(intent)
            _assistantState.value = AssistantState.LISTENING
        } catch (e: Exception) {
            Log.e(tag, "Failed to start listening", e)
            _assistantState.value = AssistantState.ERROR
            onError("Unable to initialize audio capture: ${e.message}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.w(tag, "Error stopping listener", e)
        }
        _assistantState.value = AssistantState.IDLE
        _rmsLevel.value = 0f
    }

    fun speak(text: String) {
        if (!autoSpeak || !isTtsReady || text.isBlank()) return
        stopSpeaking()
        _assistantState.value = AssistantState.SPEAKING
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "JARVIS_TTS_${System.currentTimeMillis()}")
    }

    fun stopSpeaking() {
        if (textToSpeech?.isSpeaking == true) {
            textToSpeech?.stop()
        }
        if (_assistantState.value == AssistantState.SPEAKING) {
            _assistantState.value = AssistantState.IDLE
        }
    }

    fun destroy() {
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            textToSpeech = null
        } catch (e: Exception) {
            Log.w(tag, "Error cleaning up VoiceManager", e)
        }
    }
}
