package com.swiftquantum.quantumcareer.data.api

import com.swiftquantum.quantumcareer.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface RankingsApi {

    /**
     * Get global leaderboard with optional filters.
     */
    @GET("career-passport/rankings")
    suspend fun getLeaderboard(
        @Query("type") type: String = "overall",
        @Query("country") country: String? = null,
        @Query("institution") institution: String? = null,
        @Query("min_badge") minBadge: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 50
    ): Response<LeaderboardResponseDto>

    /**
     * Get current user's rank and statistics.
     */
    @GET("career-passport/rankings/me")
    suspend fun getMyRank(): Response<UserRankingStatsDto>

    /**
     * Get friends' rankings.
     */
    @GET("career-passport/rankings/friends")
    suspend fun getFriendsRankings(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<FriendsRankingResponseDto>

    /**
     * Get available countries for filtering.
     */
    @GET("career-passport/rankings/countries")
    suspend fun getCountries(): Response<RankingCountriesResponseDto>

    /**
     * Get available institutions for filtering.
     */
    @GET("career-passport/rankings/institutions")
    suspend fun getInstitutions(
        @Query("country") country: String? = null,
        @Query("query") query: String? = null
    ): Response<RankingInstitutionsResponseDto>

    /**
     * Get ranking achievements.
     */
    @GET("career-passport/rankings/achievements")
    suspend fun getAchievements(): Response<RankingAchievementsResponseDto>

    /**
     * Get a specific user's rank.
     */
    @GET("career-passport/rankings/user/{userId}")
    suspend fun getUserRank(
        @Path("userId") userId: String
    ): Response<RankedUserDto>
}
