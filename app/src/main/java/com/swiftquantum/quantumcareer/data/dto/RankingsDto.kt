package com.swiftquantum.quantumcareer.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ============== Response DTOs ==============

@Serializable
data class RankedUserDto(
    @SerialName("rank") val rank: Int,
    @SerialName("user_id") val userId: String,
    @SerialName("name") val name: String,
    @SerialName("avatar_url") val avatarUrl: String?,
    @SerialName("score") val score: Int,
    @SerialName("badges") val badges: List<String>,
    @SerialName("institution") val institution: String?,
    @SerialName("country") val country: String?,
    @SerialName("country_code") val countryCode: String?,
    @SerialName("total_tests") val totalTests: Int,
    @SerialName("best_percentage") val bestPercentage: Float,
    @SerialName("h_index") val hIndex: Int?,
    @SerialName("publications") val publications: Int?
)

@Serializable
data class LeaderboardResponseDto(
    @SerialName("type") val type: String,
    @SerialName("entries") val entries: List<RankedUserDto>,
    @SerialName("user_rank") val userRank: RankedUserDto?,
    @SerialName("total_participants") val totalParticipants: Int,
    @SerialName("last_updated") val lastUpdated: String,
    @SerialName("filter_country") val filterCountry: String?,
    @SerialName("filter_institution") val filterInstitution: String?,
    @SerialName("page") val page: Int,
    @SerialName("per_page") val perPage: Int,
    @SerialName("total_pages") val totalPages: Int
)

@Serializable
data class UserRankingStatsDto(
    @SerialName("current_rank") val currentRank: Int,
    @SerialName("previous_rank") val previousRank: Int?,
    @SerialName("best_rank") val bestRank: Int,
    @SerialName("total_score") val totalScore: Int,
    @SerialName("tests_completed") val testsCompleted: Int,
    @SerialName("average_percentage") val averagePercentage: Float,
    @SerialName("rank_in_country") val rankInCountry: Int?,
    @SerialName("country_total") val countryTotal: Int?,
    @SerialName("rank_in_institution") val rankInInstitution: Int?,
    @SerialName("institution_total") val institutionTotal: Int?,
    @SerialName("percentile") val percentile: Float,
    @SerialName("last_updated") val lastUpdated: String
)

@Serializable
data class FriendsRankingResponseDto(
    @SerialName("friends") val friends: List<RankedUserDto>,
    @SerialName("user_rank_among_friends") val userRankAmongFriends: Int,
    @SerialName("total_friends") val totalFriends: Int
)

@Serializable
data class RankingCountryDto(
    @SerialName("code") val code: String,
    @SerialName("name") val name: String,
    @SerialName("participant_count") val participantCount: Int,
    @SerialName("flag_emoji") val flagEmoji: String?
)

@Serializable
data class RankingCountriesResponseDto(
    @SerialName("countries") val countries: List<RankingCountryDto>
)

@Serializable
data class RankingInstitutionDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("country") val country: String?,
    @SerialName("participant_count") val participantCount: Int,
    @SerialName("average_score") val averageScore: Float
)

@Serializable
data class RankingInstitutionsResponseDto(
    @SerialName("institutions") val institutions: List<RankingInstitutionDto>
)

@Serializable
data class RankingAchievementDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("achieved_at") val achievedAt: String?,
    @SerialName("is_achieved") val isAchieved: Boolean,
    @SerialName("progress") val progress: Float
)

@Serializable
data class RankingAchievementsResponseDto(
    @SerialName("achievements") val achievements: List<RankingAchievementDto>
)
