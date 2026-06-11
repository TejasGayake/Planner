package com.jobtracker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY remindAt ASC")
    fun getAllReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE jobId = :jobId ORDER BY remindAt ASC")
    fun getRemindersForJob(jobId: Long): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE remindAt <= :timestamp AND isCompleted = 0")
    suspend fun getDueReminders(timestamp: Long): List<Reminder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder): Long

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Query("UPDATE reminders SET isCompleted = 1 WHERE id = :id")
    suspend fun markAsCompleted(id: Long)

    @Query("SELECT COUNT(*) FROM reminders WHERE isCompleted = 0")
    fun getPendingReminderCount(): Flow<Int>
}
