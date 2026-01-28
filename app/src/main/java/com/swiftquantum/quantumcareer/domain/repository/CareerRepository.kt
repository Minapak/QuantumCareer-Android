package com.swiftquantum.quantumcareer.domain.repository

import com.swiftquantum.quantumcareer.domain.model.*

interface CareerRepository {
    // Circuits
    suspend fun getCircuits(page: Int = 1, perPage: Int = 20, status: CircuitStatus? = null): Result<List<PublishedCircuit>>
    suspend fun getCircuit(circuitId: String): Result<PublishedCircuit>
    suspend fun publishCircuit(request: PublishCircuitRequest): Result<PublishedCircuit>
    suspend fun citeCircuit(circuitId: String, request: CiteCircuitRequest): Result<Boolean>
    suspend fun deleteCircuit(circuitId: String): Result<Boolean>

    // Badges
    suspend fun getBadges(): Result<BadgeCollection>

    // Citations
    suspend fun getCitationStats(): Result<CitationStats>
    suspend fun getCircuitCitations(circuitId: String): Result<List<CitationDetail>>

    // Profile
    suspend fun getProfile(): Result<PublicProfile>
    suspend fun updateProfile(request: UpdateProfileRequest): Result<PublicProfile>
    suspend fun getPublicProfile(username: String): Result<PublicProfile>

    // Dashboard
    suspend fun getDashboardStats(): Result<DashboardStats>
}
