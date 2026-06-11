package com.jobtracker.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.jobtracker.ui.MainActivity

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "job_reminders"
        const val CHANNEL_NAME = "Job Reminders"
        const val CHANNEL_DESC = "Reminders for job applications"
        const val CHANNEL_INTERVIEWS = "job_interviews"
        const val CHANNEL_DEADLINES = "job_deadlines"
        const val CHANNEL_FOLLOW_UPS = "job_follow_ups"
    }

    /**
     * Create all notification channels required for job reminders.
     * Safe to call multiple times — Android ignores re-creation of existing channels.
     */
    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channels = listOf(
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = CHANNEL_DESC
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_INTERVIEWS,
                    "Interviews",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Reminders for upcoming interviews"
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_DEADLINES,
                    "Job Deadlines",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Deadline reminders for job applications"
                },
                NotificationChannel(
                    CHANNEL_FOLLOW_UPS,
                    "Follow-ups",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Follow-up reminders for applied jobs"
                }
            )

            channels.forEach { channel ->
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    /**
     * Show a notification for a job reminder.
     * Tapping the notification opens MainActivity with the reminder_id extra.
     */
    fun showReminderNotification(
        reminderId: Long,
        title: String,
        note: String?,
        channelId: String = CHANNEL_ID
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("reminder_id", reminderId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(note ?: "Job application reminder")
            .setStyle(NotificationCompat.BigTextStyle().bigText(note ?: "Job application reminder"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(reminderId.toInt(), notification)
    }
}
