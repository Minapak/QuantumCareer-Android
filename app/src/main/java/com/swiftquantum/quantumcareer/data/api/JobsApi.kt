package com.swiftquantum.quantumcareer.data.api

import com.swiftquantum.quantumcareer.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface JobsApi {

    /**
     * Get list of jobs with optional filters.
     */
    @GET("jobs")
    suspend fun getJobs(
        @Query("query") query: String? = null,
        @Query("location_types") locationTypes: String? = null,
        @Query("employment_types") employmentTypes: String? = null,
        @Query("experience_levels") experienceLevels: String? = null,
        @Query("skills") skills: String? = null,
        @Query("min_salary") minSalary: Int? = null,
        @Query("max_salary") maxSalary: Int? = null,
        @Query("company_sizes") companySizes: String? = null,
        @Query("posted_within_days") postedWithinDays: Int? = null,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): Response<JobListResponseDto>

    /**
     * Get job details by ID.
     */
    @GET("jobs/{id}")
    suspend fun getJobById(
        @Path("id") jobId: String
    ): Response<JobDto>

    /**
     * Get AI-recommended jobs for the current user.
     */
    @GET("jobs/recommended")
    suspend fun getRecommendedJobs(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): Response<RecommendedJobsResponseDto>

    /**
     * Get saved jobs for the current user.
     */
    @GET("jobs/saved")
    suspend fun getSavedJobs(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): Response<SavedJobsResponseDto>

    /**
     * Save a job to bookmarks.
     */
    @POST("jobs/{id}/save")
    suspend fun saveJob(
        @Path("id") jobId: String,
        @Body request: SaveJobRequestDto? = null
    ): Response<SaveJobResponseDto>

    /**
     * Remove a job from bookmarks.
     */
    @DELETE("jobs/{id}/save")
    suspend fun unsaveJob(
        @Path("id") jobId: String
    ): Response<UnsaveJobResponseDto>

    /**
     * Apply to a job.
     */
    @POST("jobs/{id}/apply")
    suspend fun applyToJob(
        @Path("id") jobId: String,
        @Body request: JobApplicationRequestDto
    ): Response<JobApplicationResponseDto>

    /**
     * Get all job applications for the current user.
     */
    @GET("applications")
    suspend fun getApplications(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): Response<JobApplicationsResponseDto>

    /**
     * Get a specific application by ID.
     */
    @GET("applications/{id}")
    suspend fun getApplicationById(
        @Path("id") applicationId: String
    ): Response<JobApplicationDto>

    /**
     * Withdraw a job application.
     */
    @POST("applications/{id}/withdraw")
    suspend fun withdrawApplication(
        @Path("id") applicationId: String
    ): Response<JobApplicationResponseDto>

    /**
     * Get job statistics for the current user.
     */
    @GET("jobs/stats")
    suspend fun getJobStats(): Response<JobStatsDto>

    /**
     * Get match analysis for a specific job.
     */
    @GET("jobs/{id}/match")
    suspend fun getJobMatchAnalysis(
        @Path("id") jobId: String
    ): Response<RecommendedJobDto>

    /**
     * Search jobs with POST body for complex filters.
     */
    @POST("jobs/search")
    suspend fun searchJobs(
        @Body request: JobFilterRequestDto
    ): Response<JobListResponseDto>
}
