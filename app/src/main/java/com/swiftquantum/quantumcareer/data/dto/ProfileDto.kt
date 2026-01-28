package com.swiftquantum.quantumcareer.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PublicProfileDto(
    @SerialName("user_id") val userId: String,
    @SerialName("username") val username: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("avatar_url") val avatarUrl: String?,
    @SerialName("bio") val bio: String?,
    @SerialName("institution") val institution: String?,
    @SerialName("location") val location: String?,
    @SerialName("website") val website: String?,
    @SerialName("specializations") val specializations: List<String>,
    @SerialName("h_index") val hIndex: Int,
    @SerialName("i10_index") val i10Index: Int,
    @SerialName("total_publications") val totalPublications: Int,
    @SerialName("total_citations") val totalCitations: Int,
    @SerialName("badge_tier") val badgeTier: String,
    @SerialName("badges") val badges: List<CareerBadgeDto>,
    @SerialName("recent_circuits") val recentCircuits: List<PublishedCircuitDto>,
    @SerialName("is_public") val isPublic: Boolean,
    @SerialName("profile_url") val profileUrl: String,
    @SerialName("joined_at") val joinedAt: String
)

@Serializable
data class UpdateProfileRequestDto(
    @SerialName("display_name") val displayName: String?,
    @SerialName("bio") val bio: String?,
    @SerialName("institution") val institution: String?,
    @SerialName("location") val location: String?,
    @SerialName("website") val website: String?,
    @SerialName("specializations") val specializations: List<String>?,
    @SerialName("is_public") val isPublic: Boolean?,
    @SerialName("is_available_for_hire") val isAvailableForHire: Boolean?
)

@Serializable
data class UpdateProfileResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("profile") val profile: PublicProfileDto?
)

@Serializable
data class DashboardStatsDto(
    @SerialName("total_publications") val totalPublications: Int,
    @SerialName("total_citations") val totalCitations: Int,
    @SerialName("h_index") val hIndex: Int,
    @SerialName("i10_index") val i10Index: Int,
    @SerialName("pending_reviews") val pendingReviews: Int,
    @SerialName("current_badge_tier") val currentBadgeTier: String,
    @SerialName("next_badge_progress") val nextBadgeProgress: Float?,
    @SerialName("recent_activity") val recentActivity: List<ActivityItemDto>
)

@Serializable
data class ActivityItemDto(
    @SerialName("id") val id: String,
    @SerialName("type") val type: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("created_at") val createdAt: String
)
