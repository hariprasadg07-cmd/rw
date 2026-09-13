package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JarvisAlertRed
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDarkSurface
import com.example.ui.theme.JarvisElectricBlue
import com.example.ui.theme.JarvisHoloBorder
import com.example.ui.theme.JarvisStarkGold
import com.example.ui.theme.JarvisSuccess
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import com.example.voice.AssistantState

@Composable
fun HudTopBar(
    state: AssistantState,
    onOpenTelemetry: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (state) {
        AssistantState.IDLE -> JarvisSuccess
        AssistantState.LISTENING -> JarvisCyan
        AssistantState.PROCESSING -> JarvisStarkGold
        AssistantState.SPEAKING -> JarvisElectricBlue
        AssistantState.ERROR -> JarvisAlertRed
    }

    val statusText = when (state) {
        AssistantState.IDLE -> "SYSTEM ONLINE"
        AssistantState.LISTENING -> "AUDIO INPUT ACTIVE"
        AssistantState.PROCESSING -> "PROCESSING QUERY"
        AssistantState.SPEAKING -> "VOCALIZING RESPONSE"
        AssistantState.ERROR -> "SYSTEM NOTICE"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Title and Status
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "J.A.R.V.I.S.",
                    color = JarvisCyan,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                )
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(JarvisDarkSurface)
                        .border(1.dp, JarvisHoloBorder, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "v3.5",
                        color = JarvisStarkGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Status Indicator Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Text(
                    text = statusText,
                    color = JarvisTextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }

        // Action Icons
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onOpenTelemetry,
                modifier = Modifier.testTag("telemetry_button")
            ) {
                Icon(
                    imageVector = Icons.Default.QueryStats,
                    contentDescription = "System Diagnostics",
                    tint = JarvisCyan
                )
            }

            IconButton(
                onClick = onOpenHistory,
                modifier = Modifier.testTag("history_button")
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Command Memory Log",
                    tint = JarvisTextPrimary
                )
            }

            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier.testTag("settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Assistant Settings",
                    tint = JarvisTextPrimary
                )
            }
        }
    }
}
