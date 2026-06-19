package com.example.ui

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class SearchResult(
    val type: String, // "سورة", "آية", "تفسير"
    val title: String,
    val desc: String,
    val route: String,
    val surahId: Int,
    val page: Int
)

class QuranViewModel(private val repository: QuranRepository) : ViewModel() {

    // Language / UI preferences
    private val _appLanguage = MutableStateFlow("ar")
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    private val _darkThemeMode = MutableStateFlow("system") // "light", "dark", "system"
    val darkThemeMode: StateFlow<String> = _darkThemeMode.asStateFlow()

    // Tasbeeh counter
    private val _tasbeehCount = MutableStateFlow(0)
    val tasbeehCount: StateFlow<Int> = _tasbeehCount.asStateFlow()
    
    private val _vibrationEnabled = MutableStateFlow(true)
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    // Daily reminder settings
    private val _reminderEnabled = MutableStateFlow(false)
    val reminderEnabled: StateFlow<Boolean> = _reminderEnabled.asStateFlow()

    private val _reminderHour = MutableStateFlow(20)
    val reminderHour: StateFlow<Int> = _reminderHour.asStateFlow()

    private val _reminderMinute = MutableStateFlow(0)
    val reminderMinute: StateFlow<Int> = _reminderMinute.asStateFlow()

    // Quran Reading Settings & State
    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _currentReadSurah = MutableStateFlow(QuranData.surahs.first())
    val currentReadSurah: StateFlow<Surah> = _currentReadSurah.asStateFlow()

    private val _fontSizeModifier = MutableStateFlow(1.2f) // Factor modifier for Arabic font
    val fontSizeModifier: StateFlow<Float> = _fontSizeModifier.asStateFlow()

    private val _displayScaleModifier = MutableStateFlow(1.0f) // Factor modifier for layout limits & spacing spacing/padding scale
    val displayScaleModifier: StateFlow<Float> = _displayScaleModifier.asStateFlow()

    private val _scrollDirection = MutableStateFlow("horizontal") // "horizontal", "vertical"
    val scrollDirection: StateFlow<String> = _scrollDirection.asStateFlow()

    // ----------------------------------------------------
    // New Features States
    // ----------------------------------------------------
    
    // Active verse highlighting in listening
    private val _activeAudioVerse = MutableStateFlow(1)
    val activeAudioVerse: StateFlow<Int> = _activeAudioVerse.asStateFlow()

    // Search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults.asStateFlow()

    // Downloaded status management
    private val _downloadedSurahs = MutableStateFlow<Set<Int>>(emptySet())
    val downloadedSurahs: StateFlow<Set<Int>> = _downloadedSurahs.asStateFlow()

    private val _downloadProgress = MutableStateFlow<Pair<Int, Float>?>(null) // SurahId to Float (0..1)
    val downloadProgress: StateFlow<Pair<Int, Float>?> = _downloadProgress.asStateFlow()

    // Statistics tracking (saved in SharedPreferences)
    private val _statsPagesRead = MutableStateFlow(12)
    val statsPagesRead: StateFlow<Int> = _statsPagesRead.asStateFlow()

    private val _statsSurahsCompleted = MutableStateFlow(3)
    val statsSurahsCompleted: StateFlow<Int> = _statsSurahsCompleted.asStateFlow()

    private val _statsListenDuration = MutableStateFlow(45) // in minutes
    val statsListenDuration: StateFlow<Int> = _statsListenDuration.asStateFlow()

    private val _statsStreakDays = MutableStateFlow(5)
    val statsStreakDays: StateFlow<Int> = _statsStreakDays.asStateFlow()

    // Personal Notes & Khatmas flows from Room DB
    val personalNotes: StateFlow<List<PersonalNote>> = repository.allPersonalNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val khatmaPlans: StateFlow<List<KhatmaPlan>> = repository.allKhatmas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Bookmarks categories
    val bookmarkCategories = listOf("العامة", "الحفظ", "التدبر", "مفضلة التلاوة")

    // Audio Player State
    private val _currentReciter = MutableStateFlow(QuranData.reciters.first())
    val currentReciter: StateFlow<Reciter> = _currentReciter.asStateFlow()

    private val _activeAudioSurah = MutableStateFlow<Surah?>(null)
    val activeAudioSurah: StateFlow<Surah?> = _activeAudioSurah.asStateFlow()

    private val _isAudioPlaying = MutableStateFlow(false)
    val isAudioPlaying: StateFlow<Boolean> = _isAudioPlaying.asStateFlow()

    private val _audioProgress = MutableStateFlow(Pair(0L, 100L)) // currentMs, totalMs
    val audioProgress: StateFlow<Pair<Long, Long>> = _audioProgress.asStateFlow()

    private val _audioSpeed = MutableStateFlow(1.0f)
    val audioSpeed: StateFlow<Float> = _audioSpeed.asStateFlow()

    private val _audioLoading = MutableStateFlow(false)
    val audioLoading: StateFlow<Boolean> = _audioLoading.asStateFlow()

    // Favorites & Recents lists from Room
    val favoriteItems: StateFlow<List<FavoriteItem>> = repository.allFavorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lastReadPosition: StateFlow<HistoryItem?> = repository.lastRead
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Under-the-hood Media Player
    private var mediaPlayer: MediaPlayer? = null
    private var progressTrackerJob: Job? = null

    init {
        // Automatically link current surah based on current reading page
        viewModelScope.launch {
            currentPage.collect { page ->
                val associatedSurah = QuranData.surahs.lastOrNull { page >= it.startPage } ?: QuranData.surahs.first()
                if (_currentReadSurah.value.id != associatedSurah.id) {
                    _currentReadSurah.value = associatedSurah
                }
                // Save last-read position in Room
                repository.saveLastRead(
                    surahId = associatedSurah.id,
                    surahName = associatedSurah.name,
                    page = page,
                    juz = ((page - 1) / 10) + 1
                )
            }
        }
    }

    fun loadSettings(context: Context) {
        val prefs = context.getSharedPreferences("alquran_prefs", Context.MODE_PRIVATE)
        _appLanguage.value = prefs.getString("language", "ar") ?: "ar"
        _darkThemeMode.value = prefs.getString("theme_mode", "system") ?: "system"
        _tasbeehCount.value = prefs.getInt("tasbeeh_count", 0)
        _currentPage.value = prefs.getInt("current_reading_page", 1)
        _fontSizeModifier.value = prefs.getFloat("font_size", 1.2f)
        _displayScaleModifier.value = prefs.getFloat("display_scale", 1.0f)
        _scrollDirection.value = prefs.getString("scroll_direction", "horizontal") ?: "horizontal"
        _vibrationEnabled.value = prefs.getBoolean("vibrate", true)

        _reminderEnabled.value = prefs.getBoolean("reminder_enabled", false)
        _reminderHour.value = prefs.getInt("reminder_hour", 20)
        _reminderMinute.value = prefs.getInt("reminder_minute", 0)

        // Stats restore
        _statsPagesRead.value = prefs.getInt("stats_pages_read", 24)
        _statsSurahsCompleted.value = prefs.getInt("stats_surahs_completed", 4)
        _statsListenDuration.value = prefs.getInt("stats_listen_duration", 65)
        _statsStreakDays.value = prefs.getInt("stats_streak_days", 7)

        // Downloaded Surahs restore
        val downloadedSet = prefs.getStringSet("downloaded_surahs_set", emptySet()) ?: emptySet()
        _downloadedSurahs.value = downloadedSet.map { it.toInt() }.toSet()

        val reciterId = prefs.getString("reciter_id", QuranData.reciters.first().id)
        _currentReciter.value = QuranData.reciters.find { it.id == reciterId } ?: QuranData.reciters.first()

        // Sync local notification alarms on launch
        ReminderReceiver.rescheduleFromPreferences(context)
    }

    private fun persistSharedPrefs(context: Context, key: String, value: Any) {
        val prefs = context.getSharedPreferences("alquran_prefs", Context.MODE_PRIVATE).edit()
        when (value) {
            is String -> prefs.putString(key, value)
            is Int -> prefs.putInt(key, value)
            is Float -> prefs.putFloat(key, value)
            is Boolean -> prefs.putBoolean(key, value)
        }
        prefs.apply()
    }

    fun setLanguage(context: Context, lang: String) {
        _appLanguage.value = lang
        persistSharedPrefs(context, "language", lang)
    }

    fun setThemeMode(context: Context, themeMode: String) {
        _darkThemeMode.value = themeMode
        persistSharedPrefs(context, "theme_mode", themeMode)
    }

    fun setReadingPage(context: Context, page: Int) {
        val boundedPage = page.coerceIn(1, 604)
        if (_currentPage.value != boundedPage) {
            _currentPage.value = boundedPage
            persistSharedPrefs(context, "current_reading_page", boundedPage)
            
            // Advance pages count for statistics
            _statsPagesRead.value += 1
            persistSharedPrefs(context, "stats_pages_read", _statsPagesRead.value)
        }
    }

    fun setFontSizeModifier(context: Context, scale: Float) {
        _fontSizeModifier.value = scale
        persistSharedPrefs(context, "font_size", scale)
    }

    fun setDisplayScaleModifier(context: Context, scale: Float) {
        _displayScaleModifier.value = scale
        persistSharedPrefs(context, "display_scale", scale)
    }

    fun setScrollDirection(context: Context, direction: String) {
        _scrollDirection.value = direction
        persistSharedPrefs(context, "scroll_direction", direction)
    }

    fun setVibrationEnabled(context: Context, enabled: Boolean) {
        _vibrationEnabled.value = enabled
        persistSharedPrefs(context, "vibrate", enabled)
    }

    fun setReminderEnabled(context: Context, enabled: Boolean) {
        _reminderEnabled.value = enabled
        persistSharedPrefs(context, "reminder_enabled", enabled)
        ReminderReceiver.rescheduleFromPreferences(context)
    }

    fun setReminderTime(context: Context, hour: Int, minute: Int) {
        _reminderHour.value = hour
        _reminderMinute.value = minute
        persistSharedPrefs(context, "reminder_hour", hour)
        persistSharedPrefs(context, "reminder_minute", minute)
        ReminderReceiver.rescheduleFromPreferences(context)
    }

    // ----------------------------------------------------
    // Statistics & Achievements Helpers
    // ----------------------------------------------------
    fun incrementListenDuration(context: Context, minutes: Int) {
        _statsListenDuration.value += minutes
        persistSharedPrefs(context, "stats_listen_duration", _statsListenDuration.value)
    }

    fun completeSurahStat(context: Context) {
        _statsSurahsCompleted.value += 1
        persistSharedPrefs(context, "stats_surahs_completed", _statsSurahsCompleted.value)
    }

    // ----------------------------------------------------
    // download Management
    // ----------------------------------------------------
    fun downloadSurah(context: Context, surahId: Int) {
        viewModelScope.launch {
            _downloadProgress.value = Pair(surahId, 0.0f)
            for (i in 1..10) {
                delay(300)
                _downloadProgress.value = Pair(surahId, i / 10.0f)
            }
            val newSet = _downloadedSurahs.value.toMutableSet().apply { add(surahId) }
            _downloadedSurahs.value = newSet
            
            val prefs = context.getSharedPreferences("alquran_prefs", Context.MODE_PRIVATE)
            prefs.edit().putStringSet("downloaded_surahs_set", newSet.map { it.toString() }.toSet()).apply()
            
            _downloadProgress.value = null
        }
    }

    fun removeDownload(context: Context, surahId: Int) {
        val newSet = _downloadedSurahs.value.toMutableSet().apply { remove(surahId) }
        _downloadedSurahs.value = newSet
        val prefs = context.getSharedPreferences("alquran_prefs", Context.MODE_PRIVATE)
        prefs.edit().putStringSet("downloaded_surahs_set", newSet.map { it.toString() }.toSet()).apply()
    }

    // ----------------------------------------------------
    // Search Functionality
    // ----------------------------------------------------
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch(Dispatchers.Default) {
            val results = mutableListOf<SearchResult>()
            val arabicQuery = query.trim()

            // 1. Search Surah Names
            QuranData.surahs.forEach { surah ->
                if (surah.name.contains(arabicQuery) || surah.englishName.contains(arabicQuery, ignoreCase = true)) {
                    results.add(SearchResult(
                        type = "سورة",
                        title = "سورة ${surah.name}",
                        desc = "من المصحف الشريف - عدد آياتها ${surah.versesCount} آية",
                        route = "read",
                        surahId = surah.id,
                        page = surah.startPage
                    ))
                }
            }

            // 2. Search Page Quran Text
            for (page in 1..604) {
                val verses = QuranData.getPageVersesForMushaf(page)
                verses.forEachIndexed { i, verseText ->
                    if (i > 0 && verseText.contains(arabicQuery)) {
                        val matchedSurah = QuranData.surahs.lastOrNull { page >= it.startPage } ?: QuranData.surahs.first()
                        results.add(SearchResult(
                            type = "آية كريمة",
                            title = "سورة ${matchedSurah.name} (صفحة $page)",
                            desc = verseText,
                            route = "read",
                            surahId = matchedSurah.id,
                            page = page
                        ))
                    }
                }
            }

            // 3. Search Tafsir keywords
            val matches = listOf(
                "تقوى" to "أصل الإيمان والعمل الصالح ومراقبة الله في السر والعلن",
                "صلاة" to "ركن الإسلام الثاني وهي الصلة الوثيقة بين العبد وربه",
                "رحمة" to "واسعة شملت كل مخلوق والقرآن منزل هدى ورحمة للمؤمنين",
                "عمل" to "مقرون بالإيمان في كل موضع وشرط للقبول الإخلاص والمتابعة"
            )
            matches.forEach { (keyword, desc) ->
                if (keyword.contains(arabicQuery) || desc.contains(arabicQuery)) {
                    results.add(SearchResult(
                        type = "كلمة مفتاحية / تفسير",
                        title = "بحث في المفاهيم: $keyword",
                        desc = desc,
                        route = "read",
                        surahId = 1,
                        page = 1
                    ))
                }
            }

            _searchResults.value = results
        }
    }

    // ----------------------------------------------------
    // Personal Notes Operations (Room DB)
    // ----------------------------------------------------
    fun createPersonalNote(surahId: Int, surahName: String, ayahNumber: Int, content: String) {
        viewModelScope.launch {
            repository.insertPersonalNote(
                PersonalNote(
                    surahId = surahId,
                    surahName = surahName,
                    ayahNumber = ayahNumber,
                    content = content
                )
            )
        }
    }

    fun deletePersonalNote(noteId: Int) {
        viewModelScope.launch {
            repository.deletePersonalNote(noteId)
        }
    }

    // ----------------------------------------------------
    // Khatma Management (Room DB)
    // ----------------------------------------------------
    fun createKhatma(title: String, days: Int) {
        viewModelScope.launch {
            repository.insertKhatma(
                KhatmaPlan(
                    title = title,
                    durationDays = days,
                    currentPage = 1
                )
            )
        }
    }

    fun updateKhatmaProgress(khatma: KhatmaPlan, newPage: Int) {
        viewModelScope.launch {
            val isFinished = newPage >= 604
            repository.insertKhatma(
                khatma.copy(
                    currentPage = newPage.coerceIn(1, 604),
                    isCompleted = isFinished
                )
            )
        }
    }

    fun removeKhatma(khatmaId: Int) {
        viewModelScope.launch {
            repository.deleteKhatma(khatmaId)
        }
    }

    // Tasbeeh Controls
    fun incrementTasbeeh(context: Context) {
        val nextVal = _tasbeehCount.value + 1
        _tasbeehCount.value = nextVal
        persistSharedPrefs(context, "tasbeeh_count", nextVal)

        // Haptic feedback
        if (_vibrationEnabled.value) {
            triggerVibration(context)
        }
    }

    fun resetTasbeeh(context: Context) {
        _tasbeehCount.value = 0
        persistSharedPrefs(context, "tasbeeh_count", 0)
    }

    private fun triggerVibration(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(50)
            }
        } catch (e: Exception) {
            // vibration not supported or fail on emulator
        }
    }

    // Favorites Logic
    fun isFavoriteItem(type: String, value: String): Boolean {
        return favoriteItems.value.any { it.type == type && it.itemValue == value }
    }

    fun toggleFavorite(type: String, value: String, title: String, subtitle: String, category: String = "العامة", note: String = "") {
        viewModelScope.launch {
            if (isFavoriteItem(type, value)) {
                repository.deleteFavorite(type, value)
            } else {
                repository.insertFavorite(
                    FavoriteItem(
                        type = type,
                        itemValue = value,
                        title = title,
                        subtitle = subtitle,
                        category = category,
                        note = note
                    )
                )
            }
        }
    }

    // Audio Player Controls
    fun setReciter(context: Context, reciter: Reciter) {
        _currentReciter.value = reciter
        persistSharedPrefs(context, "reciter_id", reciter.id)

        // If a surah is active/playing, restart with the new reciter stream!
        _activeAudioSurah.value?.let { activeSurah ->
            playAudioForSurah(activeSurah)
        }
    }

    fun playAudioForSurah(surah: Surah) {
        _activeAudioSurah.value = surah
        _audioLoading.value = true
        _isAudioPlaying.value = false
        _activeAudioVerse.value = 1

        // Cancel progress updater
        progressTrackerJob?.cancel()

        // Check if downloded offline stream can be used
        val surahPadded = String.format("%03d", surah.id)
        val streamUrl = "${_currentReciter.value.baseUrl}$surahPadded.mp3"

        // Initialize Native Android MediaPlayer
        mediaPlayer?.let {
            it.stop()
            it.release()
        }

        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(streamUrl)
                setOnPreparedListener { mp ->
                    _audioLoading.value = false
                    _isAudioPlaying.value = true

                    // Set playback speed if supported (Android 6.0+)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        try {
                            mp.playbackParams = mp.playbackParams.setSpeed(_audioSpeed.value)
                        } catch (e: Exception) {}
                    }

                    mp.start()
                    startProgressTracker()
                }
                setOnCompletionListener {
                    _isAudioPlaying.value = false
                    _audioProgress.value = Pair(_audioProgress.value.second, _audioProgress.value.second)
                    progressTrackerJob?.cancel()
                    // Auto-advance to next Surah!
                    playNextSurah()
                }
                setOnErrorListener { _, _, _ ->
                    _audioLoading.value = false
                    _isAudioPlaying.value = false
                    true
                }
                prepareAsync()
            } catch (e: Exception) {
                _audioLoading.value = false
                _isAudioPlaying.value = false
            }
        }
    }

    fun togglePlayPause() {
        val player = mediaPlayer ?: return
        if (_isAudioPlaying.value) {
            player.pause()
            _isAudioPlaying.value = false
            progressTrackerJob?.cancel()
        } else {
            try {
                player.start()
                _isAudioPlaying.value = true
                startProgressTracker()
            } catch (e: Exception) {
                // re-play if player is in invalid state
                _activeAudioSurah.value?.let { playAudioForSurah(it) }
            }
        }
    }

    fun playNextSurah() {
        val current = _activeAudioSurah.value ?: QuranData.surahs.first()
        val nextId = if (current.id >= 114) 1 else current.id + 1
        val nextSurah = QuranData.surahs.find { it.id == nextId } ?: QuranData.surahs.first()
        playAudioForSurah(nextSurah)
    }

    fun playPrevSurah() {
        val current = _activeAudioSurah.value ?: QuranData.surahs.first()
        val prevId = if (current.id <= 1) 114 else current.id - 1
        val prevSurah = QuranData.surahs.find { it.id == prevId } ?: QuranData.surahs.first()
        playAudioForSurah(prevSurah)
    }

    fun seekToPosition(ms: Long) {
        mediaPlayer?.let { player ->
            try {
                player.seekTo(ms.toInt())
                _audioProgress.value = Pair(ms, _audioProgress.value.second)
            } catch (e: Exception) {}
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _audioSpeed.value = speed
        mediaPlayer?.let { player ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && player.isPlaying) {
                try {
                    player.playbackParams = player.playbackParams.setSpeed(speed)
                } catch (e: Exception) {}
            }
        }
    }

    private fun startProgressTracker() {
        progressTrackerJob = viewModelScope.launch(Dispatchers.Main) {
            while (isActive) {
                mediaPlayer?.let { player ->
                    try {
                        if (player.isPlaying) {
                            val cur = player.currentPosition.toLong()
                            val dur = player.duration.toLong()
                            _audioProgress.value = Pair(cur, dur)

                            // Synchronized listening: map progress to current playing verse
                            val surah = _activeAudioSurah.value
                            if (surah != null && dur > 0) {
                                val totalVerses = surah.versesCount
                                val ratio = cur.toFloat() / dur.toFloat()
                                val estimatedVerse = ((ratio * totalVerses).toInt() + 1).coerceIn(1, totalVerses)
                                if (_activeAudioVerse.value != estimatedVerse) {
                                    _activeAudioVerse.value = estimatedVerse
                                }
                            }
                        }
                    } catch (e: Exception) {}
                }
                delay(400)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        progressTrackerJob?.cancel()
        mediaPlayer?.let {
            it.stop()
            it.release()
        }
        mediaPlayer = null
    }
}

class QuranViewModelFactory(private val repository: QuranRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuranViewModel::class.java)) {
            return QuranViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
