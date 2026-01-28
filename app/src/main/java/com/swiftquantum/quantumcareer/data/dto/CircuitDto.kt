package com.swiftquantum.quantumcareer.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PublishCircuitRequestDto(
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("qasm_code") val qasmCode: String,
    @SerialName("tags") val tags: List<String>,
    @SerialName("is_public") val isPublic: Boolean = true
)

@Serializable
data class PublishedCircuitDto(
    @SerialName("id") val id: String,
    @SerialName("doi") val doi: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("qasm_code") val qasmCode: String,
    @SerialName("tags") val tags: List<String>,
    @SerialName("author_id") val authorId: String,
    @SerialName("author_name") val authorName: String,
    @SerialName("status") val status: String,
    @SerialName("citation_count") val citationCount: Int,
    @SerialName("published_at") val publishedAt: String?,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)

@Serializable
data class CircuitListResponseDto(
    @SerialName("circuits") val circuits: List<PublishedCircuitDto>,
    @SerialName("total") val total: Int,
    @SerialName("page") val page: Int,
    @SerialName("per_page") val perPage: Int
)

@Serializable
data class CiteCircuitRequestDto(
    @SerialName("citing_circuit_id") val citingCircuitId: String,
    @SerialName("citation_context") val citationContext: String?
)

@Serializable
data class CiteCircuitResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("citation_id") val citationId: String?
)
