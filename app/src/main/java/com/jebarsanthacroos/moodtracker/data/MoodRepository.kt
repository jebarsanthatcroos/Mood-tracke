package com.jebarsanthacroos.moodtracker.data

import kotlinx.coroutines.flow.Flow

class MoodRepository(private val moodDao: MoodDao) {

    val allMoodEntries: Flow<List<MoodEntry>> = moodDao.getAllMoodEntries()

    suspend fun getMoodEntryById(id: Long): MoodEntry? {
        return moodDao.getMoodEntryById(id)
    }

    suspend fun insertMoodEntry(entry: MoodEntry) {
        moodDao.insertMoodEntry(entry)
    }

    suspend fun updateMoodEntry(entry: MoodEntry) {
        moodDao.updateMoodEntry(entry)
    }

    suspend fun deleteMoodEntry(entry: MoodEntry) {
        moodDao.deleteMoodEntry(entry)
    }

    fun getMoodEntriesFromDate(startTime: Long): Flow<List<MoodEntry>> {
        return moodDao.getMoodEntriesFromDate(startTime)
    }
}