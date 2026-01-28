package com.swiftquantum.quantumcareer.domain.model

import java.time.LocalDateTime

data class CareerBadge(
    val id: String,
    val tier: BadgeTier,
    val name: String,
    val description: String,
    val earned: Boolean,
    val earnedAt: LocalDateTime?,
    val progress: BadgeProgress?
)

enum class BadgeTier {
    BRONZE,
    SILVER,
    GOLD,
    PLATINUM;

    companion object {
        fun fromString(value: String): BadgeTier {
            return when (value.lowercase()) {
                "bronze" -> BRONZE
                "silver" -> SILVER
                "gold" -> GOLD
                "platinum" -> PLATINUM
                else -> BRONZE
            }
        }
    }

    val displayName: String
        get() = name.lowercase().replaceFirstChar { it.uppercase() }

    val requirements: BadgeRequirements
        get() = when (this) {
            BRONZE -> BadgeRequirements(publications = 1, citations = 0)
            SILVER -> BadgeRequirements(publications = 5, citations = 10)
            GOLD -> BadgeRequirements(publications = 20, citations = 50)
            PLATINUM -> BadgeRequirements(publications = 50, citations = 200)
        }

    val nextTier: BadgeTier?
        get() = when (this) {
            BRONZE -> SILVER
            SILVER -> GOLD
            GOLD -> PLATINUM
            PLATINUM -> null
        }
}

data class BadgeRequirements(
    val publications: Int,
    val citations: Int
)

data class BadgeProgress(
    val publicationsRequired: Int,
    val publicationsCurrent: Int,
    val citationsRequired: Int,
    val citationsCurrent: Int,
    val percentage: Float
) {
    val publicationsProgress: Float
        get() = if (publicationsRequired > 0) {
            (publicationsCurrent.toFloat() / publicationsRequired).coerceIn(0f, 1f)
        } else 1f

    val citationsProgress: Float
        get() = if (citationsRequired > 0) {
            (citationsCurrent.toFloat() / citationsRequired).coerceIn(0f, 1f)
        } else 1f
}

data class BadgeCollection(
    val badges: List<CareerBadge>,
    val nextBadge: CareerBadge?,
    val currentTier: BadgeTier
)
