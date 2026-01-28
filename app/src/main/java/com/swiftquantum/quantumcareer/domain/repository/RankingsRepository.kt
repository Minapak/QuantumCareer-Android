package com.swiftquantum.quantumcareer.domain.repository

import com.swiftquantum.quantumcareer.domain.model.*

interface RankingsRepository {

    /**
     * Get the leaderboard with optional filters.
     */
    suspend fun getLeaderboard(filter: RankingFilter = RankingFilter()): Result<Leaderboard>

    /**
     * Get current user's ranking statistics.
     */
    suspend fun getMyRank(): Result<UserRankingStats>

    /**
     * Get friends' rankings.
     */
    suspend fun getFriendsRankings(page: Int = 1, perPage: Int = 20): Result<FriendsRanking>

    /**
     * Get available countries for filtering.
     */
    suspend fun getCountries(): Result<List<RankingCountry>>

    /**
     * Get available institutions for filtering.
     */
    suspend fun getInstitutions(country: String? = null, query: String? = null): Result<List<RankingInstitution>>

    /**
     * Get ranking achievements.
     */
    suspend fun getAchievements(): Result<List<RankingAchievement>>

    /**
     * Get a specific user's rank.
     */
    suspend fun getUserRank(userId: String): Result<RankedUser>
}
