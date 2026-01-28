package com.swiftquantum.quantumcareer.domain.model

import java.time.LocalDateTime

data class PublishCircuitRequest(
    val title: String,
    val description: String,
    val qasmCode: String,
    val tags: List<String>,
    val isPublic: Boolean = true
)

data class PublishedCircuit(
    val id: String,
    val doi: String,
    val title: String,
    val description: String,
    val qasmCode: String,
    val tags: List<String>,
    val authorId: String,
    val authorName: String,
    val status: CircuitStatus,
    val citationCount: Int,
    val qubitCount: Int = 2,
    val viewCount: Int = 0,
    val forkCount: Int = 0,
    val publishedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    val doiDisplay: String
        get() = "10.5281/sqc.2026.$doi"

    val doiUrl: String
        get() = "https://doi.org/10.5281/sqc.2026.$doi"
}

enum class CircuitStatus {
    DRAFT,
    UNDER_REVIEW,
    PUBLISHED,
    REJECTED;

    companion object {
        fun fromString(value: String): CircuitStatus {
            return when (value.lowercase()) {
                "draft" -> DRAFT
                "under_review", "pending" -> UNDER_REVIEW
                "published", "approved" -> PUBLISHED
                "rejected" -> REJECTED
                else -> DRAFT
            }
        }
    }
}

data class CiteCircuitRequest(
    val citingCircuitId: String,
    val citationContext: String?
)
