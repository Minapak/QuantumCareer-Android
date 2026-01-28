package com.swiftquantum.quantumcareer.domain.model

import java.time.LocalDateTime

/**
 * Represents a job listing.
 */
data class Job(
    val id: String,
    val title: String,
    val company: Company,
    val location: String,
    val locationType: LocationType,
    val employmentType: EmploymentType,
    val experienceLevel: ExperienceLevel,
    val description: String,
    val requirements: List<String>,
    val responsibilities: List<String>,
    val requiredSkills: List<String>,
    val preferredSkills: List<String>,
    val salaryRange: SalaryRange?,
    val benefits: List<String>,
    val postedAt: LocalDateTime,
    val applicationDeadline: LocalDateTime?,
    val isRemote: Boolean,
    val isSaved: Boolean = false,
    val hasApplied: Boolean = false,
    val matchScore: Float? = null
) {
    val isExpired: Boolean
        get() = applicationDeadline?.isBefore(LocalDateTime.now()) ?: false

    val daysUntilDeadline: Int?
        get() = applicationDeadline?.let {
            java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), it).toInt()
        }

    val formattedPostedDate: String
        get() {
            val now = LocalDateTime.now()
            val days = java.time.temporal.ChronoUnit.DAYS.between(postedAt, now)
            return when {
                days == 0L -> "Today"
                days == 1L -> "Yesterday"
                days < 7 -> "$days days ago"
                days < 30 -> "${days / 7} weeks ago"
                else -> "${days / 30} months ago"
            }
        }
}

/**
 * Represents a company.
 */
data class Company(
    val id: String,
    val name: String,
    val logoUrl: String?,
    val website: String?,
    val size: CompanySize?,
    val industry: String?,
    val description: String?
)

/**
 * Employment type enum.
 */
enum class EmploymentType {
    FULL_TIME,
    PART_TIME,
    CONTRACT,
    INTERNSHIP,
    FREELANCE,
    TEMPORARY;

    val displayName: String
        get() = when (this) {
            FULL_TIME -> "Full-time"
            PART_TIME -> "Part-time"
            CONTRACT -> "Contract"
            INTERNSHIP -> "Internship"
            FREELANCE -> "Freelance"
            TEMPORARY -> "Temporary"
        }

    companion object {
        fun fromString(value: String): EmploymentType {
            return when (value.uppercase().replace("-", "_").replace(" ", "_")) {
                "FULL_TIME", "FULLTIME" -> FULL_TIME
                "PART_TIME", "PARTTIME" -> PART_TIME
                "CONTRACT" -> CONTRACT
                "INTERNSHIP" -> INTERNSHIP
                "FREELANCE" -> FREELANCE
                "TEMPORARY" -> TEMPORARY
                else -> FULL_TIME
            }
        }
    }
}

/**
 * Experience level enum.
 */
enum class ExperienceLevel {
    ENTRY,
    MID,
    SENIOR,
    LEAD,
    EXECUTIVE;

    val displayName: String
        get() = when (this) {
            ENTRY -> "Entry Level"
            MID -> "Mid Level"
            SENIOR -> "Senior Level"
            LEAD -> "Lead / Manager"
            EXECUTIVE -> "Executive"
        }

    val shortName: String
        get() = when (this) {
            ENTRY -> "Entry"
            MID -> "Mid"
            SENIOR -> "Senior"
            LEAD -> "Lead"
            EXECUTIVE -> "Exec"
        }

    companion object {
        fun fromString(value: String): ExperienceLevel {
            return when (value.uppercase().replace("-", "_").replace(" ", "_")) {
                "ENTRY", "ENTRY_LEVEL", "JUNIOR" -> ENTRY
                "MID", "MID_LEVEL", "INTERMEDIATE" -> MID
                "SENIOR", "SENIOR_LEVEL" -> SENIOR
                "LEAD", "MANAGER", "LEAD_MANAGER" -> LEAD
                "EXECUTIVE", "DIRECTOR", "VP" -> EXECUTIVE
                else -> MID
            }
        }
    }
}

/**
 * Represents a job application.
 */
data class JobApplication(
    val id: String,
    val job: Job,
    val status: ApplicationStatus,
    val appliedAt: LocalDateTime,
    val lastUpdatedAt: LocalDateTime,
    val coverLetter: String?,
    val resumeUrl: String?,
    val notes: String?,
    val nextSteps: String?,
    val interviewDate: LocalDateTime?
) {
    val statusText: String
        get() = status.displayName

    val daysSinceApplied: Int
        get() = java.time.temporal.ChronoUnit.DAYS.between(appliedAt, LocalDateTime.now()).toInt()
}

/**
 * Application status enum.
 */
enum class ApplicationStatus {
    APPLIED,
    REVIEWING,
    INTERVIEW,
    OFFER,
    REJECTED,
    WITHDRAWN;

    val displayName: String
        get() = when (this) {
            APPLIED -> "Applied"
            REVIEWING -> "Under Review"
            INTERVIEW -> "Interview"
            OFFER -> "Offer"
            REJECTED -> "Rejected"
            WITHDRAWN -> "Withdrawn"
        }

    val color: Long
        get() = when (this) {
            APPLIED -> 0xFF2196F3  // Blue
            REVIEWING -> 0xFFFF9800  // Orange
            INTERVIEW -> 0xFF9C27B0  // Purple
            OFFER -> 0xFF4CAF50  // Green
            REJECTED -> 0xFFF44336  // Red
            WITHDRAWN -> 0xFF9E9E9E  // Gray
        }

    companion object {
        fun fromString(value: String): ApplicationStatus {
            return when (value.uppercase().replace("-", "_").replace(" ", "_")) {
                "APPLIED" -> APPLIED
                "REVIEWING", "UNDER_REVIEW", "IN_REVIEW" -> REVIEWING
                "INTERVIEW", "INTERVIEWING" -> INTERVIEW
                "OFFER", "OFFERED" -> OFFER
                "REJECTED" -> REJECTED
                "WITHDRAWN" -> WITHDRAWN
                else -> APPLIED
            }
        }
    }
}

/**
 * Job filter options.
 */
data class JobFilter(
    val searchQuery: String = "",
    val locationTypes: List<LocationType> = emptyList(),
    val employmentTypes: List<EmploymentType> = emptyList(),
    val experienceLevels: List<ExperienceLevel> = emptyList(),
    val skills: List<String> = emptyList(),
    val minSalary: Int? = null,
    val maxSalary: Int? = null,
    val companySizes: List<CompanySize> = emptyList(),
    val postedWithin: PostedWithin? = null
) {
    val hasActiveFilters: Boolean
        get() = searchQuery.isNotBlank() ||
                locationTypes.isNotEmpty() ||
                employmentTypes.isNotEmpty() ||
                experienceLevels.isNotEmpty() ||
                skills.isNotEmpty() ||
                minSalary != null ||
                maxSalary != null ||
                companySizes.isNotEmpty() ||
                postedWithin != null

    val activeFilterCount: Int
        get() {
            var count = 0
            if (searchQuery.isNotBlank()) count++
            count += locationTypes.size
            count += employmentTypes.size
            count += experienceLevels.size
            count += skills.size
            if (minSalary != null || maxSalary != null) count++
            count += companySizes.size
            if (postedWithin != null) count++
            return count
        }
}

/**
 * Posted within filter option.
 */
enum class PostedWithin {
    LAST_24_HOURS,
    LAST_WEEK,
    LAST_MONTH,
    LAST_3_MONTHS;

    val displayName: String
        get() = when (this) {
            LAST_24_HOURS -> "Last 24 hours"
            LAST_WEEK -> "Last week"
            LAST_MONTH -> "Last month"
            LAST_3_MONTHS -> "Last 3 months"
        }

    val days: Int
        get() = when (this) {
            LAST_24_HOURS -> 1
            LAST_WEEK -> 7
            LAST_MONTH -> 30
            LAST_3_MONTHS -> 90
        }
}

/**
 * Recommended job with match information.
 */
data class RecommendedJob(
    val job: Job,
    val matchScore: Float,
    val matchReasons: List<MatchReason>,
    val skillMatches: List<String>,
    val skillGaps: List<String>
) {
    val matchPercentage: Int
        get() = (matchScore * 100).toInt()

    val isStrongMatch: Boolean
        get() = matchScore >= 0.75f

    val isGoodMatch: Boolean
        get() = matchScore >= 0.5f
}

/**
 * Saved job with save date.
 */
data class SavedJob(
    val job: Job,
    val savedAt: LocalDateTime,
    val notes: String?
)

/**
 * Job application request.
 */
data class JobApplicationRequest(
    val jobId: String,
    val coverLetter: String?,
    val resumeUrl: String?,
    val portfolioUrl: String?,
    val linkedInUrl: String?,
    val additionalNotes: String?
)

/**
 * Job statistics for the user.
 */
data class JobStats(
    val totalApplications: Int,
    val pendingApplications: Int,
    val interviewsScheduled: Int,
    val offersReceived: Int,
    val savedJobs: Int,
    val recommendedJobsCount: Int
)
