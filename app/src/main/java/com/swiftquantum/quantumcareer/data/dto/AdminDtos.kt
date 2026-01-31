package com.swiftquantum.quantumcareer.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ============================================
// Admin Dashboard DTOs
// ============================================

@Serializable
data class AdminStatsDto(
    @SerialName("total_users") val totalUsers: Int = 0,
    @SerialName("active_users") val activeUsers: Int = 0,
    @SerialName("new_users_today") val newUsersToday: Int = 0,
    @SerialName("new_users_week") val newUsersWeek: Int = 0,
    @SerialName("total_quizzes") val totalQuizzes: Int = 0,
    @SerialName("quizzes_completed_today") val quizzesCompletedToday: Int = 0,
    @SerialName("total_jobs") val totalJobs: Int = 0,
    @SerialName("active_jobs") val activeJobs: Int = 0,
    @SerialName("total_certificates") val totalCertificates: Int = 0,
    @SerialName("certificates_issued_today") val certificatesIssuedToday: Int = 0,
    @SerialName("total_circuits") val totalCircuits: Int = 0,
    @SerialName("pending_reviews") val pendingReviews: Int = 0,
    @SerialName("total_revenue") val totalRevenue: Double = 0.0,
    @SerialName("revenue_this_month") val revenueThisMonth: Double = 0.0
)

@Serializable
data class AdminAnalyticsDto(
    @SerialName("user_growth") val userGrowth: List<GrowthDataPointDto> = emptyList(),
    @SerialName("quiz_completions") val quizCompletions: List<GrowthDataPointDto> = emptyList(),
    @SerialName("revenue_trend") val revenueTrend: List<GrowthDataPointDto> = emptyList(),
    @SerialName("top_categories") val topCategories: List<AdminCategoryStatsDto> = emptyList()
)

@Serializable
data class GrowthDataPointDto(
    @SerialName("date") val date: String,
    @SerialName("value") val value: Int
)

@Serializable
data class AdminCategoryStatsDto(
    @SerialName("category") val category: String,
    @SerialName("count") val count: Int,
    @SerialName("percentage") val percentage: Double
)

@Serializable
data class SystemHealthDto(
    @SerialName("api_status") val apiStatus: String = "healthy",
    @SerialName("database_status") val databaseStatus: String = "healthy",
    @SerialName("storage_status") val storageStatus: String = "healthy",
    @SerialName("cache_status") val cacheStatus: String = "healthy",
    @SerialName("last_backup") val lastBackup: String? = null,
    @SerialName("uptime_hours") val uptimeHours: Int = 0
)

// ============================================
// Admin Users DTOs
// ============================================

@Serializable
data class AdminUserListResponseDto(
    @SerialName("users") val users: List<AdminUserItemDto> = emptyList(),
    @SerialName("total") val total: Int = 0,
    @SerialName("page") val page: Int = 1,
    @SerialName("page_size") val pageSize: Int = 20,
    @SerialName("total_pages") val totalPages: Int = 1
)

@Serializable
data class AdminUserItemDto(
    @SerialName("id") val id: Int,
    @SerialName("username") val username: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("is_admin") val isAdmin: Boolean? = false,
    @SerialName("is_active") val isActive: Boolean? = true,
    @SerialName("subscription_type") val subscriptionType: String? = null,
    @SerialName("subscription_expires_at") val subscriptionExpiresAt: String? = null,
    @SerialName("total_xp") val totalXp: Int? = 0,
    @SerialName("current_level") val currentLevel: Int? = 1,
    @SerialName("current_streak") val currentStreak: Int? = 0,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("last_login_at") val lastLoginAt: String? = null
) {
    val isPro: Boolean
        get() = subscriptionType?.lowercase() in listOf("pro", "premium", "scholar", "career")

    val displayNameOrUsername: String
        get() = displayName ?: username ?: "User #$id"
}

@Serializable
data class AdminUserDetailDto(
    @SerialName("id") val id: Int,
    @SerialName("username") val username: String,
    @SerialName("email") val email: String,
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("bio") val bio: String? = null,
    @SerialName("is_admin") val isAdmin: Boolean = false,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("is_verified") val isVerified: Boolean = false,
    @SerialName("subscription_type") val subscriptionType: String? = null,
    @SerialName("subscription_expires_at") val subscriptionExpiresAt: String? = null,
    @SerialName("total_xp") val totalXp: Int = 0,
    @SerialName("current_level") val currentLevel: Int = 1,
    @SerialName("current_streak") val currentStreak: Int = 0,
    @SerialName("longest_streak") val longestStreak: Int = 0,
    @SerialName("certificates_count") val certificatesCount: Int = 0,
    @SerialName("badges_count") val badgesCount: Int = 0,
    @SerialName("circuits_count") val circuitsCount: Int = 0,
    @SerialName("quiz_attempts") val quizAttempts: Int = 0,
    @SerialName("highest_score") val highestScore: Int = 0,
    @SerialName("created_at") val createdAt: String,
    @SerialName("last_login_at") val lastLoginAt: String? = null,
    @SerialName("linkedin_url") val linkedinUrl: String? = null,
    @SerialName("github_url") val githubUrl: String? = null
)

@Serializable
data class UpdateUserRequest(
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("is_active") val isActive: Boolean? = null,
    @SerialName("is_admin") val isAdmin: Boolean? = null,
    @SerialName("subscription_type") val subscriptionType: String? = null
)

@Serializable
data class GrantPremiumRequest(
    @SerialName("user_id") val userId: Int,
    @SerialName("subscription_type") val subscriptionType: String,
    @SerialName("days") val days: Int = 30
)

@Serializable
data class BanUserRequest(
    @SerialName("user_id") val userId: Int,
    @SerialName("reason") val reason: String? = null
)

// ============================================
// Admin Content DTOs - Questions
// ============================================

@Serializable
data class AdminQuestionListResponseDto(
    @SerialName("questions") val questions: List<AdminQuestionItemDto> = emptyList(),
    @SerialName("total") val total: Int = 0,
    @SerialName("page") val page: Int = 1,
    @SerialName("page_size") val pageSize: Int = 20
)

@Serializable
data class AdminQuestionItemDto(
    @SerialName("id") val id: Int,
    @SerialName("text") val text: String,
    @SerialName("text_ko") val textKo: String? = null,
    @SerialName("text_ja") val textJa: String? = null,
    @SerialName("text_zh") val textZh: String? = null,
    @SerialName("text_de") val textDe: String? = null,
    @SerialName("category") val category: String,
    @SerialName("difficulty") val difficulty: String,
    @SerialName("points") val points: Int = 10,
    @SerialName("options_count") val optionsCount: Int = 4,
    @SerialName("times_answered") val timesAnswered: Int = 0,
    @SerialName("correct_rate") val correctRate: Double = 0.0,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class CreateQuestionRequest(
    @SerialName("text") val text: String,
    @SerialName("text_ko") val textKo: String? = null,
    @SerialName("text_ja") val textJa: String? = null,
    @SerialName("text_zh") val textZh: String? = null,
    @SerialName("text_de") val textDe: String? = null,
    @SerialName("category") val category: String,
    @SerialName("difficulty") val difficulty: String,
    @SerialName("options") val options: List<CreateOptionRequest>,
    @SerialName("correct_option_index") val correctOptionIndex: Int,
    @SerialName("explanation") val explanation: String? = null,
    @SerialName("explanation_ko") val explanationKo: String? = null,
    @SerialName("explanation_ja") val explanationJa: String? = null,
    @SerialName("explanation_zh") val explanationZh: String? = null,
    @SerialName("explanation_de") val explanationDe: String? = null,
    @SerialName("points") val points: Int = 10
)

@Serializable
data class CreateOptionRequest(
    @SerialName("text") val text: String,
    @SerialName("text_ko") val textKo: String? = null,
    @SerialName("text_ja") val textJa: String? = null,
    @SerialName("text_zh") val textZh: String? = null,
    @SerialName("text_de") val textDe: String? = null
)

// ============================================
// Admin Content DTOs - Jobs
// ============================================

@Serializable
data class AdminJobListResponseDto(
    @SerialName("jobs") val jobs: List<AdminJobItemDto> = emptyList(),
    @SerialName("total") val total: Int = 0,
    @SerialName("page") val page: Int = 1,
    @SerialName("page_size") val pageSize: Int = 20
)

@Serializable
data class AdminJobItemDto(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("company_name") val companyName: String,
    @SerialName("location") val location: String,
    @SerialName("job_type") val jobType: String? = null,
    @SerialName("is_remote") val isRemote: Boolean = false,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("application_count") val applicationCount: Int = 0,
    @SerialName("view_count") val viewCount: Int = 0,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("expires_at") val expiresAt: String? = null
)

@Serializable
data class CreateJobRequest(
    @SerialName("title") val title: String,
    @SerialName("company_name") val companyName: String,
    @SerialName("location") val location: String,
    @SerialName("description") val description: String,
    @SerialName("requirements") val requirements: List<String> = emptyList(),
    @SerialName("salary_min") val salaryMin: Int? = null,
    @SerialName("salary_max") val salaryMax: Int? = null,
    @SerialName("job_type") val jobType: String = "full_time",
    @SerialName("is_remote") val isRemote: Boolean = false
)

// ============================================
// Admin Content DTOs - Badges
// ============================================

@Serializable
data class AdminBadgeListResponseDto(
    @SerialName("badges") val badges: List<AdminBadgeItemDto> = emptyList(),
    @SerialName("total") val total: Int = 0
)

@Serializable
data class AdminBadgeItemDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("icon") val icon: String,
    @SerialName("tier") val tier: String,
    @SerialName("category") val category: String? = null,
    @SerialName("requirement_type") val requirementType: String? = null,
    @SerialName("requirement_value") val requirementValue: Int = 0,
    @SerialName("earned_count") val earnedCount: Int = 0,
    @SerialName("is_active") val isActive: Boolean = true
)

// ============================================
// Admin Login History DTOs
// ============================================

@Serializable
data class AdminLoginHistoryResponseDto(
    @SerialName("history") val history: List<AdminLoginHistoryItemDto> = emptyList(),
    @SerialName("total") val total: Int = 0
)

@Serializable
data class AdminLoginHistoryItemDto(
    @SerialName("id") val id: Int,
    @SerialName("user_id") val userId: Int,
    @SerialName("username") val username: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("ip_address") val ipAddress: String? = null,
    @SerialName("user_agent") val userAgent: String? = null,
    @SerialName("login_at") val loginAt: String,
    @SerialName("success") val success: Boolean = true
)

// ============================================
// Admin Activity DTOs
// ============================================

@Serializable
data class AdminActivityResponseDto(
    @SerialName("activities") val activities: List<AdminActivityItemDto> = emptyList()
)

@Serializable
data class AdminActivityItemDto(
    @SerialName("id") val id: Int,
    @SerialName("type") val type: String,
    @SerialName("description") val description: String,
    @SerialName("user_id") val userId: Int? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("timestamp") val timestamp: String,
    @SerialName("metadata") val metadata: Map<String, String>? = null
)

// ============================================
// Admin Settings DTOs
// ============================================

@Serializable
data class AdminSettingsDto(
    @SerialName("maintenance_mode") val maintenanceMode: Boolean = false,
    @SerialName("registration_enabled") val registrationEnabled: Boolean = true,
    @SerialName("email_verification_required") val emailVerificationRequired: Boolean = true,
    @SerialName("max_login_attempts") val maxLoginAttempts: Int = 5,
    @SerialName("session_timeout_hours") val sessionTimeoutHours: Int = 24,
    @SerialName("min_password_length") val minPasswordLength: Int = 8,
    @SerialName("require_strong_password") val requireStrongPassword: Boolean = true
)

@Serializable
data class UpdateAdminSettingsRequest(
    @SerialName("maintenance_mode") val maintenanceMode: Boolean? = null,
    @SerialName("registration_enabled") val registrationEnabled: Boolean? = null,
    @SerialName("email_verification_required") val emailVerificationRequired: Boolean? = null,
    @SerialName("max_login_attempts") val maxLoginAttempts: Int? = null,
    @SerialName("session_timeout_hours") val sessionTimeoutHours: Int? = null
)
