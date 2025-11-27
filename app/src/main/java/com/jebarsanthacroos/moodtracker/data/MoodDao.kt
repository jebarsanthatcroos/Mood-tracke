package com.jebarsanthacroos.moodtracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {
    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    fun getAllMoodEntries(): Flow<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries WHERE id = :id")
    suspend fun getMoodEntryById(id: Long): MoodEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodEntry(entry: MoodEntry)

    @Update
    suspend fun updateMoodEntry(entry: MoodEntry)

    @Delete
    suspend fun deleteMoodEntry(entry: MoodEntry)

    @Query("SELECT * FROM mood_entries WHERE timestamp >= :startTime ORDER BY timestamp DESC")
    fun getMoodEntriesFromDate(startTime: Long): Flow<List<MoodEntry>>
}