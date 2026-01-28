package com.swiftquantum.quantumcareer.domain.model

import java.time.LocalDateTime

/**
 * Represents the type of ranking/leaderboard.
 */
enum class RankingType {
    OVERALL,
    MONTHLY,
    WEEKLY,
    BY_COUNTRY,
    BY_INSTITUTION;

    companion object {
        fun fromString(value: String): RankingType {
            return when (value.uppercase()) {
                "OVERALL" -> OVERALL
                "MONTHLY" -> MONTHLY
                "WEEKLY" -> WEEKLY
                "BY_COUNTRY" -> BY_COUNTRY
                "BY_INSTITUTION" -> BY_INSTITUTION
                else -> OVERALL
            }
        }
    }

    val displayName: String
        get() = when (this) {
            OVERALL -> "All Time"
            MONTHLY -> "This Month"
            WEEKLY -> "This Week"
            BY_COUNTRY -> "By Country"
            BY_INSTITUTION -> "By Institution"
        }
}

/**
 * Represents a ranked user in the leaderboard.
 */
data class RankedUser(
    val rank: Int,
    val userId: String,
    val name: String,
    val avatarUrl: String?,
    val score: Int,
    val badges: List<BadgeTier>,
    val institution: String?,
    val country: String?,
    val countryCode: String?,
    val totalTests: Int,
    val bestPercentage: Float,
    val hIndex: Int?,
    val publications: Int?
) {
    val highestBadge: BadgeTier?
        get() = badges.maxByOrNull { it.ordinal }

    val formattedScore: String
        get() = when {
            score >= 1000000 -> "${score / 1000000}M"
            score >= 1000 -> "${score / 1000}K"
            else -> score.toString()
        }

    val rankSuffix: String
        get() = when {
            rank % 100 in 11..13 -> "th"
            rank % 10 == 1 -> "st"
            rank % 10 == 2 -> "nd"
            rank % 10 == 3 -> "rd"
            else -> "th"
        }

    val formattedRank: String
        get() = "$rank$rankSuffix"

    val isTopTen: Boolean
        get() = rank <= 10

    val isTopHundred: Boolean
        get() = rank <= 100
}

/**
 * Represents a leaderboard with entries and metadata.
 */
data class Leaderboard(
    val type: RankingType,
    val entries: List<RankedUser>,
    val userRank: RankedUser?,
    val totalParticipants: Int,
    val lastUpdated: LocalDateTime,
    val filterCountry: String?,
    val filterInstitution: String?
) {
    val hasUserRank: Boolean
        get() = userRank != null

    val isFiltered: Boolean
        get() = filterCountry != null || filterInstitution != null

    val topThree: List<RankedUser>
        get() = entries.take(3)

    val restOfList: List<RankedUser>
        get() = entries.drop(3)
}

/**
 * Filter options for the leaderboard.
 */
data class RankingFilter(
    val type: RankingType = RankingType.OVERALL,
    val country: String? = null,
    val institution: String? = null,
    val minBadge: BadgeTier? = null,
    val page: Int = 1,
    val perPage: Int = 50
)

/**
 * User's ranking statistics.
 */
data class UserRankingStats(
    val currentRank: Int,
    val previousRank: Int?,
    val bestRank: Int,
    val totalScore: Int,
    val testsCompleted: Int,
    val averagePercentage: Float,
    val rankInCountry: Int?,
    val countryTotal: Int?,
    val rankInInstitution: Int?,
    val institutionTotal: Int?,
    val percentile: Float,
    val lastUpdated: LocalDateTime
) {
    val rankChange: Int?
        get() = previousRank?.let { it - currentRank }

    val rankChangeStatus: RankChangeStatus
        get() {
            val change = rankChange
            return when {
                change == null -> RankChangeStatus.NEW
                change > 0 -> RankChangeStatus.UP
                change < 0 -> RankChangeStatus.DOWN
                else -> RankChangeStatus.SAME
            }
        }

    val isImproving: Boolean
        get() = rankChange?.let { it > 0 } ?: false

    val topPercentileText: String
        get() = "Top ${(100 - percentile).toInt()}%"
}

enum class RankChangeStatus {
    UP,
    DOWN,
    SAME,
    NEW;

    val displayName: String
        get() = when (this) {
            UP -> "Moved up"
            DOWN -> "Moved down"
            SAME -> "No change"
            NEW -> "New entry"
        }
}

/**
 * Friends/connections ranking.
 */
data class FriendsRanking(
    val friends: List<RankedUser>,
    val userRankAmongFriends: Int,
    val totalFriends: Int
)

/**
 * Ranking achievement/milestone.
 */
data class RankingAchievement(
    val id: String,
    val title: String,
    val description: String,
    val achievedAt: LocalDateTime?,
    val isAchieved: Boolean,
    val progress: Float
) {
    companion object {
        val TOP_100 = RankingAchievement(
            id = "top_100",
            title = "Top 100",
            description = "Reach top 100 in global rankings",
            achievedAt = null,
            isAchieved = false,
            progress = 0f
        )

        val TOP_10 = RankingAchievement(
            id = "top_10",
            title = "Top 10",
            description = "Reach top 10 in global rankings",
            achievedAt = null,
            isAchieved = false,
            progress = 0f
        )

        val COUNTRY_LEADER = RankingAchievement(
            id = "country_leader",
            title = "Country Champion",
            description = "Reach #1 in your country",
            achievedAt = null,
            isAchieved = false,
            progress = 0f
        )
    }
}

/**
 * Available countries for filtering.
 */
data class RankingCountry(
    val code: String,
    val name: String,
    val participantCount: Int,
    val flagEmoji: String?
)

/**
 * Available institutions for filtering.
 */
data class RankingInstitution(
    val id: String,
    val name: String,
    val country: String?,
    val participantCount: Int,
    val averageScore: Float
)
