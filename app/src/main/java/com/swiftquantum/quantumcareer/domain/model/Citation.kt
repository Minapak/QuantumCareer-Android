package com.swiftquantum.quantumcareer.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class CitationStats(
    val totalCitations: Int,
    val hIndex: Int,
    val i10Index: Int,
    val totalPublications: Int,
    val citationHistory: List<CitationHistoryPoint>,
    val topCitedCircuits: List<TopCitedCircuit>
) {
    val averageCitationsPerPublication: Float
        get() = if (totalPublications > 0) {
            totalCitations.toFloat() / totalPublications
        } else 0f
}

data class CitationHistoryPoint(
    val date: LocalDate,
    val cumulativeCitations: Int,
    val newCitations: Int
)

data class TopCitedCircuit(
    val circuitId: String,
    val title: String,
    val doi: String,
    val citationCount: Int
) {
    val doiDisplay: String
        get() = "10.5281/sqc.2026.$doi"
}

data class CitationDetail(
    val id: String,
    val citingCircuitId: String,
    val citingCircuitTitle: String,
    val citingAuthor: String,
    val citedCircuitId: String,
    val citationContext: String?,
    val createdAt: LocalDateTime
)
