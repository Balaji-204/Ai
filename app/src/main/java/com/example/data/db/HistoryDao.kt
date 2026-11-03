package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history_items ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryItem>>

    @Query("SELECT * FROM history_items WHERE type = :type ORDER BY timestamp DESC")
    fun getHistoryByType(type: String): Flow<List<HistoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HistoryItem)

    @Query("DELETE FROM history_items WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM history_items")
    suspend fun clearAll()
}
