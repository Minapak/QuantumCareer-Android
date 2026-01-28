package com.swiftquantum.quantumcareer.data.repository

import com.swiftquantum.quantumcareer.data.api.RankingsApi
import com.swiftquantum.quantumcareer.data.mapper.toDomain
import com.swiftquantum.quantumcareer.domain.model.*
import com.swiftquantum.quantumcareer.domain.repository.RankingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RankingsRepositoryImpl @Inject constructor(
    private val api: RankingsApi
) : RankingsRepository {

    override suspend fun getLeaderboard(filter: RankingFilter): Result<Leaderboard> {
        return try {
            val response = api.getLeaderboard(
                type = filter.type.name.lowercase(),
                country = filter.country,
                institution = filter.institution,
                minBadge = filter.minBadge?.name?.lowercase(),
                page = filter.page,
                perPage = filter.perPage
            )
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.toDomain())
                } ?: Result.failure(Exception("Failed to get leaderboard"))
            } else {
                Result.failure(Exception("Failed to get leaderboard: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyRank(): Result<UserRankingStats> {
        return try {
            val response = api.getMyRank()
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.toDomain())
                } ?: Result.failure(Exception("Failed to get user rank"))
            } else {
                Result.failure(Exception("Failed to get user rank: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFriendsRankings(page: Int, perPage: Int): Result<FriendsRanking> {
        return try {
            val response = api.getFriendsRankings(page, perPage)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.toDomain())
                } ?: Result.failure(Exception("Failed to get friends rankings"))
            } else {
                Result.failure(Exception("Failed to get friends rankings: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCountries(): Result<List<RankingCountry>> {
        return try {
            val response = api.getCountries()
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.countries.map { it.toDomain() })
                } ?: Result.failure(Exception("Failed to get countries"))
            } else {
                Result.failure(Exception("Failed to get countries: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getInstitutions(country: String?, query: String?): Result<List<RankingInstitution>> {
        return try {
            val response = api.getInstitutions(country, query)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.institutions.map { it.toDomain() })
                } ?: Result.failure(Exception("Failed to get institutions"))
            } else {
                Result.failure(Exception("Failed to get institutions: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAchievements(): Result<List<RankingAchievement>> {
        return try {
            val response = api.getAchievements()
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.achievements.map { it.toDomain() })
                } ?: Result.failure(Exception("Failed to get achievements"))
            } else {
                Result.failure(Exception("Failed to get achievements: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserRank(userId: String): Result<RankedUser> {
        return try {
            val response = api.getUserRank(userId)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.toDomain())
                } ?: Result.failure(Exception("Failed to get user rank"))
            } else {
                Result.failure(Exception("Failed to get user rank: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
