package com.swiftquantum.quantumcareer.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CareerBadgeDto(
    @SerialName("id") val id: String,
    @SerialName("tier") val tier: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("earned") val earned: Boolean,
    @SerialName("earned_at") val earnedAt: String?,
    @SerialName("progress") val progress: BadgeProgressDto?
)

@Serializable
data class BadgeProgressDto(
    @SerialName("publications_required") val publicationsRequired: Int,
    @SerialName("publications_current") val publicationsCurrent: Int,
    @SerialName("citations_required") val citationsRequired: Int,
    @SerialName("citations_current") val citationsCurrent: Int,
    @SerialName("percentage") val percentage: Float
)

@Serializable
data class BadgeListResponseDto(
    @SerialName("badges") val badges: List<CareerBadgeDto>,
    @SerialName("next_badge") val nextBadge: CareerBadgeDto?,
    @SerialName("current_tier") val currentTier: String
)
