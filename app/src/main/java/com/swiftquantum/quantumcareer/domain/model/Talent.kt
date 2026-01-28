package com.swiftquantum.quantumcareer.domain.model

import java.time.LocalDateTime

data class TalentSearchCriteria(
    val query: String? = null,
    val minHIndex: Int? = null,
    val minPublications: Int? = null,
    val badgeTier: BadgeTier? = null,
    val specializations: List<String>? = null,
    val page: Int = 1,
    val perPage: Int = 20
)

data class TalentProfile(
    val userId: String,
    val username: String,
    val displayName: String,
    val avatarUrl: String?,
    val bio: String?,
    val institution: String?,
    val specializations: List<String>,
    val hIndex: Int,
    val i10Index: Int,
    val totalPublications: Int,
    val totalCitations: Int,
    val badgeTier: BadgeTier,
    val isAvailable: Boolean,
    val profileUrl: String
)

data class TalentSearchResult(
    val profiles: List<TalentProfile>,
    val total: Int,
    val page: Int,
    val perPage: Int
) {
    val hasMore: Boolean
        get() = page * perPage < total
}

data class ScoutRequest(
    val targetUserId: String,
    val message: String,
    val offerType: OfferType,
    val organization: String,
    val position: String?,
    val details: String?
)

enum class OfferType {
    JOB,
    COLLABORATION,
    RESEARCH,
    INTERNSHIP,
    CONSULTING;

    companion object {
        fun fromString(value: String): OfferType {
            return when (value.lowercase()) {
                "job" -> JOB
                "collaboration" -> COLLABORATION
                "research" -> RESEARCH
                "internship" -> INTERNSHIP
                "consulting" -> CONSULTING
                else -> JOB
            }
        }
    }

    val displayName: String
        get() = when (this) {
            JOB -> "Job Offer"
            COLLABORATION -> "Collaboration"
            RESEARCH -> "Research Position"
            INTERNSHIP -> "Internship"
            CONSULTING -> "Consulting"
        }
}

data class TalentOffer(
    val id: String,
    val fromUserId: String,
    val fromUserName: String,
    val fromOrganization: String,
    val offerType: OfferType,
    val position: String?,
    val message: String,
    val details: String?,
    val status: OfferStatus,
    val createdAt: LocalDateTime,
    val respondedAt: LocalDateTime?
)

enum class OfferStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    WITHDRAWN;

    companion object {
        fun fromString(value: String): OfferStatus {
            return when (value.lowercase()) {
                "pending" -> PENDING
                "accepted" -> ACCEPTED
                "declined" -> DECLINED
                "withdrawn" -> WITHDRAWN
                else -> PENDING
            }
        }
    }

    val displayName: String
        get() = name.lowercase().replaceFirstChar { it.uppercase() }
}
