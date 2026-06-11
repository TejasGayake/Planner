package com.jobtracker.data.repository

import com.jobtracker.data.db.Job
import com.jobtracker.data.db.JobDao
import kotlinx.coroutines.flow.Flow

class JobRepository(private val jobDao: JobDao) {
    val allJobs: Flow<List<Job>> = jobDao.getAllJobs()

    fun getJobById(id: Long) = jobDao.getJobById(id)
    fun getJobsByStatus(status: String) = jobDao.getJobsByStatus(status)
    fun searchJobs(query: String) = jobDao.searchJobs(query)
    fun getJobCount() = jobDao.getJobCount()
    fun getJobCountByStatus(status: String) = jobDao.getJobCountByStatus(status)

    suspend fun insertJob(job: Job): Long = jobDao.insertJob(job)
    suspend fun updateJob(job: Job) = jobDao.updateJob(job)
    suspend fun deleteJob(job: Job) = jobDao.deleteJob(job)
}
