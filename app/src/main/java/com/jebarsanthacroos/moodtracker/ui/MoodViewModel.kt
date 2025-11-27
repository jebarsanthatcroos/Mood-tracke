package com.jebarsanthacroos.moodtracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jebarsanthacroos.moodtracker.data.MoodEntry
import com.jebarsanthacroos.moodtracker.data.MoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class MoodViewModel(private val repository: MoodRepository) : ViewModel() {

    // Available moods for selection
    val availableMoods = listOf(
        "Happy 😊",
        "Calm 🙂",
        "Neutral 😐",
        "Sad 😟",
        "Anxious 😬"
    )

    // State flow for mood entries
    private val _moodEntries = MutableStateFlow<List<MoodEntry>>(emptyList())
    val moodEntries: StateFlow<List<MoodEntry>> = _moodEntries.asStateFlow()

    // State for weekly report data
    private val _weeklyMoodData = MutableStateFlow<Map<String, Int>>(emptyMap())
    val weeklyMoodData: StateFlow<Map<String, Int>> = _weeklyMoodData.asStateFlow()

    init {
        loadMoodEntries()
    }

    private fun loadMoodEntries() {
        viewModelScope.launch {
            repository.allMoodEntries.collect { entries ->
                _moodEntries.value = entries
            }
        }
    }

    fun addMoodEntry(mood: String) {
        viewModelScope.launch {
            val entry = MoodEntry(mood = mood)
            repository.insertMoodEntry(entry)
        }
    }

    fun deleteMoodEntry(entry: MoodEntry) {
        viewModelScope.launch {
            repository.deleteMoodEntry(entry)
        }
    }

    suspend fun getMoodEntryById(id: Long): MoodEntry? {
        return repository.getMoodEntryById(id)
    }

    fun updateMoodEntry(entry: MoodEntry) {
        viewModelScope.launch {
            repository.updateMoodEntry(entry)
        }
    }

    fun loadWeeklyData() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -7)
            val weekAgo = calendar.timeInMillis

            repository.getMoodEntriesFromDate(weekAgo).collect { entries ->
                val moodFrequency = mutableMapOf<String, Int>()

                availableMoods.forEach { mood ->
                    moodFrequency[mood] = 0
                }

                entries.forEach { entry ->
                    moodFrequency[entry.mood] = (moodFrequency[entry.mood] ?: 0) + 1
                }

                _weeklyMoodData.value = moodFrequency
            }
        }
    }
}