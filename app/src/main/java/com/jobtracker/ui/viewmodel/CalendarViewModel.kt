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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

data class CalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val hasJobs: Boolean = false,
    val hasDeadlines: Boolean = false,
    val jobs: List<Job> = emptyList()
)

data class CalendarState(
    val currentMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate? = null,
    val days: List<List<CalendarDay>> = emptyList(),
    val jobsForSelectedDate: List<Job> = emptyList(),
    val remindersForSelectedDate: List<Reminder> = emptyList()
)

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val jobRepo = JobRepository(db.jobDao())
    private val reminderRepo = ReminderRepository(db.reminderDao())

    private val _calendarState = MutableStateFlow(CalendarState())
    val calendarState: StateFlow<CalendarState> = _calendarState.asStateFlow()

    private val _allReminders = reminderRepo.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            jobRepo.allJobs.collect { jobs ->
                rebuildCalendar(jobs)
            }
        }
        selectToday()
    }

    fun navigateMonth(offset: Int) {
        val newMonth = _calendarState.value.currentMonth.plusMonths(offset.toLong())
        _calendarState.value = _calendarState.value.copy(currentMonth = newMonth)
        viewModelScope.launch {
            jobRepo.allJobs.collect { jobs ->
                rebuildCalendar(jobs, newMonth)
            }
        }
    }

    fun selectDate(date: LocalDate) {
        val jobs = getJobsForDate(date)
        val reminders = _allReminders.value.filter { reminder ->
            val reminderDate = java.time.Instant.ofEpochMilli(reminder.remindAt)
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
            reminderDate == date
        }
        _calendarState.value = _calendarState.value.copy(
            selectedDate = date,
            jobsForSelectedDate = jobs,
            remindersForSelectedDate = reminders
        )
    }

    fun selectToday() {
        selectDate(LocalDate.now())
    }

    private fun rebuildCalendar(jobs: List<Job>, month: YearMonth = _calendarState.value.currentMonth) {
        val today = LocalDate.now()
        val firstOfMonth = month.atDay(1)
        val lastOfMonth = month.atEndOfMonth()
        val startDayOfWeek = firstOfMonth.dayOfWeek.value % 7 // Monday=0...Sunday=6

        val days = mutableListOf<List<CalendarDay>>()
        val weekDays = mutableListOf<CalendarDay>()

        // Add padding days from previous month
        val prevMonth = month.minusMonths(1)
        val prevMonthDays = prevMonth.lengthOfMonth()
        for (i in startDayOfWeek - 1 downTo 0) {
            val date = prevMonth.atDay(prevMonthDays - i)
            weekDays.add(
                CalendarDay(
                    date = date,
                    isCurrentMonth = false,
                    isToday = date == today,
                    hasJobs = jobs.any { isSameDay(it, date) }
                )
            )
        }

        // Current month days
        for (day in 1..month.lengthOfMonth()) {
            val date = month.atDay(day)
            val dayJobs = jobs.filter { isSameDay(it, date) }
            weekDays.add(
                CalendarDay(
                    date = date,
                    isCurrentMonth = true,
                    isToday = date == today,
                    hasJobs = dayJobs.isNotEmpty(),
                    hasDeadlines = dayJobs.any { it.deadline != null && isSameDayTimestamp(it.deadline, date) },
                    jobs = dayJobs
                )
            )
            if (weekDays.size == 7) {
                days.add(weekDays.toList())
                weekDays.clear()
            }
        }

        // Fill remaining days with next month
        if (weekDays.isNotEmpty()) {
            val nextMonth = month.plusMonths(1)
            var nextDay = 1
            while (weekDays.size < 7) {
                val date = nextMonth.atDay(nextDay)
                weekDays.add(
                    CalendarDay(
                        date = date,
                        isCurrentMonth = false,
                        isToday = date == today,
                        hasJobs = jobs.any { isSameDay(it, date) }
                    )
                )
                nextDay++
            }
            days.add(weekDays.toList())
        }

        _calendarState.value = _calendarState.value.copy(
            currentMonth = month,
            days = days
        )
    }

    private fun getJobsForDate(date: LocalDate): List<Job> {
        // This will be called after rebuildCalendar, but we need to store jobs
        return emptyList() // Placeholder - jobs are embedded in CalendarDay
    }

    private fun isSameDay(job: Job, date: LocalDate): Boolean {
        val jobDate = java.time.Instant.ofEpochMilli(job.createdAt)
            .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        return jobDate == date
    }

    private fun isSameDayTimestamp(timestamp: Long, date: LocalDate): Boolean {
        val tsDate = java.time.Instant.ofEpochMilli(timestamp)
            .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        return tsDate == date
    }

    fun getMonthHeader(): String {
        val month = _calendarState.value.currentMonth
        return "${month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.year}"
    }

    fun getDayHeader(): List<String> {
        val dow = DayOfWeek.entries
        return dow.map { it.getDisplayName(TextStyle.SHORT, Locale.getDefault()) }
    }
}
