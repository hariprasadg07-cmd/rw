package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.JarvisAlertRed
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDarkBackground
import com.example.ui.theme.JarvisDarkSurface
import com.example.ui.theme.JarvisHoloBorder
import com.example.ui.theme.JarvisStarkGold
import com.example.ui.theme.JarvisSurfaceVariant
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import com.example.ui.theme.JarvisTextTertiary

@Composable
fun SettingsDialog(
    speechRate: Float,
    speechPitch: Float,
    autoSpeak: Boolean,
    onSpeechRateChange: (Float) -> Unit,
    onSpeechPitchChange: (Float) -> Unit,
    onAutoSpeakChange: (Boolean) -> Unit,
    onTestVoice: () -> Unit,
    onClearHistory: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("settings_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = JarvisDarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, JarvisCyan.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CONFIGURATION",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = JarvisStarkGold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Assistant Voice Settings",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = JarvisCyan
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Settings",
                            tint = JarvisTextSecondary
                        )
                    }
                }

                // Auto Speak Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(JarvisSurfaceVariant, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Auto-Vocalize Answers",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = JarvisTextPrimary
                        )
                        Text(
                            text = "Speak responses aloud via Text-to-Speech",
                            fontSize = 11.sp,
                            color = JarvisTextSecondary
                        )
                    }
                    Switch(
                        checked = autoSpeak,
                        onCheckedChange = onAutoSpeakChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = JarvisDarkBackground,
                            checkedTrackColor = JarvisCyan,
                            uncheckedThumbColor = JarvisTextSecondary,
                            uncheckedTrackColor = JarvisDarkSurface
                        ),
                        modifier = Modifier.testTag("auto_speak_switch")
                    )
                }

                // Voice Pitch
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Voice Butler Pitch",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = JarvisTextPrimary
                        )
                        Text(
                            text = String.format("%.2fx", speechPitch),
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            color = JarvisCyan
                        )
                    }
                    Slider(
                        value = speechPitch,
                        onValueChange = onSpeechPitchChange,
                        valueRange = 0.7f..1.3f,
                        steps = 6,
                        colors = SliderDefaults.colors(
                            thumbColor = JarvisCyan,
                            activeTrackColor = JarvisCyan,
                            inactiveTrackColor = JarvisSurfaceVariant
                        ),
                        modifier = Modifier.testTag("pitch_slider")
                    )
                }

                // Voice Speed / Rate
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Cadence / Speech Rate",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = JarvisTextPrimary
                        )
                        Text(
                            text = String.format("%.2fx", speechRate),
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            color = JarvisCyan
                        )
                    }
                    Slider(
                        value = speechRate,
                        onValueChange = onSpeechRateChange,
                        valueRange = 0.7f..1.4f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = JarvisCyan,
                            activeTrackColor = JarvisCyan,
                            inactiveTrackColor = JarvisSurfaceVariant
                        ),
                        modifier = Modifier.testTag("rate_slider")
                    )
                }

                // Test Voice Button
                OutlinedButton(
                    onClick = onTestVoice,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_voice_button"),
                    border = androidx.compose.foundation.BorderStroke(1.dp, JarvisCyan),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = null,
                        tint = JarvisCyan,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "TEST VOICE SYNTHESIZER",
                        color = JarvisCyan,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Clear Memory Button
                OutlinedButton(
                    onClick = onClearHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("clear_memory_button"),
                    border = androidx.compose.foundation.BorderStroke(1.dp, JarvisAlertRed.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = JarvisAlertRed,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "PURGE INTERACTION MEMORY",
                        color = JarvisAlertRed,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Done Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("done_settings_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "APPLY & CLOSE",
                        color = JarvisDarkBackground,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
