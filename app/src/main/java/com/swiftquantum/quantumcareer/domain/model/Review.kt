package com.swiftquantum.quantumcareer.domain.model

import java.time.LocalDateTime

data class PeerReview(
    val id: String,
    val circuitId: String,
    val circuitTitle: String,
    val circuitAuthor: String,
    val reviewerId: String?,
    val reviewerName: String?,
    val reviewerLevel: ReviewerLevel?,
    val status: ReviewStatus,
    val decision: ReviewDecision?,
    val comment: String?,
    val submittedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val qasmCode: String?
)

enum class ReviewerLevel {
    JUNIOR,
    SENIOR,
    EXPERT;

    companion object {
        fun fromString(value: String?): ReviewerLevel? {
            return when (value?.lowercase()) {
                "junior" -> JUNIOR
                "senior" -> SENIOR
                "expert" -> EXPERT
                else -> null
            }
        }
    }

    val displayName: String
        get() = when (this) {
            JUNIOR -> "Junior Reviewer"
            SENIOR -> "Senior Reviewer"
            EXPERT -> "Expert Reviewer"
        }

    val requiredReviews: Int
        get() = when (this) {
            JUNIOR -> 0
            SENIOR -> 10
            EXPERT -> 50
        }
}

enum class ReviewStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED;

    companion object {
        fun fromString(value: String): ReviewStatus {
            return when (value.lowercase()) {
                "pending" -> PENDING
                "in_progress", "claimed" -> IN_PROGRESS
                "completed", "submitted" -> COMPLETED
                else -> PENDING
            }
        }
    }
}

enum class ReviewDecision {
    APPROVED,
    REQUEST_CHANGES,
    REJECTED;

    companion object {
        fun fromString(value: String?): ReviewDecision? {
            return when (value?.lowercase()) {
                "approved", "approve" -> APPROVED
                "request_changes", "changes_requested" -> REQUEST_CHANGES
                "rejected", "reject" -> REJECTED
                else -> null
            }
        }
    }

    val displayName: String
        get() = when (this) {
            APPROVED -> "Approved"
            REQUEST_CHANGES -> "Changes Requested"
            REJECTED -> "Rejected"
        }
}

data class SubmitReviewRequest(
    val reviewId: String,
    val decision: ReviewDecision,
    val comment: String,
    val technicalScore: Int? = null,
    val innovationScore: Int? = null,
    val documentationScore: Int? = null
)

data class ReviewerStats(
    val totalReviews: Int,
    val approvedCount: Int,
    val rejectedCount: Int,
    val reviewerLevel: ReviewerLevel,
    val reviewsUntilNextLevel: Int?
)
