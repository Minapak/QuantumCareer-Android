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
                Result.failure(Exception("Failed to get circuits: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
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
                } ?: Result.failure(Exception("Failed to get badges"))
            } else {
                Result.failure(Exception("Failed to get badges: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCitationStats(): Result<CitationStats> {
        return try {
            val response = api.getCitationStats()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomain())
                } ?: Result.failure(Exception("Failed to get citation stats"))
            } else {
                Result.failure(Exception("Failed to get citation stats: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
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
                } ?: Result.failure(Exception("Failed to get profile"))
            } else {
                Result.failure(Exception("Failed to get profile: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
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
                } ?: Result.failure(Exception("Failed to get dashboard stats"))
            } else {
                Result.failure(Exception("Failed to get dashboard stats: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
