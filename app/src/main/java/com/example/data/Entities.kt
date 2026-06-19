package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "surah", "page", "reciter", "ayah"
    val itemValue: String, // the item ID or value (e.g. Surah ID, Page number, Reciter ID, or SurahId_AyahNumber)
    val title: String,
    val subtitle: String,
    val category: String = "العامة", // Folder or category tag
    val note: String = "", // Added personal tag or bookmark comment
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "history")
data class HistoryItem(
    @PrimaryKey val id: String = "last_read",
    val surahId: Int,
    val surahName: String,
    val page: Int,
    val juz: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "personal_notes")
data class PersonalNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val surahId: Int,
    val surahName: String,
    val ayahNumber: Int,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "khatma_plans")
data class KhatmaPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val durationDays: Int, // 7, 15, 30, custom
    val startPage: Int = 1,
    val currentPage: Int = 1,
    val startTimestamp: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val notificationTime: String = "20:00"
)
