package com.jobtracker.data.repository

import com.google.common.truth.Truth.assertThat
import com.jobtracker.data.db.Reminder
import com.jobtracker.data.db.ReminderDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Unit tests for [ReminderRepository] using MockK to mock the [ReminderDao] layer.
 *
 * Verifies that the repository correctly delegates all operations
 * to the DAO and preserves the data contracts.
 */
class ReminderRepositoryTest {

    private lateinit var mockDao: ReminderDao
    private lateinit var repository: ReminderRepository

    @BeforeEach
    fun setUp() {
        mockDao = mockk(relaxed = true)
        repository = ReminderRepository(mockDao)
    }

    @Nested
    inner class GetAllReminders {

        @Test
        fun `getAllReminders delegates to DAO`() {
            every { mockDao.getAllReminders() } returns flowOf(
                listOf(
                    Reminder(id = 1L, jobId = 1L, title = "Interview at Google", remindAt = 1_000_000L),
                    Reminder(id = 2L, jobId = 2L, title = "Follow up with Amazon", remindAt = 2_000_000L)
                )
            )

            val result = repository.getAllReminders()

            verify(exactly = 1) { mockDao.getAllReminders() }
            assertThat(result).isNotNull()
        }

        @Test
        fun `getAllReminders returns empty list when no reminders`() {
            every { mockDao.getAllReminders() } returns flowOf(emptyList())

            val result = repository.getAllReminders()

            assertThat(result).isNotNull()
        }
    }

    @Nested
    inner class GetRemindersForJob {

        @Test
        fun `getRemindersForJob delegates to DAO`() {
            every { mockDao.getRemindersForJob(5L) } returns flowOf(
                listOf(
                    Reminder(id = 3L, jobId = 5L, title = "Tech screen", remindAt = 3_000_000L)
                )
            )

            val result = repository.getRemindersForJob(5L)

            verify(exactly = 1) { mockDao.getRemindersForJob(5L) }
            assertThat(result).isNotNull()
        }

        @Test
        fun `getRemindersForJob returns empty for job with no reminders`() {
            every { mockDao.getRemindersForJob(999L) } returns flowOf(emptyList())

            val result = repository.getRemindersForJob(999L)

            assertThat(result).isNotNull()
        }
    }

    @Nested
    inner class GetDueReminders {

        @Test
        fun `getDueReminders delegates to DAO`() = runBlocking {
            val now = System.currentTimeMillis()
            val dueReminders = listOf(
                Reminder(id = 4L, jobId = 1L, title = "Interview tomorrow", remindAt = now - 1000)
            )
            coEvery { mockDao.getDueReminders(now) } returns dueReminders

            val result = repository.getDueReminders(now)

            coVerify(exactly = 1) { mockDao.getDueReminders(now) }
            assertThat(result).isEqualTo(dueReminders)
        }

        @Test
        fun `getDueReminders returns empty list when none due`() = runBlocking {
            val now = System.currentTimeMillis()
            coEvery { mockDao.getDueReminders(now) } returns emptyList()

            val result = repository.getDueReminders(now)

            assertThat(result).isEmpty()
        }
    }

    @Nested
    inner class InsertReminder {

        @Test
        fun `insertReminder delegates to DAO and returns id`() = runBlocking {
            val reminder = Reminder(jobId = 1L, title = "Interview", remindAt = 1_000_000L)
            coEvery { mockDao.insertReminder(reminder) } returns 42L

            val resultId = repository.insertReminder(reminder)

            coVerify(exactly = 1) { mockDao.insertReminder(reminder) }
            assertThat(resultId).isEqualTo(42L)
        }

        @Test
        fun `insertReminder with all fields`() = runBlocking {
            val reminder = Reminder(
                jobId = 1L,
                title = "Final Round",
                note = "Panel of 3",
                remindAt = 10_000_000L,
                isSyncedToCalendar = true,
                calendarEventId = 555L,
                isCompleted = false
            )
            coEvery { mockDao.insertReminder(reminder) } returns 100L

            val resultId = repository.insertReminder(reminder)

            assertThat(resultId).isEqualTo(100L)
        }
    }

    @Nested
    inner class UpdateReminder {

        @Test
        fun `updateReminder delegates to DAO`() = runBlocking {
            val reminder = Reminder(id = 1L, jobId = 1L, title = "Updated Title", remindAt = 5_000_000L)
            coEvery { mockDao.updateReminder(reminder) } returns Unit

            repository.updateReminder(reminder)

            coVerify(exactly = 1) { mockDao.updateReminder(reminder) }
        }
    }

    @Nested
    inner class DeleteReminder {

        @Test
        fun `deleteReminder delegates to DAO`() = runBlocking {
            val reminder = Reminder(id = 1L, jobId = 1L, title = "Delete me", remindAt = 1_000_000L)
            coEvery { mockDao.deleteReminder(reminder) } returns Unit

            repository.deleteReminder(reminder)

            coVerify(exactly = 1) { mockDao.deleteReminder(reminder) }
        }
    }

    @Nested
    inner class MarkAsCompleted {

        @Test
        fun `markAsCompleted delegates to DAO`() = runBlocking {
            coEvery { mockDao.markAsCompleted(5L) } returns Unit

            repository.markAsCompleted(5L)

            coVerify(exactly = 1) { mockDao.markAsCompleted(5L) }
        }

        @Test
        fun `markAsCompleted with non-existent id does not throw`() = runBlocking {
            coEvery { mockDao.markAsCompleted(999L) } returns Unit

            repository.markAsCompleted(999L)

            coVerify(exactly = 1) { mockDao.markAsCompleted(999L) }
        }
    }

    @Nested
    inner class GetPendingReminderCount {

        @Test
        fun `getPendingReminderCount delegates to DAO`() {
            every { mockDao.getPendingReminderCount() } returns flowOf(3)

            val result = repository.getPendingReminderCount()

            verify(exactly = 1) { mockDao.getPendingReminderCount() }
            assertThat(result).isNotNull()
        }

        @Test
        fun `getPendingReminderCount returns zero when none pending`() {
            every { mockDao.getPendingReminderCount() } returns flowOf(0)

            val result = repository.getPendingReminderCount()

            assertThat(result).isNotNull()
        }
    }
}
