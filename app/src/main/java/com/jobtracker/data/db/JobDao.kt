package com.jobtracker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {
    @Query("SELECT * FROM jobs ORDER BY createdAt DESC")
    fun getAllJobs(): Flow<List<Job>>

    @Query("SELECT * FROM jobs WHERE id = :id")
    suspend fun getJobById(id: Long): Job?

    @Query("SELECT * FROM jobs WHERE status = :status ORDER BY createdAt DESC")
    fun getJobsByStatus(status: String): Flow<List<Job>>

    @Query("SELECT * FROM jobs WHERE companyName LIKE '%' || :query || '%' OR jobTitle LIKE '%' || :query || '%'")
    fun searchJobs(query: String): Flow<List<Job>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: Job): Long

    @Update
    suspend fun updateJob(job: Job)

    @Delete
    suspend fun deleteJob(job: Job)

    @Query("SELECT COUNT(*) FROM jobs")
    fun getJobCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM jobs WHERE status = :status")
    fun getJobCountByStatus(status: String): Flow<Int>
}
