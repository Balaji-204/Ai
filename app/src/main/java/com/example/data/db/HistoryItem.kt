package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_items")
data class HistoryItem(
    @PrimaryKey val id: String,
    val type: String, // "VIDEO", "QR", "SLIDESHOW", "TAMIL_AUDIO"
    val title: String,
    val subtitle: String,
    val detailsJson: String,
    val timestamp: Long = System.currentTimeMillis(),
    val thumbnailBase64: String? = null
)
