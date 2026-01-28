package com.swiftquantum.quantumcareer.domain.repository

import com.swiftquantum.quantumcareer.domain.model.*

interface JobsRepository {

    /**
     * Get list of jobs with optional filters.
     */
    suspend fun getJobs(
        filter: JobFilter = JobFilter(),
        page: Int = 1,
        pageSize: Int = 20
    ): Result<Pair<List<Job>, Boolean>>

    /**
     * Get job details by ID.
     */
    suspend fun getJobById(jobId: String): Result<Job>

    /**
     * Get AI-recommended jobs for the current user.
     */
    suspend fun getRecommendedJobs(
        page: Int = 1,
        pageSize: Int = 20
    ): Result<List<RecommendedJob>>

    /**
     * Get saved jobs for the current user.
     */
    suspend fun getSavedJobs(
        page: Int = 1,
        pageSize: Int = 20
    ): Result<List<SavedJob>>

    /**
     * Save a job to bookmarks.
     */
    suspend fun saveJob(jobId: String, notes: String? = null): Result<Unit>

    /**
     * Remove a job from bookmarks.
     */
    suspend fun unsaveJob(jobId: String): Result<Unit>

    /**
     * Apply to a job.
     */
    suspend fun applyToJob(request: JobApplicationRequest): Result<JobApplication>

    /**
     * Get all job applications for the current user.
     */
    suspend fun getApplications(
        status: ApplicationStatus? = null,
        page: Int = 1,
        pageSize: Int = 20
    ): Result<List<JobApplication>>

    /**
     * Get a specific application by ID.
     */
    suspend fun getApplicationById(applicationId: String): Result<JobApplication>

    /**
     * Withdraw a job application.
     */
    suspend fun withdrawApplication(applicationId: String): Result<Unit>

    /**
     * Get job statistics for the current user.
     */
    suspend fun getJobStats(): Result<JobStats>

    /**
     * Get match analysis for a specific job.
     */
    suspend fun getJobMatchAnalysis(jobId: String): Result<RecommendedJob>
}
