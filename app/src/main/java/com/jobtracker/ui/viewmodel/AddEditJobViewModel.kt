package com.jobtracker.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jobtracker.data.db.AppDatabase
import com.jobtracker.data.db.Job
import com.jobtracker.data.repository.JobRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddEditJobViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val jobRepo = JobRepository(db.jobDao())

    // Form fields
    private val _companyName = MutableStateFlow("")
    val companyName: StateFlow<String> = _companyName.asStateFlow()

    private val _jobTitle = MutableStateFlow("")
    val jobTitle: StateFlow<String> = _jobTitle.asStateFlow()

    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location.asStateFlow()

    private val _salary = MutableStateFlow("")
    val salary: StateFlow<String> = _salary.asStateFlow()

    private val _jobType = MutableStateFlow("Full-time")
    val jobType: StateFlow<String> = _jobType.asStateFlow()

    private val _source = MutableStateFlow("Manual")
    val source: StateFlow<String> = _source.asStateFlow()

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    private val _deadline = MutableStateFlow<Long?>(null)
    val deadline: StateFlow<Long?> = _deadline.asStateFlow()

    // Edit mode
    private var editingJobId: Long? = null
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    // Validation errors
    private val _companyError = MutableStateFlow<String?>(null)
    val companyError: StateFlow<String?> = _companyError.asStateFlow()

    private val _titleError = MutableStateFlow<String?>(null)
    val titleError: StateFlow<String?> = _titleError.asStateFlow()

    // Save state
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveComplete = MutableStateFlow(false)
    val saveComplete: StateFlow<Boolean> = _saveComplete.asStateFlow()

    private val _savedJobId = MutableStateFlow<Long?>(null)
    val savedJobId: StateFlow<Long?> = _savedJobId.asStateFlow()

    val jobTypes = listOf("Full-time", "Part-time", "Contract", "Internship", "Temporary", "Remote")
    val sources = listOf("Manual", "WhatsApp", "Telegram", "SMS", "Email", "LinkedIn", "Indeed", "Other")

    fun loadJob(jobId: Long) {
        viewModelScope.launch {
            val job = jobRepo.getJobById(jobId)
            if (job != null) {
                editingJobId = job.id
                _isEditing.value = true
                _companyName.value = job.companyName
                _jobTitle.value = job.jobTitle
                _location.value = job.location ?: ""
                _salary.value = job.salary ?: ""
                _jobType.value = job.jobType ?: "Full-time"
                _source.value = job.source
                _notes.value = job.notes ?: ""
                _deadline.value = job.deadline
            }
        }
    }

    fun updateCompanyName(value: String) {
        _companyName.value = value
        if (value.isNotBlank()) _companyError.value = null
    }

    fun updateJobTitle(value: String) {
        _jobTitle.value = value
        if (value.isNotBlank()) _titleError.value = null
    }

    fun updateLocation(value: String) { _location.value = value }
    fun updateSalary(value: String) { _salary.value = value }
    fun updateJobType(value: String) { _jobType.value = value }
    fun updateSource(value: String) { _source.value = value }
    fun updateNotes(value: String) { _notes.value = value }
    fun updateDeadline(value: Long?) { _deadline.value = value }

    fun save() {
        var hasError = false

        if (_companyName.value.isBlank()) {
            _companyError.value = "Company name is required"
            hasError = true
        }
        if (_jobTitle.value.isBlank()) {
            _titleError.value = "Job title is required"
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            _isSaving.value = true
            val job = Job(
                id = editingJobId ?: 0,
                companyName = _companyName.value.trim(),
                jobTitle = _jobTitle.value.trim(),
                location = _location.value.trim().ifBlank { null },
                salary = _salary.value.trim().ifBlank { null },
                jobType = _jobType.value,
                source = _source.value,
                notes = _notes.value.trim().ifBlank { null },
                status = if (editingJobId != null) {
                    jobRepo.getJobById(editingJobId!!)?.status ?: "New"
                } else "New",
                deadline = _deadline.value,
                createdAt = if (editingJobId != null) {
                    jobRepo.getJobById(editingJobId!!)?.createdAt ?: System.currentTimeMillis()
                } else System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            val savedId = if (editingJobId != null) {
                jobRepo.updateJob(job)
                editingJobId!!
            } else {
                jobRepo.insertJob(job)
            }

            _savedJobId.value = savedId
            _saveComplete.value = true
            _isSaving.value = false
        }
    }

    fun reset() {
        _companyName.value = ""
        _jobTitle.value = ""
        _location.value = ""
        _salary.value = ""
        _jobType.value = "Full-time"
        _source.value = "Manual"
        _notes.value = ""
        _deadline.value = null
        _companyError.value = null
        _titleError.value = null
        _isEditing.value = false
        _saveComplete.value = false
        _savedJobId.value = null
        editingJobId = null
    }
}
