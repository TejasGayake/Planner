package com.jobtracker.reminder

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Unified interface for scheduling job reminders.
 *
 * Uses WorkManager for deferrable reminders. For exact-timing reminders
 * (e.g., 5 minutes before an interview), use [scheduleExactReminder] which
 * falls back to AlarmManager.
 */
class ReminderScheduler(private val context: Context) {

    /**
     * Schedule a one-time reminder using WorkManager.
     * The work request is named uniquely per reminder ID so it can be
     * updated or cancelled independently.
     *
     * @param reminderId  Unique identifier for the reminder.
     * @param remindAt    Epoch millis when the reminder should fire.
     */
    fun scheduleReminder(reminderId: Long, remindAt: Long) {
        val delay = remindAt - System.currentTimeMillis()
        if (delay <= 0) return // already past, skip

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag("reminder_$reminderId")
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "reminder_$reminderId",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
    }

    /**
     * Cancel a previously scheduled reminder.
     */
    fun cancelReminder(reminderId: Long) {
        WorkManager.getInstance(context)
            .cancelUniqueWork("reminder_$reminderId")
    }

    /**
     * Cancel all reminders tagged with "job_reminders".
     */
    fun cancelAllReminders() {
        WorkManager.getInstance(context)
            .cancelAllWorkByTag("job_reminders")
    }
}
