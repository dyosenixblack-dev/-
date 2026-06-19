package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranDao {
    // Favorites queries
    @Query("SELECT * FROM favorites ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteItem)

    @Query("DELETE FROM favorites WHERE type = :type AND itemValue = :itemValue")
    suspend fun deleteFavorite(type: String, itemValue: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE type = :type AND itemValue = :itemValue)")
    fun checkFavoriteExists(type: String, itemValue: String): Flow<Boolean>

    // Last Read queries
    @Query("SELECT * FROM history WHERE id = 'last_read' LIMIT 1")
    fun getLastRead(): Flow<HistoryItem?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(historyItem: HistoryItem)

    // Personal Notes queries
    @Query("SELECT * FROM personal_notes ORDER BY timestamp DESC")
    fun getAllPersonalNotes(): Flow<List<PersonalNote>>

    @Query("SELECT * FROM personal_notes WHERE surahId = :surahId AND ayahNumber = :ayahNumber")
    fun getNotesForAyah(surahId: Int, ayahNumber: Int): Flow<List<PersonalNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonalNote(note: PersonalNote)

    @Query("DELETE FROM personal_notes WHERE id = :noteId")
    suspend fun deletePersonalNote(noteId: Int)

    // Khatma queries
    @Query("SELECT * FROM khatma_plans ORDER BY startTimestamp DESC")
    fun getAllKhatmas(): Flow<List<KhatmaPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKhatma(khatma: KhatmaPlan)

    @Query("DELETE FROM khatma_plans WHERE id = :id")
    suspend fun deleteKhatma(id: Int)
}
