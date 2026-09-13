package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ArcReactorView
import com.example.ui.components.DiagnosticsDialog
import com.example.ui.components.HistorySheet
import com.example.ui.components.HudTopBar
import com.example.ui.components.QuickCommands
import com.example.ui.components.SettingsDialog
import com.example.ui.components.TranscriptCard
import com.example.ui.theme.JarvisAlertRed
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDarkBackground
import com.example.ui.theme.JarvisDarkSurface
import com.example.ui.theme.JarvisGridLines
import com.example.ui.theme.JarvisHoloBorder
import com.example.ui.theme.JarvisStarkGold
import com.example.ui.theme.JarvisSurfaceVariant
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import com.example.ui.theme.JarvisTextTertiary
import com.example.voice.AssistantState

@Composable
fun JarvisScreen(
    viewModel: JarvisViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val assistantState by viewModel.assistantState.collectAsState()
    val rmsLevel by viewModel.rmsLevel.collectAsState()
    val telemetry by viewModel.telemetry.collectAsState()
    val history by viewModel.interactionHistory.collectAsState()

    var textInput by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startVoiceInput()
        } else {
            Toast.makeText(
                context,
                "Microphone permission is required to capture voice commands.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun requestVoiceInput() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        )
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            viewModel.onArcReactorClick()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(JarvisDarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        containerColor = JarvisDarkBackground,
        topBar = {
            HudTopBar(
                state = assistantState,
                onOpenTelemetry = { viewModel.setShowDiagnostics(true) },
                onOpenHistory = { viewModel.setShowHistory(true) },
                onOpenSettings = { viewModel.setShowSettings(true) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Subtle Cybernetic Background Grid & Hologram Ambient Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridSpacing = 44.dp.toPx()
                val width = size.width
                val height = size.height

                // Ambient Radial Center Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            JarvisCyan.copy(alpha = 0.07f),
                            JarvisDarkBackground.copy(alpha = 0f)
                        ),
                        center = Offset(width / 2f, height * 0.35f),
                        radius = width * 0.7f
                    ),
                    radius = width * 0.7f,
                    center = Offset(width / 2f, height * 0.35f)
                )

                // High-tech subtle grid lines
                var x = 0f
                while (x < width) {
                    drawLine(
                        color = JarvisGridLines,
                        start = Offset(x, 0f),
                        end = Offset(x, height),
                        strokeWidth = 0.5.dp.toPx()
                    )
                    x += gridSpacing
                }

                var y = 0f
                while (y < height) {
                    drawLine(
                        color = JarvisGridLines,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 0.5.dp.toPx()
                    )
                    y += gridSpacing
                }
            }

            // Main Scrollable Stage
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Arc Reactor Hologram
                ArcReactorView(
                    state = assistantState,
                    rmsLevel = rmsLevel,
                    onClick = { requestVoiceInput() },
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // Action Cue Text
                val cueText = when (assistantState) {
                    AssistantState.IDLE -> "TAP CORE OR SAY COMMAND"
                    AssistantState.LISTENING -> "LISTENING... SPEAK NOW"
                    AssistantState.PROCESSING -> "ANALYZING NEURAL TELEMETRY..."
                    AssistantState.SPEAKING -> "JARVIS VOCALIZING • TAP TO HALT"
                    AssistantState.ERROR -> "SYSTEM NOTICE ENCOUNTERED"
                }

                val cueColor = when (assistantState) {
                    AssistantState.LISTENING -> JarvisCyan
                    AssistantState.PROCESSING -> JarvisStarkGold
                    AssistantState.SPEAKING -> JarvisCyan
                    AssistantState.ERROR -> JarvisAlertRed
                    AssistantState.IDLE -> JarvisTextSecondary
                }

                Text(
                    text = cueText,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = cueColor,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Quick Command Suggestions
                QuickCommands(
                    onCommandSelected = { command ->
                        viewModel.submitTextQuery(command)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Query & Response Transcripts
                val displayQuery = if (assistantState == AssistantState.LISTENING && uiState.liveTranscript.isNotBlank()) {
                    uiState.liveTranscript
                } else {
                    uiState.currentQuery
                }

                TranscriptCard(
                    state = assistantState,
                    recognizedQuery = displayQuery,
                    jarvisResponse = uiState.jarvisResponse,
                    latencyMs = uiState.latencyMs,
                    onReplayAudio = { text -> viewModel.replayAudio(text) },
                    modifier = Modifier.widthIn(max = 600.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Bottom Floating Command Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                JarvisDarkBackground.copy(alpha = 0f),
                                JarvisDarkBackground.copy(alpha = 0.95f),
                                JarvisDarkBackground
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Text Input Bar
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = {
                            Text(
                                text = "Ask Jarvis or enter instruction...",
                                fontSize = 13.sp,
                                color = JarvisTextTertiary
                            )
                        },
                        trailingIcon = {
                            if (textInput.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        val query = textInput.trim()
                                        textInput = ""
                                        viewModel.submitTextQuery(query)
                                    },
                                    modifier = Modifier.testTag("send_query_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Send Text Command",
                                        tint = JarvisCyan
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JarvisCyan,
                            unfocusedBorderColor = JarvisHoloBorder,
                            focusedTextColor = JarvisTextPrimary,
                            unfocusedTextColor = JarvisTextPrimary,
                            cursorColor = JarvisCyan,
                            focusedContainerColor = JarvisDarkSurface,
                            unfocusedContainerColor = JarvisDarkSurface
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("command_input_field")
                    )

                    // Active Voice / Stop Action Button
                    val isListening = assistantState == AssistantState.LISTENING
                    val isSpeaking = assistantState == AssistantState.SPEAKING

                    FloatingActionButton(
                        onClick = {
                            if (isSpeaking) {
                                viewModel.stopSpeaking()
                            } else {
                                requestVoiceInput()
                            }
                        },
                        containerColor = when {
                            isSpeaking -> JarvisSurfaceVariant
                            isListening -> JarvisCyan
                            else -> JarvisCyan
                        },
                        contentColor = when {
                            isSpeaking -> JarvisCyan
                            else -> JarvisDarkBackground
                        },
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("voice_fab_button")
                    ) {
                        when {
                            isSpeaking -> {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeOff,
                                    contentDescription = "Halt Vocalization",
                                    tint = JarvisCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            isListening -> {
                                Icon(
                                    imageVector = Icons.Default.Stop,
                                    contentDescription = "Stop Listening",
                                    tint = JarvisDarkBackground,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            else -> {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Activate Voice Input",
                                    tint = JarvisDarkBackground,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Overlays & Dialogs
            if (uiState.showDiagnostics) {
                DiagnosticsDialog(
                    telemetry = telemetry,
                    onDismiss = { viewModel.setShowDiagnostics(false) }
                )
            }

            if (uiState.showSettings) {
                SettingsDialog(
                    speechRate = uiState.speechRate,
                    speechPitch = uiState.speechPitch,
                    autoSpeak = uiState.autoSpeak,
                    onSpeechRateChange = { viewModel.setSpeechRate(it) },
                    onSpeechPitchChange = { viewModel.setSpeechPitch(it) },
                    onAutoSpeakChange = { viewModel.setAutoSpeak(it) },
                    onTestVoice = { viewModel.testVoice() },
                    onClearHistory = {
                        viewModel.clearHistory()
                        Toast.makeText(context, "Memory logs cleared.", Toast.LENGTH_SHORT).show()
                    },
                    onDismiss = { viewModel.setShowSettings(false) }
                )
            }

            if (uiState.showHistory) {
                HistorySheet(
                    interactions = history,
                    onReplayAudio = { viewModel.replayAudio(it) },
                    onDismiss = { viewModel.setShowHistory(false) }
                )
            }
        }
    }
}
