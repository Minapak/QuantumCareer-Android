package com.swiftquantum.quantumcareer.data.repository

import com.swiftquantum.quantumcareer.data.api.CareerPassportApi
import com.swiftquantum.quantumcareer.data.dto.*
import com.swiftquantum.quantumcareer.data.mapper.toDomain
import com.swiftquantum.quantumcareer.data.mapper.toDto
import com.swiftquantum.quantumcareer.domain.model.*
import com.swiftquantum.quantumcareer.domain.repository.CareerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CareerRepositoryImpl @Inject constructor(
    private val api: CareerPassportApi
) : CareerRepository {

    override suspend fun getCircuits(
        page: Int,
        perPage: Int,
        status: CircuitStatus?
    ): Result<List<PublishedCircuit>> {
        return try {
            val response = api.getCircuits(page, perPage, status?.name?.lowercase())
            if (response.isSuccessful) {
                val body = response.body()
                Result.success(body?.circuits?.map { it.toDomain() } ?: emptyList())
            } else {
                // Return empty list for guest/offline mode
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            // Return empty list on API failure
            Result.success(emptyList())
        }
    }

    override suspend fun getCircuit(circuitId: String): Result<PublishedCircuit> {
        return try {
            val response = api.getCircuit(circuitId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomain())
                } ?: Result.failure(Exception("Circuit not found"))
            } else {
                Result.failure(Exception("Failed to get circuit: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun publishCircuit(request: PublishCircuitRequest): Result<PublishedCircuit> {
        return try {
            val response = api.publishCircuit(request.toDto())
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomain())
                } ?: Result.failure(Exception("Failed to publish circuit"))
            } else {
                Result.failure(Exception("Failed to publish circuit: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun citeCircuit(circuitId: String, request: CiteCircuitRequest): Result<Boolean> {
        return try {
            val response = api.citeCircuit(circuitId, request.toDto())
            if (response.isSuccessful) {
                Result.success(response.body()?.success ?: false)
            } else {
                Result.failure(Exception("Failed to cite circuit: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCircuit(circuitId: String): Result<Boolean> {
        return try {
            val response = api.deleteCircuit(circuitId)
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBadges(): Result<BadgeCollection> {
        return try {
            val response = api.getBadges()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomain())
                } ?: Result.success(getDefaultBadgeCollection())
            } else {
                // Return default badges for guest/offline mode
                Result.success(getDefaultBadgeCollection())
            }
        } catch (e: Exception) {
            // Return default badges on API failure
            Result.success(getDefaultBadgeCollection())
        }
    }

    private fun getDefaultBadgeCollection(): BadgeCollection {
        return BadgeCollection(
            badges = emptyList(),
            nextBadge = null,
            currentTier = BadgeTier.BRONZE
        )
    }

    override suspend fun getCitationStats(): Result<CitationStats> {
        return try {
            val response = api.getCitationStats()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomain())
                } ?: Result.success(getDefaultCitationStats())
            } else {
                // Return default stats for guest/offline mode
                Result.success(getDefaultCitationStats())
            }
        } catch (e: Exception) {
            // Return default stats on API failure
            Result.success(getDefaultCitationStats())
        }
    }

    private fun getDefaultCitationStats(): CitationStats {
        return CitationStats(
            totalCitations = 0,
            hIndex = 0,
            i10Index = 0,
            totalPublications = 0,
            citationHistory = emptyList(),
            topCitedCircuits = emptyList()
        )
    }

    override suspend fun getCircuitCitations(circuitId: String): Result<List<CitationDetail>> {
        return try {
            val response = api.getCircuitCitations(circuitId)
            if (response.isSuccessful) {
                Result.success(response.body()?.map { it.toDomain() } ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get citations: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProfile(): Result<PublicProfile> {
        return try {
            val response = api.getProfile()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomain())
                } ?: Result.success(getDefaultProfile())
            } else {
                // Return default profile for guest/offline mode
                Result.success(getDefaultProfile())
            }
        } catch (e: Exception) {
            // Return default profile on API failure
            Result.success(getDefaultProfile())
        }
    }

    private fun getDefaultProfile(): PublicProfile {
        return PublicProfile(
            userId = "",
            username = "guest",
            displayName = "Guest User",
            avatarUrl = null,
            bio = null,
            institution = null,
            location = null,
            website = null,
            specializations = emptyList(),
            hIndex = 0,
            i10Index = 0,
            totalPublications = 0,
            totalCitations = 0,
            badgeTier = BadgeTier.BRONZE,
            badges = emptyList(),
            recentCircuits = emptyList(),
            isPublic = false,
            profileUrl = "",
            joinedAt = java.time.LocalDateTime.now()
        )
    }

    override suspend fun updateProfile(request: UpdateProfileRequest): Result<PublicProfile> {
        return try {
            val response = api.updateProfile(request.toDto())
            if (response.isSuccessful) {
                response.body()?.profile?.let {
                    Result.success(it.toDomain())
                } ?: Result.failure(Exception("Failed to update profile"))
            } else {
                Result.failure(Exception("Failed to update profile: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPublicProfile(username: String): Result<PublicProfile> {
        return try {
            val response = api.getPublicProfile(username)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomain())
                } ?: Result.failure(Exception("Profile not found"))
            } else {
                Result.failure(Exception("Failed to get public profile: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDashboardStats(): Result<DashboardStats> {
        return try {
            val response = api.getDashboardStats()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomain())
                } ?: Result.success(getDefaultDashboardStats())
            } else {
                // Return default stats for guest/offline mode
                Result.success(getDefaultDashboardStats())
            }
        } catch (e: Exception) {
            // Return default stats on API failure
            Result.success(getDefaultDashboardStats())
        }
    }

    private fun getDefaultDashboardStats(): DashboardStats {
        return DashboardStats(
            totalPublications = 0,
            totalCitations = 0,
            hIndex = 0,
            i10Index = 0,
            pendingReviews = 0,
            currentBadgeTier = BadgeTier.BRONZE,
            nextBadgeProgress = 0f,
            recentActivity = emptyList()
        )
    }
}
