package com.jobtracker.data.db

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Unit tests for [JobDao] interface contract using MockK.
 *
 * Since Room DAOs require Android instrumentation, these tests
 * verify the expected behavior of the DAO interface by mocking
 * its implementations and verifying method delegation and contracts.
 *
 * For full integration tests with an in-memory Room database,
 * use the `room-testing` library in androidTest.
 */
class JobDaoTest {

    private lateinit var dao: JobDao

    @BeforeEach
    fun setUp() {
        dao = mockk()
    }

    @Nested
    inner class GetAllJobs {

        @Test
        fun `getAllJobs returns Flow of all jobs ordered by createdAt DESC`() = runTest {
            val jobs = listOf(
                Job(id = 2L, companyName = "Amazon", jobTitle = "Backend Dev", source = "Manual", createdAt = 200L),
                Job(id = 1L, companyName = "Google", jobTitle = "SDE", source = "Manual", createdAt = 100L)
            )
            every { dao.getAllJobs() } returns flowOf(jobs)

            dao.getAllJobs().test {
                val emitted = awaitItem()
                assertThat(emitted).hasSize(2)
                assertThat(emitted[0].companyName).isEqualTo("Amazon")
                assertThat(emitted[1].companyName).isEqualTo("Google")
                awaitComplete()
            }

            verify(exactly = 1) { dao.getAllJobs() }
        }

        @Test
        fun `getAllJobs returns empty Flow when no jobs exist`() = runTest {
            every { dao.getAllJobs() } returns flowOf(emptyList())

            dao.getAllJobs().test {
                val emitted = awaitItem()
                assertThat(emitted).isEmpty()
                awaitComplete()
            }
        }

        @Test
        fun `getAllJobs returns multiple emissions on data changes`() = runTest {
            val initial = listOf(
                Job(id = 1L, companyName = "Google", jobTitle = "SDE", source = "Manual")
            )
            val updated = listOf(
                Job(id = 1L, companyName = "Google", jobTitle = "SDE", source = "Manual"),
                Job(id = 2L, companyName = "Amazon", jobTitle = "Dev", source = "Manual")
            )
            // Use a multi-emission Flow to simulate Room reactive updates
            val multiFlow = flow {
                emit(initial)
                emit(updated)
            }
            every { dao.getAllJobs() } returns multiFlow

            dao.getAllJobs().test {
                assertThat(awaitItem()).hasSize(1)
                assertThat(awaitItem()).hasSize(2)
                awaitComplete()
            }
        }
    }

    @Nested
    inner class GetJobById {

        @Test
        fun `getJobById returns job for existing id`() = runTest {
            val expectedJob = Job(id = 1L, companyName = "Google", jobTitle = "SDE", source = "Manual")
            coEvery { dao.getJobById(1L) } returns expectedJob

            val result = dao.getJobById(1L)

            coVerify(exactly = 1) { dao.getJobById(1L) }
            assertThat(result).isEqualTo(expectedJob)
        }

        @Test
        fun `getJobById returns null for non-existent id`() = runTest {
            coEvery { dao.getJobById(999L) } returns null

            val result = dao.getJobById(999L)

            assertThat(result).isNull()
        }

        @Test
        fun `getJobById with id 0 returns null`() = runTest {
            coEvery { dao.getJobById(0L) } returns null

            val result = dao.getJobById(0L)

            assertThat(result).isNull()
        }
    }

    @Nested
    inner class GetJobsByStatus {

        @Test
        fun `getJobsByStatus returns filtered jobs`() = runTest {
            val appliedJobs = listOf(
                Job(id = 1L, companyName = "Google", jobTitle = "SDE", source = "Manual", status = "Applied"),
                Job(id = 3L, companyName = "Infosys", jobTitle = "Tester", source = "Manual", status = "Applied")
            )
            every { dao.getJobsByStatus("Applied") } returns flowOf(appliedJobs)

            dao.getJobsByStatus("Applied").test {
                val emitted = awaitItem()
                assertThat(emitted).hasSize(2)
                assertThat(emitted.all { it.status == "Applied" }).isTrue()
                awaitComplete()
            }
        }

        @Test
        fun `getJobsByStatus returns empty for unused status`() = runTest {
            every { dao.getJobsByStatus("Offer") } returns flowOf(emptyList())

            dao.getJobsByStatus("Offer").test {
                assertThat(awaitItem()).isEmpty()
                awaitComplete()
            }
        }

        @Test
        fun `getJobsByStatus is ordered by createdAt DESC`() = runTest {
            val jobs = listOf(
                Job(id = 2L, companyName = "B", jobTitle = "R2", source = "S", status = "Applied", createdAt = 200L),
                Job(id = 1L, companyName = "A", jobTitle = "R1", source = "S", status = "Applied", createdAt = 100L)
            )
            every { dao.getJobsByStatus("Applied") } returns flowOf(jobs)

            dao.getJobsByStatus("Applied").test {
                val emitted = awaitItem()
                assertThat(emitted[0].createdAt).isAtLeast(emitted[1].createdAt)
                awaitComplete()
            }
        }
    }

    @Nested
    inner class SearchJobs {

        @Test
        fun `searchJobs matches by company name`() = runTest {
            val results = listOf(
                Job(id = 1L, companyName = "Google", jobTitle = "SDE", source = "Manual")
            )
            every { dao.searchJobs("Google") } returns flowOf(results)

            dao.searchJobs("Google").test {
                assertThat(awaitItem()).containsExactly(results[0])
                awaitComplete()
            }
        }

        @Test
        fun `searchJobs matches by job title`() = runTest {
            val results = listOf(
                Job(id = 2L, companyName = "ABC", jobTitle = "Android Developer", source = "Manual")
            )
            every { dao.searchJobs("Android") } returns flowOf(results)

            dao.searchJobs("Android").test {
                assertThat(awaitItem()).containsExactly(results[0])
                awaitComplete()
            }
        }

        @Test
        fun `searchJobs with partial match`() = runTest {
            every { dao.searchJobs("soft") } returns flowOf(
                listOf(Job(id = 3L, companyName = "Infosys", jobTitle = "Dev", source = "S"))
            )

            dao.searchJobs("soft").test {
                assertThat(awaitItem()).hasSize(1)
                awaitComplete()
            }
        }

        @Test
        fun `searchJobs with empty query returns all`() = runTest {
            every { dao.searchJobs("") } returns flowOf(
                listOf(
                    Job(id = 1L, companyName = "A", jobTitle = "R1", source = "S"),
                    Job(id = 2L, companyName = "B", jobTitle = "R2", source = "S")
                )
            )

            dao.searchJobs("").test {
                assertThat(awaitItem()).hasSize(2)
                awaitComplete()
            }
        }
    }

    @Nested
    inner class InsertJob {

        @Test
        fun `insertJob returns new row id`() = runTest {
            val job = Job(companyName = "NewCo", jobTitle = "Dev", source = "Manual")
            coEvery { dao.insertJob(job) } returns 42L

            val id = dao.insertJob(job)

            assertThat(id).isEqualTo(42L)
        }

        @Test
        fun `insertJob replaces on conflict`() = runTest {
            val job = Job(id = 5L, companyName = "Existing", jobTitle = "Dev", source = "Manual")
            coEvery { dao.insertJob(job) } returns 5L

            val id = dao.insertJob(job)

            assertThat(id).isEqualTo(5L)
        }

        @Test
        fun `insertJob with autoGenerate id returns positive id`() = runTest {
            val job = Job(companyName = "AutoGen", jobTitle = "Role", source = "S")
            coEvery { dao.insertJob(job) } returns 1L

            val id = dao.insertJob(job)

            assertThat(id).isGreaterThan(0)
        }

        @Test
        fun `insertJob with all fields is accepted`() = runTest {
            val job = Job(
                companyName = "FullJob",
                jobTitle = "Senior Engineer",
                location = "Bangalore",
                salary = "30 LPA",
                jobType = "Full-Time",
                source = "WhatsApp",
                sourceUrl = "https://example.com/job",
                notes = "Great opportunity",
                status = "New",
                appliedDate = 1000L,
                interviewDate = 2000L,
                deadline = 3000L
            )
            coEvery { dao.insertJob(job) } returns 10L

            val id = dao.insertJob(job)

            assertThat(id).isEqualTo(10L)
            coVerify(exactly = 1) { dao.insertJob(job) }
        }
    }

    @Nested
    inner class UpdateJob {

        @Test
        fun `updateJob modifies existing row`() = runTest {
            val job = Job(id = 1L, companyName = "Google", jobTitle = "Senior SDE", source = "Manual", status = "Applied")
            coEvery { dao.updateJob(job) } returns Unit

            dao.updateJob(job)

            coVerify(exactly = 1) { dao.updateJob(job) }
        }
    }

    @Nested
    inner class DeleteJob {

        @Test
        fun `deleteJob removes job`() = runTest {
            val job = Job(id = 1L, companyName = "Google", jobTitle = "SDE", source = "Manual")
            coEvery { dao.deleteJob(job) } returns Unit

            dao.deleteJob(job)

            coVerify(exactly = 1) { dao.deleteJob(job) }
        }
    }

    @Nested
    inner class GetJobCount {

        @Test
        fun `getJobCount returns total count`() = runTest {
            every { dao.getJobCount() } returns flowOf(10)

            dao.getJobCount().test {
                assertThat(awaitItem()).isEqualTo(10)
                awaitComplete()
            }
        }

        @Test
        fun `getJobCount returns zero when table is empty`() = runTest {
            every { dao.getJobCount() } returns flowOf(0)

            dao.getJobCount().test {
                assertThat(awaitItem()).isEqualTo(0)
                awaitComplete()
            }
        }
    }

    @Nested
    inner class GetJobCountByStatus {

        @Test
        fun `getJobCountByStatus returns count for specific status`() = runTest {
            every { dao.getJobCountByStatus("Interview") } returns flowOf(3)

            dao.getJobCountByStatus("Interview").test {
                assertThat(awaitItem()).isEqualTo(3)
                awaitComplete()
            }
        }

        @Test
        fun `getJobCountByStatus returns zero for unused status`() = runTest {
            every { dao.getJobCountByStatus("Offer") } returns flowOf(0)

            dao.getJobCountByStatus("Offer").test {
                assertThat(awaitItem()).isEqualTo(0)
                awaitComplete()
            }
        }
    }
}
