package com.example.data

import kotlinx.coroutines.flow.Flow

class QuranRepository(private val quranDao: QuranDao) {
    val allFavorites: Flow<List<FavoriteItem>> = quranDao.getAllFavorites()
    val lastRead: Flow<HistoryItem?> = quranDao.getLastRead()
    val allPersonalNotes: Flow<List<PersonalNote>> = quranDao.getAllPersonalNotes()
    val allKhatmas: Flow<List<KhatmaPlan>> = quranDao.getAllKhatmas()

    suspend fun insertFavorite(favorite: FavoriteItem) {
        quranDao.insertFavorite(favorite)
    }

    suspend fun deleteFavorite(type: String, itemValue: String) {
        quranDao.deleteFavorite(type, itemValue)
    }

    fun isFavorite(type: String, itemValue: String): Flow<Boolean> {
        return quranDao.checkFavoriteExists(type, itemValue)
    }

    suspend fun saveLastRead(surahId: Int, surahName: String, page: Int, juz: Int) {
        val historyItem = HistoryItem(
            surahId = surahId,
            surahName = surahName,
            page = page,
            juz = juz,
            timestamp = System.currentTimeMillis()
        )
        quranDao.insertHistory(historyItem)
    }

    // Personal Notes
    fun getNotesForAyah(surahId: Int, ayahNumber: Int): Flow<List<PersonalNote>> {
        return quranDao.getNotesForAyah(surahId, ayahNumber)
    }

    suspend fun insertPersonalNote(note: PersonalNote) {
        quranDao.insertPersonalNote(note)
    }

    suspend fun deletePersonalNote(noteId: Int) {
        quranDao.deletePersonalNote(noteId)
    }

    // Khatmas
    suspend fun insertKhatma(khatma: KhatmaPlan) {
        quranDao.insertKhatma(khatma)
    }

    suspend fun deleteKhatma(id: Int) {
        quranDao.deleteKhatma(id)
    }
}
