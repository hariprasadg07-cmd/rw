package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.InteractionEntity
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorySheet(
    interactions: List<InteractionEntity>,
    onReplayAudio: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = remember(interactions, searchQuery) {
        if (searchQuery.isBlank()) {
            interactions
        } else {
            interactions.filter {
                it.query.contains(searchQuery, ignoreCase = true) ||
                it.response.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val timeFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = JarvisDarkSurface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .background(JarvisHoloBorder, RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .testTag("history_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "NEURAL LOGS",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = JarvisStarkGold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Interaction Memory",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = JarvisCyan
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Memory Logs",
                        tint = JarvisTextSecondary
                    )
                }
            }

            // Search Filter
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Filter memory transmissions...",
                        fontSize = 13.sp,
                        color = JarvisTextTertiary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = JarvisCyan,
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = JarvisCyan,
                    unfocusedBorderColor = JarvisHoloBorder,
                    focusedTextColor = JarvisTextPrimary,
                    unfocusedTextColor = JarvisTextPrimary,
                    cursorColor = JarvisCyan,
                    focusedContainerColor = JarvisDarkBackground,
                    unfocusedContainerColor = JarvisDarkBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .testTag("history_search_field")
            )

            // Content List
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = JarvisTextTertiary,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = if (searchQuery.isNotBlank()) "No matching telemetry logs found." else "Memory logs are empty, Sir.",
                            color = JarvisTextSecondary,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("history_item_${item.id}"),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = JarvisDarkBackground),
                            border = androidx.compose.foundation.BorderStroke(1.dp, JarvisHoloBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = timeFormat.format(Date(item.timestamp)),
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = JarvisTextTertiary
                                    )
                                    IconButton(
                                        onClick = { onReplayAudio(item.response) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = "Speak this log entry",
                                            tint = JarvisElectricBlue,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "USER: ${item.query}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = JarvisCyan
                                )

                                Text(
                                    text = "JARVIS: ${item.response}",
                                    fontSize = 12.sp,
                                    color = JarvisTextPrimary,
                                    modifier = Modifier.padding(top = 4.dp),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
