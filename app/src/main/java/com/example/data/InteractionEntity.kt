package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jarvis_interactions")
data class InteractionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val query: String,
    val response: String,
    val isVoice: Boolean = true,
    val latencyMs: Long = 0,
    val category: String = "GENERAL"
)
