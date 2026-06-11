package com.jobtracker.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jobtracker.data.db.AppDatabase
import com.jobtracker.data.db.Job
import com.jobtracker.data.db.Reminder
import com.jobtracker.data.repository.JobRepository
import com.jobtracker.data.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JobDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val jobRepo = JobRepository(db.jobDao())
    private val reminderRepo = ReminderRepository(db.reminderDao())

    private val _job = MutableStateFlow<Job?>(null)
    val job: StateFlow<Job?> = _job.asStateFlow()

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _deleteComplete = MutableStateFlow(false)
    val deleteComplete: StateFlow<Boolean> = _deleteComplete.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    fun loadJob(jobId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            val loadedJob = jobRepo.getJobById(jobId)
            _job.value = loadedJob
            _isLoading.value = false
        }
    }

    fun loadReminders(jobId: Long) {
        viewModelScope.launch {
            reminderRepo.getRemindersForJob(jobId).collect { reminderList ->
                _reminders.value = reminderList
            }
        }
    }

    fun updateStatus(newStatus: String) {
        viewModelScope.launch {
            val currentJob = _job.value ?: return@launch
            val updatedJob = currentJob.copy(
                status = newStatus,
                updatedAt = System.currentTimeMillis(),
                appliedDate = if (newStatus == "Applied" && currentJob.appliedDate == null) {
                    System.currentTimeMillis()
                } else currentJob.appliedDate,
                interviewDate = if (newStatus == "Interviewing" && currentJob.interviewDate == null) {
                    System.currentTimeMillis()
                } else currentJob.interviewDate
            )
            jobRepo.updateJob(updatedJob)
            _job.value = updatedJob
            _actionMessage.value = "Status updated to $newStatus"
        }
    }

    fun deleteJob() {
        viewModelScope.launch {
            val currentJob = _job.value ?: return@launch
            jobRepo.deleteJob(currentJob)
            _deleteComplete.value = true
            _actionMessage.value = "Job deleted"
        }
    }

    fun addReminder(title: String, remindAt: Long, note: String? = null) {
        viewModelScope.launch {
            val currentJob = _job.value ?: return@launch
            val reminder = Reminder(
                jobId = currentJob.id,
                title = title,
                note = note,
                remindAt = remindAt
            )
            reminderRepo.insertReminder(reminder)
            _actionMessage.value = "Reminder set"
        }
    }

    fun dismissMessage() {
        _actionMessage.value = null
    }
}
