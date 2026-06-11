package com.jobtracker.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jobtracker.data.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver that re-schedules all pending reminders after a
 * device reboot. WorkManager tasks are lost on reboot, so this receiver
 * re-enqueues them for the next 30-day window.
 *
 * Requires the [android.Manifest.permission.RECEIVE_BOOT_COMPLETED]
 * permission (already declared in AndroidManifest.xml).
 */
class ReminderBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                val reminders = db.reminderDao().getDueReminders(
                    System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000 // next 30 days
                )

                val scheduler = ReminderScheduler(context)
                for (reminder in reminders) {
                    if (reminder.remindAt > System.currentTimeMillis()) {
                        scheduler.scheduleReminder(reminder.id, reminder.remindAt)
                    }
                }
            } catch (_: Exception) {
                // Silently handle reboot scheduling failures
            }
        }
    }
}
