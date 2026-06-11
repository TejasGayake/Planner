package com.jobtracker.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jobtracker.data.db.AppDatabase
import com.jobtracker.data.db.Job
import com.jobtracker.data.repository.JobRepository
import com.jobtracker.data.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val jobRepo = JobRepository(db.jobDao())
    private val reminderRepo = ReminderRepository(db.reminderDao())

    // All jobs from repository
    private val allJobs: StateFlow<List<Job>> = jobRepo.allJobs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected filter
    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    // Filtered jobs based on selected filter
    val filteredJobs: StateFlow<List<Job>> = combine(allJobs, selectedFilter) { jobs, filter ->
        if (filter == "All") jobs
        else jobs.filter { it.status == filter }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Statistics
    val totalCount: StateFlow<Int> = allJobs.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val newCount: StateFlow<Int> = allJobs.map { jobs ->
        jobs.count { it.status == "New" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val appliedCount: StateFlow<Int> = allJobs.map { jobs ->
        jobs.count { it.status == "Applied" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val interviewCount: StateFlow<Int> = allJobs.map { jobs ->
        jobs.count { it.status == "Interviewing" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val offerCount: StateFlow<Int> = allJobs.map { jobs ->
        jobs.count { it.status == "Offer" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Pending reminder count
    val pendingReminderCount: StateFlow<Int> = reminderRepo.getPendingReminderCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Refresh trigger
    private val _refreshTrigger = MutableStateFlow(0)
    val refreshTrigger: StateFlow<Int> = _refreshTrigger.asStateFlow()

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun deleteJob(job: Job) {
        viewModelScope.launch {
            jobRepo.deleteJob(job)
        }
    }

    fun refresh() {
        _refreshTrigger.value++
    }

    fun getJobCountForFilter(filter: String): StateFlow<Int> {
        return if (filter == "All") {
            totalCount
        } else {
            allJobs.map { jobs -> jobs.count { it.status == filter } }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
        }
    }
}
