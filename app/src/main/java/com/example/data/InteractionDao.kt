package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InteractionDao {
    @Query("SELECT * FROM jarvis_interactions ORDER BY timestamp DESC")
    fun getAllInteractions(): Flow<List<InteractionEntity>>

    @Query("SELECT * FROM jarvis_interactions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentInteractions(limit: Int = 20): Flow<List<InteractionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInteraction(interaction: InteractionEntity): Long

    @Query("DELETE FROM jarvis_interactions WHERE id = :id")
    suspend fun deleteInteraction(id: Long)

    @Query("DELETE FROM jarvis_interactions")
    suspend fun clearAllInteractions()
}
