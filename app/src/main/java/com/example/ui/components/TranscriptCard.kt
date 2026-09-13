package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDarkSurface
import com.example.ui.theme.JarvisElectricBlue
import com.example.ui.theme.JarvisHoloBorder
import com.example.ui.theme.JarvisStarkGold
import com.example.ui.theme.JarvisSurfaceVariant
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import com.example.ui.theme.JarvisTextTertiary
import com.example.voice.AssistantState

@Composable
fun TranscriptCard(
    state: AssistantState,
    recognizedQuery: String,
    jarvisResponse: String,
    latencyMs: Long,
    onReplayAudio: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // User Query Card
        AnimatedVisibility(
            visible = recognizedQuery.isNotBlank(),
            enter = fadeIn() + slideInVertically { it / 2 }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("user_query_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = JarvisDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, JarvisHoloBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(JarvisSurfaceVariant, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "User Voice Query",
                            tint = JarvisCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        Text(
                            text = "COMMAND TRANSMITTED",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = JarvisTextTertiary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = recognizedQuery,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = JarvisTextPrimary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        // JARVIS Response Card
        AnimatedVisibility(
            visible = jarvisResponse.isNotBlank(),
            enter = fadeIn() + slideInVertically { it / 2 }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("jarvis_response_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = JarvisDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, JarvisCyan.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "J.A.R.V.I.S.",
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                color = JarvisCyan,
                                letterSpacing = 1.5.sp
                            )
                            if (latencyMs > 0) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .background(JarvisSurfaceVariant, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${latencyMs}ms",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = JarvisStarkGold
                                    )
                                }
                            }
                        }

                        Row {
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(jarvisResponse))
                                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp).testTag("copy_response_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Response",
                                    tint = JarvisTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            IconButton(
                                onClick = { onReplayAudio(jarvisResponse) },
                                modifier = Modifier.size(32.dp).testTag("replay_audio_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Replay Voice Response",
                                    tint = JarvisElectricBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Content Body
                    Text(
                        text = jarvisResponse,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        color = JarvisTextPrimary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
