package com.jobtracker.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jobtracker.data.db.AppDatabase
import com.jobtracker.data.db.Job
import com.jobtracker.data.db.Reminder
import com.jobtracker.data.repository.JobRepository
import com.jobtracker.data.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream

@Serializable
data class ExportData(
    val jobs: List<Job>,
    val reminders: List<Reminder>,
    val exportVersion: String = "1.0",
    val exportedAt: Long = System.currentTimeMillis()
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val jobRepo = JobRepository(db.jobDao())
    private val reminderRepo = ReminderRepository(db.reminderDao())

    private val _notificationEnabled = MutableStateFlow(true)
    val notificationEnabled: StateFlow<Boolean> = _notificationEnabled.asStateFlow()

    private val _defaultReminderMinutes = MutableStateFlow(30)
    val defaultReminderMinutes: StateFlow<Int> = _defaultReminderMinutes.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> = _isImporting.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val prefs = application.getSharedPreferences("job_tracker_prefs", Context.MODE_PRIVATE)

    init {
        _notificationEnabled.value = prefs.getBoolean("notifications_enabled", true)
        _defaultReminderMinutes.value = prefs.getInt("default_reminder_minutes", 30)
        _isDarkTheme.value = prefs.getBoolean("dark_theme", true)
    }

    fun toggleNotifications(enabled: Boolean) {
        _notificationEnabled.value = enabled
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    fun setDefaultReminderMinutes(minutes: Int) {
        _defaultReminderMinutes.value = minutes
        prefs.edit().putInt("default_reminder_minutes", minutes).apply()
    }

    fun toggleDarkTheme(dark: Boolean) {
        _isDarkTheme.value = dark
        prefs.edit().putBoolean("dark_theme", dark).apply()
    }

    fun exportData() {
        viewModelScope.launch {
            _isExporting.value = true
            try {
                val context = getApplication<Application>()
                val json = Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                }

                // Collect a single value from each flow
                val currentJobs = jobRepo.allJobs.first()
                val currentReminders = reminderRepo.getAllReminders().first()

                val exportData = ExportData(
                    jobs = currentJobs,
                    reminders = currentReminders
                )

                val jsonString = json.encodeToString(ExportData.serializer(), exportData)
                val exportFile = File(context.getExternalFilesDir(null), "job_tracker_export.json")
                FileOutputStream(exportFile).use { it.write(jsonString.toByteArray()) }

                _statusMessage.value = "Exported ${currentJobs.size} jobs to ${exportFile.absolutePath}"
                _isExporting.value = false
            } catch (e: Exception) {
                _statusMessage.value = "Export failed: ${e.message}"
                _isExporting.value = false
            }
        }
    }

    fun importData(jsonString: String) {
        viewModelScope.launch {
            _isImporting.value = true
            try {
                val json = Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                }
                val exportData = json.decodeFromString(ExportData.serializer(), jsonString)

                // Import jobs with new IDs
                var importedCount = 0
                for (job in exportData.jobs) {
                    val newJob = job.copy(id = 0, createdAt = System.currentTimeMillis())
                    jobRepo.insertJob(newJob)
                    importedCount++
                }

                _statusMessage.value = "Imported $importedCount jobs"
                _isImporting.value = false
            } catch (e: Exception) {
                _statusMessage.value = "Import failed: ${e.message}"
                _isImporting.value = false
            }
        }
    }

    fun dismissMessage() {
        _statusMessage.value = null
    }

    fun clearAllData() {
        viewModelScope.launch {
            try {
                val currentJobs = jobRepo.allJobs.first()
                currentJobs.forEach { jobRepo.deleteJob(it) }
                _statusMessage.value = "All data cleared"
            } catch (e: Exception) {
                _statusMessage.value = "Failed to clear data: ${e.message}"
            }
        }
    }

    val appVersion: String = "1.0.0"
}
