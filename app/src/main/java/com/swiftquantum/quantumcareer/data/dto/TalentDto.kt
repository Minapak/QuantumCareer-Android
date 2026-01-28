package com.swiftquantum.quantumcareer.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TalentSearchRequestDto(
    @SerialName("query") val query: String?,
    @SerialName("min_h_index") val minHIndex: Int?,
    @SerialName("min_publications") val minPublications: Int?,
    @SerialName("badge_tier") val badgeTier: String?,
    @SerialName("specializations") val specializations: List<String>?,
    @SerialName("page") val page: Int = 1,
    @SerialName("per_page") val perPage: Int = 20
)

@Serializable
data class TalentProfileDto(
    @SerialName("user_id") val userId: String,
    @SerialName("username") val username: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("avatar_url") val avatarUrl: String?,
    @SerialName("bio") val bio: String?,
    @SerialName("institution") val institution: String?,
    @SerialName("specializations") val specializations: List<String>,
    @SerialName("h_index") val hIndex: Int,
    @SerialName("i10_index") val i10Index: Int,
    @SerialName("total_publications") val totalPublications: Int,
    @SerialName("total_citations") val totalCitations: Int,
    @SerialName("badge_tier") val badgeTier: String,
    @SerialName("is_available") val isAvailable: Boolean,
    @SerialName("profile_url") val profileUrl: String
)

@Serializable
data class TalentSearchResponseDto(
    @SerialName("profiles") val profiles: List<TalentProfileDto>,
    @SerialName("total") val total: Int,
    @SerialName("page") val page: Int,
    @SerialName("per_page") val perPage: Int
)

@Serializable
data class ScoutRequestDto(
    @SerialName("target_user_id") val targetUserId: String,
    @SerialName("message") val message: String,
    @SerialName("offer_type") val offerType: String,
    @SerialName("organization") val organization: String,
    @SerialName("position") val position: String?,
    @SerialName("details") val details: String?
)

@Serializable
data class ScoutResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("offer_id") val offerId: String?
)

@Serializable
data class TalentOfferDto(
    @SerialName("id") val id: String,
    @SerialName("from_user_id") val fromUserId: String,
    @SerialName("from_user_name") val fromUserName: String,
    @SerialName("from_organization") val fromOrganization: String,
    @SerialName("offer_type") val offerType: String,
    @SerialName("position") val position: String?,
    @SerialName("message") val message: String,
    @SerialName("details") val details: String?,
    @SerialName("status") val status: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("responded_at") val respondedAt: String?
)

@Serializable
data class TalentOffersResponseDto(
    @SerialName("offers") val offers: List<TalentOfferDto>,
    @SerialName("total") val total: Int
)

@Serializable
data class RespondToOfferRequestDto(
    @SerialName("offer_id") val offerId: String,
    @SerialName("response") val response: String,
    @SerialName("message") val message: String?
)
