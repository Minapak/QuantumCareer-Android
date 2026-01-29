package com.swiftquantum.quantumcareer.data.repository

import com.swiftquantum.quantumcareer.data.api.RankingsApi
import com.swiftquantum.quantumcareer.data.mapper.toDomain
import com.swiftquantum.quantumcareer.domain.model.*
import com.swiftquantum.quantumcareer.domain.repository.RankingsRepository
import java.time.LocalDateTime
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
                } ?: Result.success(getEmptyLeaderboard(filter))
            } else {
                // Return empty leaderboard for guest/offline mode
                Result.success(getEmptyLeaderboard(filter))
            }
        } catch (e: Exception) {
            // Return empty leaderboard on API failure
            Result.success(getEmptyLeaderboard(filter))
        }
    }

    private fun getEmptyLeaderboard(filter: RankingFilter): Leaderboard {
        return Leaderboard(
            type = filter.type,
            entries = emptyList(),
            userRank = null,
            totalParticipants = 0,
            lastUpdated = LocalDateTime.now(),
            filterCountry = filter.country,
            filterInstitution = filter.institution
        )
    }

    override suspend fun getMyRank(): Result<UserRankingStats> {
        return try {
            val response = api.getMyRank()
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.toDomain())
                } ?: Result.success(getDefaultUserRankingStats())
            } else {
                // Return default stats for guest/offline mode
                Result.success(getDefaultUserRankingStats())
            }
        } catch (e: Exception) {
            // Return default stats on API failure
            Result.success(getDefaultUserRankingStats())
        }
    }

    private fun getDefaultUserRankingStats(): UserRankingStats {
        return UserRankingStats(
            currentRank = 0,
            previousRank = null,
            bestRank = 0,
            totalScore = 0,
            testsCompleted = 0,
            averagePercentage = 0f,
            rankInCountry = null,
            countryTotal = null,
            rankInInstitution = null,
            institutionTotal = null,
            percentile = 0f,
            lastUpdated = LocalDateTime.now()
        )
    }

    override suspend fun getFriendsRankings(page: Int, perPage: Int): Result<FriendsRanking> {
        return try {
            val response = api.getFriendsRankings(page, perPage)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.toDomain())
                } ?: Result.success(getEmptyFriendsRanking())
            } else {
                // Return empty friends ranking for guest/offline mode
                Result.success(getEmptyFriendsRanking())
            }
        } catch (e: Exception) {
            // Return empty friends ranking on API failure
            Result.success(getEmptyFriendsRanking())
        }
    }

    private fun getEmptyFriendsRanking(): FriendsRanking {
        return FriendsRanking(
            friends = emptyList(),
            userRankAmongFriends = 0,
            totalFriends = 0
        )
    }

    override suspend fun getCountries(): Result<List<RankingCountry>> {
        return try {
            val response = api.getCountries()
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.countries.map { it.toDomain() })
                } ?: Result.success(emptyList())
            } else {
                // Return empty list for guest/offline mode
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            // Return empty list on API failure
            Result.success(emptyList())
        }
    }

    override suspend fun getInstitutions(country: String?, query: String?): Result<List<RankingInstitution>> {
        return try {
            val response = api.getInstitutions(country, query)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.institutions.map { it.toDomain() })
                } ?: Result.success(emptyList())
            } else {
                // Return empty list for guest/offline mode
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            // Return empty list on API failure
            Result.success(emptyList())
        }
    }

    override suspend fun getAchievements(): Result<List<RankingAchievement>> {
        return try {
            val response = api.getAchievements()
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.achievements.map { it.toDomain() })
                } ?: Result.success(emptyList())
            } else {
                // Return empty list for guest/offline mode
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            // Return empty list on API failure
            Result.success(emptyList())
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
