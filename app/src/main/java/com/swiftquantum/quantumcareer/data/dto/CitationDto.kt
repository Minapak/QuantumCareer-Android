package com.swiftquantum.quantumcareer.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CitationStatsDto(
    @SerialName("total_citations") val totalCitations: Int,
    @SerialName("h_index") val hIndex: Int,
    @SerialName("i10_index") val i10Index: Int,
    @SerialName("total_publications") val totalPublications: Int,
    @SerialName("citation_history") val citationHistory: List<CitationHistoryPointDto>,
    @SerialName("top_cited_circuits") val topCitedCircuits: List<TopCitedCircuitDto>
)

@Serializable
data class CitationHistoryPointDto(
    @SerialName("date") val date: String,
    @SerialName("cumulative_citations") val cumulativeCitations: Int,
    @SerialName("new_citations") val newCitations: Int
)

@Serializable
data class TopCitedCircuitDto(
    @SerialName("circuit_id") val circuitId: String,
    @SerialName("title") val title: String,
    @SerialName("doi") val doi: String,
    @SerialName("citation_count") val citationCount: Int
)

@Serializable
data class CitationDetailDto(
    @SerialName("id") val id: String,
    @SerialName("citing_circuit_id") val citingCircuitId: String,
    @SerialName("citing_circuit_title") val citingCircuitTitle: String,
    @SerialName("citing_author") val citingAuthor: String,
    @SerialName("cited_circuit_id") val citedCircuitId: String,
    @SerialName("citation_context") val citationContext: String?,
    @SerialName("created_at") val createdAt: String
)
