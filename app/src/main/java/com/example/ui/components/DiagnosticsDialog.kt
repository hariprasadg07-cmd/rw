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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.system.DeviceTelemetry
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDarkBackground
import com.example.ui.theme.JarvisDarkSurface
import com.example.ui.theme.JarvisElectricBlue
import com.example.ui.theme.JarvisHoloBorder
import com.example.ui.theme.JarvisStarkGold
import com.example.ui.theme.JarvisSurfaceVariant
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import com.example.ui.theme.JarvisTextTertiary

@Composable
fun DiagnosticsDialog(
    telemetry: DeviceTelemetry,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("diagnostics_dialog"),
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
                            text = "STARK TELEMETRY",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = JarvisStarkGold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Core System Diagnostics",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = JarvisCyan
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_diagnostics_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Diagnostics",
                            tint = JarvisTextSecondary
                        )
                    }
                }

                // Grid of Telemetry Readouts
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Power Cell / Battery
                    TelemetryRow(
                        label = "POWER CELL",
                        value = "${telemetry.batteryLevel}% ${if (telemetry.isCharging) "(CHARGING)" else "(DISCHARGING)"}",
                        highlightColor = if (telemetry.batteryLevel < 20) JarvisStarkGold else JarvisCyan
                    )
                    LinearProgressIndicator(
                        progress = { telemetry.batteryLevel / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp)),
                        color = JarvisCyan,
                        trackColor = JarvisSurfaceVariant
                    )

                    // Memory Allocation
                    TelemetryRow(
                        label = "NEURAL RAM LOAD",
                        value = "${telemetry.ramUsagePercent}% (${telemetry.availableRamMb} MB FREE)",
                        highlightColor = JarvisElectricBlue
                    )
                    LinearProgressIndicator(
                        progress = { telemetry.ramUsagePercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp)),
                        color = JarvisElectricBlue,
                        trackColor = JarvisSurfaceVariant
                    )

                    // Network Link
                    TelemetryRow(
                        label = "UPLINK STATUS",
                        value = telemetry.networkStatus,
                        highlightColor = JarvisCyan
                    )

                    // System Uptime
                    TelemetryRow(
                        label = "SYSTEM UPTIME",
                        value = telemetry.uptimeFormatted,
                        highlightColor = JarvisTextPrimary
                    )

                    // Model & Chronometer
                    TelemetryRow(
                        label = "HOST HARDWARE",
                        value = telemetry.deviceModel,
                        highlightColor = JarvisTextSecondary
                    )

                    TelemetryRow(
                        label = "LOCAL CHRONO",
                        value = "${telemetry.currentDate} • ${telemetry.currentTime}",
                        highlightColor = JarvisTextSecondary
                    )
                }

                // AI Model Status
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(JarvisSurfaceVariant)
                        .border(1.dp, JarvisHoloBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "NEURAL AI SUB-SYSTEM",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = JarvisCyan
                        )
                        Text(
                            text = "Gemini 3.5 Flash Model Connected • Speech Synthesis Active",
                            fontSize = 12.sp,
                            color = JarvisTextPrimary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dismiss_diagnostics_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "RETURN TO COCKPIT",
                        color = JarvisDarkBackground,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
private fun TelemetryRow(
    label: String,
    value: String,
    highlightColor: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = JarvisTextTertiary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = highlightColor
        )
    }
}
