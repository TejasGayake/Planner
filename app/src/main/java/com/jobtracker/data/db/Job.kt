package com.jobtracker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "jobs")
@Serializable
data class Job(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val companyName: String,
    val jobTitle: String,
    val location: String? = null,
    val salary: String? = null,
    val jobType: String? = null, // Full-time, Part-time, Contract, etc.
    val source: String,          // WhatsApp, Telegram, SMS, Manual
    val sourceUrl: String? = null,
    val notes: String? = null,
    val status: String = "New",  // New, Applied, Interviewing, Offer, Rejected, Archived
    val appliedDate: Long? = null,
    val interviewDate: Long? = null,
    val deadline: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
