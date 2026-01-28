package com.swiftquantum.quantumcareer.domain.model

import java.time.LocalDateTime

data class PublicProfile(
    val userId: String,
    val username: String,
    val displayName: String,
    val avatarUrl: String?,
    val bio: String?,
    val institution: String?,
    val location: String?,
    val website: String?,
    val specializations: List<String>,
    val hIndex: Int,
    val i10Index: Int,
    val totalPublications: Int,
    val totalCitations: Int,
    val badgeTier: BadgeTier,
    val badges: List<CareerBadge>,
    val recentCircuits: List<PublishedCircuit>,
    val isPublic: Boolean,
    val profileUrl: String,
    val joinedAt: LocalDateTime
) {
    val publicProfileUrl: String
        get() = "https://quantum.career/profile/$username"
}

data class UpdateProfileRequest(
    val displayName: String? = null,
    val bio: String? = null,
    val institution: String? = null,
    val location: String? = null,
    val website: String? = null,
    val specializations: List<String>? = null,
    val isPublic: Boolean? = null,
    val isAvailableForHire: Boolean? = null
)

data class DashboardStats(
    val totalPublications: Int,
    val totalCitations: Int,
    val hIndex: Int,
    val i10Index: Int,
    val pendingReviews: Int,
    val currentBadgeTier: BadgeTier,
    val nextBadgeProgress: Float?,
    val recentActivity: List<ActivityItem>
)

data class ActivityItem(
    val id: String,
    val type: ActivityType,
    val title: String,
    val description: String,
    val createdAt: LocalDateTime
)

enum class ActivityType {
    CIRCUIT_PUBLISHED,
    CIRCUIT_CITED,
    REVIEW_COMPLETED,
    BADGE_EARNED,
    OFFER_RECEIVED,
    PROFILE_VIEWED;

    companion object {
        fun fromString(value: String): ActivityType {
            return when (value.lowercase()) {
                "circuit_published" -> CIRCUIT_PUBLISHED
                "circuit_cited" -> CIRCUIT_CITED
                "review_completed" -> REVIEW_COMPLETED
                "badge_earned" -> BADGE_EARNED
                "offer_received" -> OFFER_RECEIVED
                "profile_viewed" -> PROFILE_VIEWED
                else -> CIRCUIT_PUBLISHED
            }
        }
    }
}
