package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDarkSurface
import com.example.ui.theme.JarvisHoloBorder
import com.example.ui.theme.JarvisStarkGold
import com.example.ui.theme.JarvisTextPrimary

data class QuickCommandItem(
    val title: String,
    val command: String,
    val icon: ImageVector,
    val isGold: Boolean = false
)

@Composable
fun QuickCommands(
    onCommandSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val commands = listOf(
        QuickCommandItem("System Diagnostics", "Run a complete diagnostic check and system status report", Icons.Rounded.Speed),
        QuickCommandItem("Mark 85 Status", "Status report on Mark 85 armor and power reserves", Icons.Rounded.Security, isGold = true),
        QuickCommandItem("Battery Reserves", "Check current power cell and battery levels", Icons.Rounded.BatteryChargingFull),
        QuickCommandItem("Current Time", "What is the exact current time and date?", Icons.Rounded.Schedule),
        QuickCommandItem("House Party Protocol", "Initiate House Party Protocol", Icons.Rounded.FlashOn, isGold = true),
        QuickCommandItem("AI Wit", "Tell me a tech joke, Jarvis", Icons.Rounded.Lightbulb)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        commands.forEach { item ->
            val accentColor = if (item.isGold) JarvisStarkGold else JarvisCyan
            AssistChip(
                onClick = { onCommandSelected(item.command) },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 12.sp,
                        color = JarvisTextPrimary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = JarvisDarkSurface,
                    labelColor = JarvisTextPrimary
                ),
                border = AssistChipDefaults.assistChipBorder(
                    enabled = true,
                    borderColor = if (item.isGold) JarvisStarkGold.copy(alpha = 0.4f) else JarvisHoloBorder
                ),
                modifier = Modifier.testTag("quick_command_${item.title.lowercase().replace(" ", "_")}")
            )
        }
    }
}
