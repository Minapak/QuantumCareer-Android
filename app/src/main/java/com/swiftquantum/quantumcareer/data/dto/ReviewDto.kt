package com.swiftquantum.quantumcareer.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PeerReviewDto(
    @SerialName("id") val id: String,
    @SerialName("circuit_id") val circuitId: String,
    @SerialName("circuit_title") val circuitTitle: String,
    @SerialName("circuit_author") val circuitAuthor: String,
    @SerialName("reviewer_id") val reviewerId: String?,
    @SerialName("reviewer_name") val reviewerName: String?,
    @SerialName("reviewer_level") val reviewerLevel: String?,
    @SerialName("status") val status: String,
    @SerialName("decision") val decision: String?,
    @SerialName("comment") val comment: String?,
    @SerialName("submitted_at") val submittedAt: String?,
    @SerialName("created_at") val createdAt: String,
    @SerialName("qasm_code") val qasmCode: String?
)

@Serializable
data class PendingReviewsResponseDto(
    @SerialName("reviews") val reviews: List<PeerReviewDto>,
    @SerialName("total") val total: Int
)

@Serializable
data class SubmitReviewRequestDto(
    @SerialName("review_id") val reviewId: String,
    @SerialName("decision") val decision: String,
    @SerialName("comment") val comment: String,
    @SerialName("technical_score") val technicalScore: Int?,
    @SerialName("innovation_score") val innovationScore: Int?,
    @SerialName("documentation_score") val documentationScore: Int?
)

@Serializable
data class SubmitReviewResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("review_id") val reviewId: String?
)

@Serializable
data class ReviewerStatsDto(
    @SerialName("total_reviews") val totalReviews: Int,
    @SerialName("approved_count") val approvedCount: Int,
    @SerialName("rejected_count") val rejectedCount: Int,
    @SerialName("reviewer_level") val reviewerLevel: String,
    @SerialName("reviews_until_next_level") val reviewsUntilNextLevel: Int?
)
