package com.jobtracker.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "reminders",
    foreignKeys = [ForeignKey(
        entity = Job::class,
        parentColumns = ["id"],
        childColumns = ["jobId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("jobId")]
)
@Serializable
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val jobId: Long,
    val title: String,
    val note: String? = null,
    val remindAt: Long,
    val isSyncedToCalendar: Boolean = false,
    val calendarEventId: Long? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
