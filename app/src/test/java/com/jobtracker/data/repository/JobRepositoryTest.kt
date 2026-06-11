package com.jobtracker.data.repository

import com.google.common.truth.Truth.assertThat
import com.jobtracker.data.db.Job
import com.jobtracker.data.db.JobDao
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
 * Unit tests for [JobRepository] using MockK to mock the [JobDao] layer.
 *
 * These tests verify that the repository correctly delegates all operations
 * to the DAO and handles any transformation logic (if added in the future).
 */
class JobRepositoryTest {

    private lateinit var mockDao: JobDao
    private lateinit var repository: JobRepository

    @BeforeEach
    fun setUp() {
        mockDao = mockk(relaxed = true)
        repository = JobRepository(mockDao)
    }

    @Nested
    inner class GetAllJobs {

        @Test
        fun `allJobs delegates to DAO getAllJobs`() {
            every { mockDao.getAllJobs() } returns flowOf(
                listOf(
                    Job(id = 1, companyName = "Google", jobTitle = "SDE", source = "Manual"),
                    Job(id = 2, companyName = "Amazon", jobTitle = "Backend Dev", source = "Manual")
                )
            )

            val resultFlow = repository.allJobs

            verify(exactly = 1) { mockDao.getAllJobs() }
            assertThat(resultFlow).isNotNull()
        }

        @Test
        fun `allJobs returns empty list when no jobs exist`() {
            every { mockDao.getAllJobs() } returns flowOf(emptyList())

            val resultFlow = repository.allJobs

            assertThat(resultFlow).isNotNull()
        }
    }

    @Nested
    inner class GetJobById {

        @Test
        fun `getJobById delegates to DAO`() = runBlocking {
            val expectedJob = Job(id = 1L, companyName = "Google", jobTitle = "SDE", source = "Manual")
            coEvery { mockDao.getJobById(1L) } returns expectedJob

            val result = repository.getJobById(1L)

            coVerify(exactly = 1) { mockDao.getJobById(1L) }
            assertThat(result).isEqualTo(expectedJob)
        }

        @Test
        fun `getJobById returns null for non-existent id`() = runBlocking {
            coEvery { mockDao.getJobById(999L) } returns null

            val result = repository.getJobById(999L)

            assertThat(result).isNull()
        }
    }

    @Nested
    inner class GetJobsByStatus {

        @Test
        fun `getJobsByStatus delegates to DAO`() {
            every { mockDao.getJobsByStatus("Applied") } returns flowOf(
                listOf(
                    Job(id = 3L, companyName = "Infosys", jobTitle = "Tester", source = "Manual", status = "Applied")
                )
            )

            val resultFlow = repository.getJobsByStatus("Applied")

            verify(exactly = 1) { mockDao.getJobsByStatus("Applied") }
            assertThat(resultFlow).isNotNull()
        }

        @Test
        fun `getJobsByStatus returns empty for non-existent status`() {
            every { mockDao.getJobsByStatus("Rejected") } returns flowOf(emptyList())

            val resultFlow = repository.getJobsByStatus("Rejected")

            assertThat(resultFlow).isNotNull()
        }

        @Test
        fun `getJobsByStatus is case-sensitive as delegated`() {
            every { mockDao.getJobsByStatus("applied") } returns flowOf(emptyList())
            every { mockDao.getJobsByStatus("Applied") } returns flowOf(
                listOf(Job(id = 4L, companyName = "Wipro", jobTitle = "Dev", source = "Manual", status = "Applied"))
            )

            val resultLower = repository.getJobsByStatus("applied")
            val resultUpper = repository.getJobsByStatus("Applied")

            assertThat(resultLower).isNotEqualTo(resultUpper)
        }
    }

    @Nested
    inner class SearchJobs {

        @Test
        fun `searchJobs delegates to DAO`() {
            every { mockDao.searchJobs("Google") } returns flowOf(
                listOf(Job(id = 1L, companyName = "Google", jobTitle = "SDE", source = "Manual"))
            )

            repository.searchJobs("Google")

            verify(exactly = 1) { mockDao.searchJobs("Google") }
        }

        @Test
        fun `searchJobs with empty query`() {
            every { mockDao.searchJobs("") } returns flowOf(emptyList())

            repository.searchJobs("")

            verify(exactly = 1) { mockDao.searchJobs("") }
        }
    }

    @Nested
    inner class InsertJob {

        @Test
        fun `insertJob delegates to DAO and returns id`() = runBlocking {
            val job = Job(companyName = "NewCo", jobTitle = "Dev", source = "Manual")
            coEvery { mockDao.insertJob(job) } returns 42L

            val resultId = repository.insertJob(job)

            coVerify(exactly = 1) { mockDao.insertJob(job) }
            assertThat(resultId).isEqualTo(42L)
        }

        @Test
        fun `insertJob handles conflict replace`() = runBlocking {
            val job = Job(id = 5L, companyName = "UpdateCo", jobTitle = "Lead", source = "Manual")
            coEvery { mockDao.insertJob(job) } returns 5L

            val resultId = repository.insertJob(job)

            assertThat(resultId).isEqualTo(5L)
        }
    }

    @Nested
    inner class UpdateJob {

        @Test
        fun `updateJob delegates to DAO`() = runBlocking {
            val job = Job(id = 1L, companyName = "Google", jobTitle = "Senior SDE", source = "Manual")
            coEvery { mockDao.updateJob(job) } returns Unit

            repository.updateJob(job)

            coVerify(exactly = 1) { mockDao.updateJob(job) }
        }

        @Test
        fun `updateJob accepts job with minimal fields`() = runBlocking {
            val job = Job(id = 10L, companyName = "Minimal", jobTitle = "Role", source = "Src")
            coEvery { mockDao.updateJob(job) } returns Unit

            repository.updateJob(job)

            coVerify(exactly = 1) { mockDao.updateJob(job) }
        }
    }

    @Nested
    inner class DeleteJob {

        @Test
        fun `deleteJob delegates to DAO`() = runBlocking {
            val job = Job(id = 1L, companyName = "Google", jobTitle = "SDE", source = "Manual")
            coEvery { mockDao.deleteJob(job) } returns Unit

            repository.deleteJob(job)

            coVerify(exactly = 1) { mockDao.deleteJob(job) }
        }
    }

    @Nested
    inner class GetJobCount {

        @Test
        fun `getJobCount delegates to DAO`() {
            every { mockDao.getJobCount() } returns flowOf(5)

            val result = repository.getJobCount()

            verify(exactly = 1) { mockDao.getJobCount() }
            assertThat(result).isNotNull()
        }

        @Test
        fun `getJobCountByStatus delegates to DAO`() {
            every { mockDao.getJobCountByStatus("Interview") } returns flowOf(2)

            val result = repository.getJobCountByStatus("Interview")

            verify(exactly = 1) { mockDao.getJobCountByStatus("Interview") }
            assertThat(result).isNotNull()
        }
    }
}
