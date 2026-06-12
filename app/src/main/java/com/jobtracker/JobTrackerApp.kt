package com.jobtracker

import android.app.Application
import com.jobtracker.reminder.NotificationHelper

class JobTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Create notification channels on app start
        NotificationHelper(this).createNotificationChannels()
    }
}
