package com.jobtracker.reminder

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import com.jobtracker.data.db.Job
import com.jobtracker.data.db.Reminder
import java.util.TimeZone

/**
 * Manages calendar event CRUD via CalendarContract.
 *
 * Events are added to the device's primary calendar (or the first
 * writable calendar found). All operations require the
 * [android.Manifest.permission.WRITE_CALENDAR] permission.
 */
class CalendarSyncManager(private val context: Context) {

    /**
     * Add a calendar event for the given job reminder.
     *
     * @return The calendar event ID if successful, null otherwise.
     */
    fun addEventToCalendar(job: Job, reminder: Reminder): Long? {
        if (!hasCalendarWritePermission()) return null

        val calendarId = getPrimaryCalendarId() ?: return null

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, reminder.remindAt)
            put(CalendarContract.Events.DTEND, reminder.remindAt + 3_600_000L) // 1 hour
            put(CalendarContract.Events.TITLE, buildEventTitle(job))
            put(CalendarContract.Events.DESCRIPTION, buildDescription(job, reminder))
            put(CalendarContract.Events.CALENDAR_ID, calendarId)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        return try {
            val uri = context.contentResolver.insert(
                CalendarContract.Events.CONTENT_URI,
                values
            )
            uri?.lastPathSegment?.toLongOrNull()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Remove a calendar event by its event ID.
     */
    fun removeEventFromCalendar(eventId: Long) {
        try {
            val uri = ContentUris.withAppendedId(
                CalendarContract.Events.CONTENT_URI,
                eventId
            )
            context.contentResolver.delete(uri, null, null)
        } catch (_: Exception) {
            // Ignore deletion failures (e.g. event already removed)
        }
    }

    /**
     * Update an existing calendar event's title, description, and timing.
     */
    fun updateCalendarEvent(
        eventId: Long,
        job: Job,
        reminder: Reminder
    ): Boolean {
        if (!hasCalendarWritePermission()) return false

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, reminder.remindAt)
            put(CalendarContract.Events.DTEND, reminder.remindAt + 3_600_000L)
            put(CalendarContract.Events.TITLE, buildEventTitle(job))
            put(CalendarContract.Events.DESCRIPTION, buildDescription(job, reminder))
        }

        return try {
            val uri = ContentUris.withAppendedId(
                CalendarContract.Events.CONTENT_URI,
                eventId
            )
            val rows = context.contentResolver.update(uri, values, null, null)
            rows > 0
        } catch (_: Exception) {
            false
        }
    }

    // ------------------------------------------------------------------
    // Internal helpers
    // ------------------------------------------------------------------

    private fun hasCalendarWritePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.WRITE_CALENDAR
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    /**
     * Returns the ID of the user's primary calendar, falling back to
     * the first available calendar if no primary is marked.
     */
    private fun getPrimaryCalendarId(): Long? {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.IS_PRIMARY
        )

        val cursor = context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val isPrimary =
                    it.getInt(it.getColumnIndexOrThrow(CalendarContract.Calendars.IS_PRIMARY))
                if (isPrimary == 1) {
                    return it.getLong(
                        it.getColumnIndexOrThrow(CalendarContract.Calendars._ID)
                    )
                }
            }
            // Fallback to first calendar
            if (it.moveToFirst()) {
                return it.getLong(
                    it.getColumnIndexOrThrow(CalendarContract.Calendars._ID)
                )
            }
        }
        return null
    }

    /**
     * Build a display title such as "Apply: Software Engineer at Google".
     */
    private fun buildEventTitle(job: Job): String {
        return "Apply: ${job.jobTitle} at ${job.companyName}"
    }

    private fun buildDescription(job: Job, reminder: Reminder): String {
        return buildString {
            appendLine("Job: ${job.jobTitle} at ${job.companyName}")
            appendLine("Status: ${job.status}")
            job.location?.let { appendLine("Location: $it") }
            job.salary?.let { appendLine("Salary: $it") }
            reminder.note?.let { appendLine("\nNote: $it") }
        }
    }
}
