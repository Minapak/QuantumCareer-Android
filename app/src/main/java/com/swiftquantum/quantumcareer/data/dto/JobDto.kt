package com.swiftquantum.quantumcareer.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JobDto(
    val id: String,
    val title: String,
    val company: CompanyDto,
    val location: String,
    @SerialName("location_type") val locationType: String,
    @SerialName("employment_type") val employmentType: String,
    @SerialName("experience_level") val experienceLevel: String,
    val description: String,
    val requirements: List<String> = emptyList(),
    val responsibilities: List<String> = emptyList(),
    @SerialName("required_skills") val requiredSkills: List<String> = emptyList(),
    @SerialName("preferred_skills") val preferredSkills: List<String> = emptyList(),
    @SerialName("salary_range") val salaryRange: SalaryRangeDto? = null,
    val benefits: List<String> = emptyList(),
    @SerialName("posted_at") val postedAt: String,
    @SerialName("application_deadline") val applicationDeadline: String? = null,
    @SerialName("is_remote") val isRemote: Boolean = false,
    @SerialName("is_saved") val isSaved: Boolean = false,
    @SerialName("has_applied") val hasApplied: Boolean = false,
    @SerialName("match_score") val matchScore: Float? = null
)

@Serializable
data class CompanyDto(
    val id: String,
    val name: String,
    @SerialName("logo_url") val logoUrl: String? = null,
    val website: String? = null,
    val size: String? = null,
    val industry: String? = null,
    val description: String? = null
)

@Serializable
data class SalaryRangeDto(
    val min: Int,
    val max: Int,
    val currency: String = "USD",
    val period: String = "YEARLY"
)

@Serializable
data class JobListResponseDto(
    val jobs: List<JobDto>,
    @SerialName("total_count") val totalCount: Int,
    val page: Int,
    @SerialName("page_size") val pageSize: Int,
    @SerialName("has_more") val hasMore: Boolean
)

@Serializable
data class RecommendedJobDto(
    val job: JobDto,
    @SerialName("match_score") val matchScore: Float,
    @SerialName("match_reasons") val matchReasons: List<MatchReasonDto> = emptyList(),
    @SerialName("skill_matches") val skillMatches: List<String> = emptyList(),
    @SerialName("skill_gaps") val skillGaps: List<String> = emptyList()
)

@Serializable
data class MatchReasonDto(
    val category: String,
    val description: String,
    val weight: Float,
    @SerialName("is_positive") val isPositive: Boolean = true
)

@Serializable
data class RecommendedJobsResponseDto(
    val recommendations: List<RecommendedJobDto>,
    @SerialName("total_count") val totalCount: Int,
    @SerialName("last_updated") val lastUpdated: String
)

@Serializable
data class SavedJobDto(
    val job: JobDto,
    @SerialName("saved_at") val savedAt: String,
    val notes: String? = null
)

@Serializable
data class SavedJobsResponseDto(
    val jobs: List<SavedJobDto>,
    @SerialName("total_count") val totalCount: Int
)

@Serializable
data class JobApplicationDto(
    val id: String,
    val job: JobDto,
    val status: String,
    @SerialName("applied_at") val appliedAt: String,
    @SerialName("last_updated_at") val lastUpdatedAt: String,
    @SerialName("cover_letter") val coverLetter: String? = null,
    @SerialName("resume_url") val resumeUrl: String? = null,
    val notes: String? = null,
    @SerialName("next_steps") val nextSteps: String? = null,
    @SerialName("interview_date") val interviewDate: String? = null
)

@Serializable
data class JobApplicationsResponseDto(
    val applications: List<JobApplicationDto>,
    @SerialName("total_count") val totalCount: Int
)

@Serializable
data class JobApplicationRequestDto(
    @SerialName("job_id") val jobId: String,
    @SerialName("cover_letter") val coverLetter: String? = null,
    @SerialName("resume_url") val resumeUrl: String? = null,
    @SerialName("portfolio_url") val portfolioUrl: String? = null,
    @SerialName("linkedin_url") val linkedInUrl: String? = null,
    @SerialName("additional_notes") val additionalNotes: String? = null
)

@Serializable
data class JobApplicationResponseDto(
    val success: Boolean,
    val application: JobApplicationDto? = null,
    val message: String? = null
)

@Serializable
data class SaveJobRequestDto(
    @SerialName("job_id") val jobId: String,
    val notes: String? = null
)

@Serializable
data class SaveJobResponseDto(
    val success: Boolean,
    val message: String? = null
)

@Serializable
data class UnsaveJobResponseDto(
    val success: Boolean,
    val message: String? = null
)

@Serializable
data class JobStatsDto(
    @SerialName("total_applications") val totalApplications: Int,
    @SerialName("pending_applications") val pendingApplications: Int,
    @SerialName("interviews_scheduled") val interviewsScheduled: Int,
    @SerialName("offers_received") val offersReceived: Int,
    @SerialName("saved_jobs") val savedJobs: Int,
    @SerialName("recommended_jobs_count") val recommendedJobsCount: Int
)

@Serializable
data class JobFilterRequestDto(
    val query: String? = null,
    @SerialName("location_types") val locationTypes: List<String>? = null,
    @SerialName("employment_types") val employmentTypes: List<String>? = null,
    @SerialName("experience_levels") val experienceLevels: List<String>? = null,
    val skills: List<String>? = null,
    @SerialName("min_salary") val minSalary: Int? = null,
    @SerialName("max_salary") val maxSalary: Int? = null,
    @SerialName("company_sizes") val companySizes: List<String>? = null,
    @SerialName("posted_within_days") val postedWithinDays: Int? = null,
    val page: Int = 1,
    @SerialName("page_size") val pageSize: Int = 20
)
