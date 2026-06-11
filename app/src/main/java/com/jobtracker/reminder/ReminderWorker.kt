package com.jobtracker.reminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.jobtracker.data.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * WorkManager Worker that processes due reminders by checking the database
 * for uncompleted reminders whose remindAt time has passed, and firing
 * notifications for each one.
 */
class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): ListenableWorker.Result = withContext(Dispatchers.IO) {
        try {
            val db = AppDatabase.getInstance(applicationContext)
            val reminderDao = db.reminderDao()
            val notificationHelper = NotificationHelper(applicationContext)

            notificationHelper.createNotificationChannels()

            val dueReminders = reminderDao.getDueReminders(System.currentTimeMillis())

            for (reminder in dueReminders) {
                notificationHelper.showReminderNotification(
                    reminderId = reminder.id,
                    title = reminder.title,
                    note = reminder.note
                )
            }

            ListenableWorker.Result.success()
        } catch (e: Exception) {
            ListenableWorker.Result.retry()
        }
    }
}
