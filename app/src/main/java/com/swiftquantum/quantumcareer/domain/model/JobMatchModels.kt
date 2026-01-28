package com.swiftquantum.quantumcareer.domain.model

import java.time.LocalDateTime

/**
 * Category of match reasons for job matching.
 */
enum class MatchCategory {
    SKILLS,
    EXPERIENCE,
    EDUCATION,
    CERTIFICATIONS,
    PUBLICATIONS,
    LOCATION,
    SALARY,
    CULTURE;

    companion object {
        fun fromString(value: String): MatchCategory {
            return when (value.uppercase()) {
                "SKILLS" -> SKILLS
                "EXPERIENCE" -> EXPERIENCE
                "EDUCATION" -> EDUCATION
                "CERTIFICATIONS" -> CERTIFICATIONS
                "PUBLICATIONS" -> PUBLICATIONS
                "LOCATION" -> LOCATION
                "SALARY" -> SALARY
                "CULTURE" -> CULTURE
                else -> SKILLS
            }
        }
    }

    val displayName: String
        get() = when (this) {
            SKILLS -> "Skills Match"
            EXPERIENCE -> "Experience"
            EDUCATION -> "Education"
            CERTIFICATIONS -> "Certifications"
            PUBLICATIONS -> "Publications"
            LOCATION -> "Location"
            SALARY -> "Compensation"
            CULTURE -> "Culture Fit"
        }

    val iconName: String
        get() = when (this) {
            SKILLS -> "psychology"
            EXPERIENCE -> "work"
            EDUCATION -> "school"
            CERTIFICATIONS -> "verified"
            PUBLICATIONS -> "article"
            LOCATION -> "location_on"
            SALARY -> "payments"
            CULTURE -> "groups"
        }
}

/**
 * Represents a reason why a job matches the user's profile.
 */
data class MatchReason(
    val category: MatchCategory,
    val description: String,
    val weight: Float,
    val isPositive: Boolean = true
) {
    val weightPercentage: Int
        get() = (weight * 100).toInt()
}

/**
 * Represents a skill gap between user and job requirements.
 */
data class SkillGap(
    val skillName: String,
    val requiredLevel: SkillLevel,
    val currentLevel: SkillLevel?,
    val importance: SkillImportance,
    val learningResources: List<LearningResource>?
) {
    val gapSize: Int
        get() = (requiredLevel.ordinal - (currentLevel?.ordinal ?: 0)).coerceAtLeast(0)

    val isCritical: Boolean
        get() = importance == SkillImportance.REQUIRED && gapSize > 1
}

enum class SkillLevel {
    NONE,
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    EXPERT;

    val displayName: String
        get() = name.lowercase().replaceFirstChar { it.uppercase() }
}

enum class SkillImportance {
    REQUIRED,
    PREFERRED,
    NICE_TO_HAVE;

    val displayName: String
        get() = when (this) {
            REQUIRED -> "Required"
            PREFERRED -> "Preferred"
            NICE_TO_HAVE -> "Nice to have"
        }
}

data class LearningResource(
    val title: String,
    val url: String,
    val type: ResourceType,
    val estimatedHours: Int?
)

enum class ResourceType {
    COURSE,
    TUTORIAL,
    DOCUMENTATION,
    BOOK,
    VIDEO,
    PRACTICE;

    val displayName: String
        get() = name.lowercase().replaceFirstChar { it.uppercase() }
}

/**
 * Represents an AI-powered job match analysis.
 */
data class JobMatch(
    val jobId: String,
    val userId: String,
    val matchScore: Float,
    val matchReasons: List<MatchReason>,
    val skillGaps: List<SkillGap>,
    val overallAssessment: String,
    val strengthAreas: List<String>,
    val improvementAreas: List<String>,
    val estimatedFitLevel: FitLevel,
    val analyzedAt: LocalDateTime
) {
    val matchPercentage: Int
        get() = (matchScore * 100).toInt()

    val topReasons: List<MatchReason>
        get() = matchReasons.filter { it.isPositive }.sortedByDescending { it.weight }.take(3)

    val criticalGaps: List<SkillGap>
        get() = skillGaps.filter { it.isCritical }

    val hasGaps: Boolean
        get() = skillGaps.isNotEmpty()

    val isStrongMatch: Boolean
        get() = matchScore >= 0.75f

    val isGoodMatch: Boolean
        get() = matchScore >= 0.5f
}

enum class FitLevel {
    EXCELLENT,
    STRONG,
    GOOD,
    MODERATE,
    WEAK;

    companion object {
        fun fromScore(score: Float): FitLevel {
            return when {
                score >= 0.9f -> EXCELLENT
                score >= 0.75f -> STRONG
                score >= 0.6f -> GOOD
                score >= 0.4f -> MODERATE
                else -> WEAK
            }
        }
    }

    val displayName: String
        get() = name.lowercase().replaceFirstChar { it.uppercase() }

    val color: Long
        get() = when (this) {
            EXCELLENT -> 0xFF4CAF50  // Green
            STRONG -> 0xFF8BC34A    // Light Green
            GOOD -> 0xFFFFEB3B      // Yellow
            MODERATE -> 0xFFFF9800  // Orange
            WEAK -> 0xFFF44336      // Red
        }
}

/**
 * Represents a job recommendation with match analysis.
 */
data class JobRecommendation(
    val job: JobListing,
    val matchScore: Float,
    val reasons: List<MatchReason>,
    val appliedDate: LocalDateTime?,
    val savedDate: LocalDateTime?
) {
    val hasApplied: Boolean
        get() = appliedDate != null

    val isSaved: Boolean
        get() = savedDate != null

    val matchPercentage: Int
        get() = (matchScore * 100).toInt()
}

/**
 * Job listing for recommendations.
 */
data class JobListing(
    val id: String,
    val title: String,
    val company: String,
    val companyLogoUrl: String?,
    val location: String,
    val locationType: LocationType,
    val salaryRange: SalaryRange?,
    val description: String,
    val requirements: List<String>,
    val postedAt: LocalDateTime,
    val applicationDeadline: LocalDateTime?,
    val jobType: JobType
) {
    val isRemote: Boolean
        get() = locationType == LocationType.REMOTE

    val isExpired: Boolean
        get() = applicationDeadline?.isBefore(LocalDateTime.now()) ?: false
}

enum class LocationType {
    REMOTE,
    HYBRID,
    ON_SITE;

    val displayName: String
        get() = when (this) {
            REMOTE -> "Remote"
            HYBRID -> "Hybrid"
            ON_SITE -> "On-site"
        }
}

data class SalaryRange(
    val min: Int,
    val max: Int,
    val currency: String,
    val period: SalaryPeriod
) {
    val formattedRange: String
        get() = "$currency${formatNumber(min)} - $currency${formatNumber(max)} / ${period.shortName}"

    private fun formatNumber(num: Int): String {
        return when {
            num >= 1000000 -> "${num / 1000000}M"
            num >= 1000 -> "${num / 1000}K"
            else -> num.toString()
        }
    }
}

enum class SalaryPeriod {
    HOURLY,
    MONTHLY,
    YEARLY;

    val shortName: String
        get() = when (this) {
            HOURLY -> "hr"
            MONTHLY -> "mo"
            YEARLY -> "yr"
        }
}

enum class JobType {
    FULL_TIME,
    PART_TIME,
    CONTRACT,
    INTERNSHIP,
    FREELANCE;

    val displayName: String
        get() = name.replace("_", "-").lowercase().replaceFirstChar { it.uppercase() }
}

/**
 * List of job recommendations.
 */
data class JobRecommendationList(
    val recommendations: List<JobRecommendation>,
    val totalJobs: Int,
    val lastUpdated: LocalDateTime
)

/**
 * User's job match preferences.
 */
data class JobMatchPreferences(
    val preferredLocations: List<String>,
    val locationType: List<LocationType>,
    val minSalary: Int?,
    val preferredJobTypes: List<JobType>,
    val preferredCompanySizes: List<CompanySize>,
    val prioritySkills: List<String>
)

enum class CompanySize {
    STARTUP,
    SMALL,
    MEDIUM,
    LARGE,
    ENTERPRISE;

    val displayName: String
        get() = when (this) {
            STARTUP -> "Startup (1-10)"
            SMALL -> "Small (11-50)"
            MEDIUM -> "Medium (51-200)"
            LARGE -> "Large (201-1000)"
            ENTERPRISE -> "Enterprise (1000+)"
        }
}
