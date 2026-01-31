package com.swiftquantum.quantumcareer.data.api

import com.swiftquantum.quantumcareer.data.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Admin API - Admin Dashboard and Management endpoints
 * Base URL: https://api.swiftquantum.tech/api/v1/
 */
interface AdminApi {

    // ============================================
    // Dashboard & Stats
    // ============================================

    /**
     * Get admin dashboard statistics
     */
    @GET("admin/stats")
    suspend fun getStats(): Response<AdminStatsDto>

    /**
     * Get admin analytics data
     */
    @GET("admin/analytics")
    suspend fun getAnalytics(
        @Query("period") period: String = "week" // day, week, month, year
    ): Response<AdminAnalyticsDto>

    /**
     * Get system health status
     */
    @GET("admin/health")
    suspend fun getSystemHealth(): Response<SystemHealthDto>

    /**
     * Get recent activity
     */
    @GET("admin/activity")
    suspend fun getRecentActivity(
        @Query("limit") limit: Int = 20
    ): Response<AdminActivityResponseDto>

    // ============================================
    // User Management
    // ============================================

    /**
     * Get all users (paginated)
     */
    @GET("admin/users")
    suspend fun getUsers(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("search") search: String? = null,
        @Query("filter") filter: String? = null, // all, active, pro, admin
        @Query("sort") sort: String? = null // created_at, last_login, xp
    ): Response<AdminUserListResponseDto>

    /**
     * Get user details
     */
    @GET("admin/users/{userId}")
    suspend fun getUserDetail(
        @Path("userId") userId: Int
    ): Response<AdminUserDetailDto>

    /**
     * Update user
     */
    @PUT("admin/users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: Int,
        @Body request: UpdateUserRequest
    ): Response<AdminUserDetailDto>

    /**
     * Delete user
     */
    @DELETE("admin/users/{userId}")
    suspend fun deleteUser(
        @Path("userId") userId: Int
    ): Response<Unit>

    /**
     * Grant premium subscription to user
     */
    @POST("admin/users/{userId}/grant-premium")
    suspend fun grantPremium(
        @Path("userId") userId: Int,
        @Body request: GrantPremiumRequest
    ): Response<AdminUserDetailDto>

    /**
     * Ban user
     */
    @POST("admin/users/{userId}/ban")
    suspend fun banUser(
        @Path("userId") userId: Int,
        @Body request: BanUserRequest
    ): Response<Unit>

    /**
     * Unban user
     */
    @POST("admin/users/{userId}/unban")
    suspend fun unbanUser(
        @Path("userId") userId: Int
    ): Response<Unit>

    /**
     * Get login history
     */
    @GET("admin/login-history")
    suspend fun getLoginHistory(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 50,
        @Query("user_id") userId: Int? = null
    ): Response<AdminLoginHistoryResponseDto>

    // ============================================
    // Content Management - Questions
    // ============================================

    /**
     * Get all questions (paginated)
     */
    @GET("admin/questions")
    suspend fun getQuestions(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("category") category: String? = null,
        @Query("difficulty") difficulty: String? = null
    ): Response<AdminQuestionListResponseDto>

    /**
     * Create new question
     */
    @POST("admin/questions")
    suspend fun createQuestion(
        @Body request: CreateQuestionRequest
    ): Response<AdminQuestionItemDto>

    /**
     * Update question
     */
    @PUT("admin/questions/{questionId}")
    suspend fun updateQuestion(
        @Path("questionId") questionId: Int,
        @Body request: CreateQuestionRequest
    ): Response<AdminQuestionItemDto>

    /**
     * Delete question
     */
    @DELETE("admin/questions/{questionId}")
    suspend fun deleteQuestion(
        @Path("questionId") questionId: Int
    ): Response<Unit>

    // ============================================
    // Content Management - Jobs
    // ============================================

    /**
     * Get all job listings (admin view)
     */
    @GET("admin/jobs")
    suspend fun getJobs(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("status") status: String? = null // active, inactive, expired
    ): Response<AdminJobListResponseDto>

    /**
     * Create new job listing
     */
    @POST("admin/jobs")
    suspend fun createJob(
        @Body request: CreateJobRequest
    ): Response<AdminJobItemDto>

    /**
     * Update job listing
     */
    @PUT("admin/jobs/{jobId}")
    suspend fun updateJob(
        @Path("jobId") jobId: Int,
        @Body request: CreateJobRequest
    ): Response<AdminJobItemDto>

    /**
     * Delete job listing
     */
    @DELETE("admin/jobs/{jobId}")
    suspend fun deleteJob(
        @Path("jobId") jobId: Int
    ): Response<Unit>

    /**
     * Toggle job active status
     */
    @POST("admin/jobs/{jobId}/toggle-active")
    suspend fun toggleJobActive(
        @Path("jobId") jobId: Int
    ): Response<AdminJobItemDto>

    // ============================================
    // Content Management - Badges
    // ============================================

    /**
     * Get all badge definitions
     */
    @GET("admin/badges")
    suspend fun getBadges(): Response<AdminBadgeListResponseDto>

    /**
     * Update badge definition
     */
    @PUT("admin/badges/{badgeId}")
    suspend fun updateBadge(
        @Path("badgeId") badgeId: Int,
        @Body request: AdminBadgeItemDto
    ): Response<AdminBadgeItemDto>

    // ============================================
    // System Settings
    // ============================================

    /**
     * Get admin settings
     */
    @GET("admin/settings")
    suspend fun getSettings(): Response<AdminSettingsDto>

    /**
     * Update admin settings
     */
    @PUT("admin/settings")
    suspend fun updateSettings(
        @Body request: UpdateAdminSettingsRequest
    ): Response<AdminSettingsDto>

    /**
     * Validate admin PIN
     */
    @POST("admin/validate-pin")
    suspend fun validateAdminPin(
        @Body request: ValidateAdminPinRequest
    ): Response<ValidateAdminPinResponse>

    /**
     * Trigger database backup
     */
    @POST("admin/backup")
    suspend fun triggerBackup(): Response<Unit>

    /**
     * Clear cache
     */
    @POST("admin/clear-cache")
    suspend fun clearCache(): Response<Unit>
}

@kotlinx.serialization.Serializable
data class ValidateAdminPinRequest(
    @kotlinx.serialization.SerialName("pin") val pin: String
)

@kotlinx.serialization.Serializable
data class ValidateAdminPinResponse(
    @kotlinx.serialization.SerialName("valid") val valid: Boolean,
    @kotlinx.serialization.SerialName("message") val message: String? = null
)
