package com.jobtracker.data.repository

import com.jobtracker.data.db.Reminder
import com.jobtracker.data.db.ReminderDao
import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val reminderDao: ReminderDao) {
    fun getAllReminders() = reminderDao.getAllReminders()
    fun getRemindersForJob(jobId: Long) = reminderDao.getRemindersForJob(jobId)
    suspend fun getDueReminders(timestamp: Long) = reminderDao.getDueReminders(timestamp)
    fun getPendingReminderCount() = reminderDao.getPendingReminderCount()

    suspend fun insertReminder(reminder: Reminder): Long = reminderDao.insertReminder(reminder)
    suspend fun updateReminder(reminder: Reminder) = reminderDao.updateReminder(reminder)
    suspend fun deleteReminder(reminder: Reminder) = reminderDao.deleteReminder(reminder)
    suspend fun markAsCompleted(id: Long) = reminderDao.markAsCompleted(id)
}
