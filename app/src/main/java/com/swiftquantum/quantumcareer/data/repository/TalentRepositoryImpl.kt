package com.swiftquantum.quantumcareer.data.repository

import com.swiftquantum.quantumcareer.data.api.TalentApi
import com.swiftquantum.quantumcareer.data.dto.*
import com.swiftquantum.quantumcareer.data.mapper.toDomain
import com.swiftquantum.quantumcareer.domain.model.*
import com.swiftquantum.quantumcareer.domain.repository.TalentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TalentRepositoryImpl @Inject constructor(
    private val api: TalentApi
) : TalentRepository {

    override suspend fun searchTalent(criteria: TalentSearchCriteria): Result<TalentSearchResult> {
        return try {
            val request = TalentSearchRequestDto(
                query = criteria.query,
                minHIndex = criteria.minHIndex,
                minPublications = criteria.minPublications,
                badgeTier = criteria.badgeTier?.name?.lowercase(),
                specializations = criteria.specializations,
                page = criteria.page,
                perPage = criteria.perPage
            )
            val response = api.searchTalent(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomain())
                } ?: Result.failure(Exception("Failed to search talent"))
            } else {
                Result.failure(Exception("Failed to search talent: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTalentProfile(userId: String): Result<TalentProfile> {
        return try {
            val response = api.getTalentProfile(userId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomain())
                } ?: Result.failure(Exception("Profile not found"))
            } else {
                Result.failure(Exception("Failed to get talent profile: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun scoutTalent(request: ScoutRequest): Result<Boolean> {
        return try {
            val dto = ScoutRequestDto(
                targetUserId = request.targetUserId,
                message = request.message,
                offerType = request.offerType.name.lowercase(),
                organization = request.organization,
                position = request.position,
                details = request.details
            )
            val response = api.scoutTalent(dto)
            if (response.isSuccessful) {
                Result.success(response.body()?.success ?: false)
            } else {
                Result.failure(Exception("Failed to scout talent: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getOffers(type: OfferType?, status: OfferStatus?): Result<List<TalentOffer>> {
        return try {
            val response = api.getOffers(
                type = type?.name?.lowercase(),
                status = status?.name?.lowercase()
            )
            if (response.isSuccessful) {
                Result.success(response.body()?.offers?.map { it.toDomain() } ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get offers: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSentOffers(): Result<List<TalentOffer>> {
        return try {
            val response = api.getSentOffers()
            if (response.isSuccessful) {
                Result.success(response.body()?.offers?.map { it.toDomain() } ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get sent offers: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun respondToOffer(offerId: String, accept: Boolean, message: String?): Result<Boolean> {
        return try {
            val dto = RespondToOfferRequestDto(
                offerId = offerId,
                response = if (accept) "accepted" else "declined",
                message = message
            )
            val response = api.respondToOffer(offerId, dto)
            if (response.isSuccessful) {
                Result.success(response.body()?.success ?: false)
            } else {
                Result.failure(Exception("Failed to respond to offer: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun withdrawOffer(offerId: String): Result<Boolean> {
        return try {
            val response = api.withdrawOffer(offerId)
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
