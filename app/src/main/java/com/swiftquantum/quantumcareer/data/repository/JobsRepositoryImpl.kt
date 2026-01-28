package com.swiftquantum.quantumcareer.data.repository

import com.swiftquantum.quantumcareer.data.api.JobsApi
import com.swiftquantum.quantumcareer.data.dto.*
import com.swiftquantum.quantumcareer.domain.model.*
import com.swiftquantum.quantumcareer.domain.repository.JobsRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobsRepositoryImpl @Inject constructor(
    private val jobsApi: JobsApi
) : JobsRepository {

    private val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    override suspend fun getJobs(
        filter: JobFilter,
        page: Int,
        pageSize: Int
    ): Result<Pair<List<Job>, Boolean>> {
        return try {
            val response = jobsApi.getJobs(
                query = filter.searchQuery.takeIf { it.isNotBlank() },
                locationTypes = filter.locationTypes.takeIf { it.isNotEmpty() }
                    ?.joinToString(",") { it.name },
                employmentTypes = filter.employmentTypes.takeIf { it.isNotEmpty() }
                    ?.joinToString(",") { it.name },
                experienceLevels = filter.experienceLevels.takeIf { it.isNotEmpty() }
                    ?.joinToString(",") { it.name },
                skills = filter.skills.takeIf { it.isNotEmpty() }?.joinToString(","),
                minSalary = filter.minSalary,
                maxSalary = filter.maxSalary,
                companySizes = filter.companySizes.takeIf { it.isNotEmpty() }
                    ?.joinToString(",") { it.name },
                postedWithinDays = filter.postedWithin?.days,
                page = page,
                pageSize = pageSize
            )

            if (response.isSuccessful) {
                val body = response.body()!!
                val jobs = body.jobs.map { it.toDomain() }
                Result.success(Pair(jobs, body.hasMore))
            } else {
                Result.failure(Exception("Failed to fetch jobs: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getJobById(jobId: String): Result<Job> {
        return try {
            val response = jobsApi.getJobById(jobId)
            if (response.isSuccessful) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Failed to fetch job details: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecommendedJobs(page: Int, pageSize: Int): Result<List<RecommendedJob>> {
        return try {
            val response = jobsApi.getRecommendedJobs(page, pageSize)
            if (response.isSuccessful) {
                val body = response.body()!!
                val recommendations = body.recommendations.map { it.toDomain() }
                Result.success(recommendations)
            } else {
                Result.failure(Exception("Failed to fetch recommended jobs: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSavedJobs(page: Int, pageSize: Int): Result<List<SavedJob>> {
        return try {
            val response = jobsApi.getSavedJobs(page, pageSize)
            if (response.isSuccessful) {
                val body = response.body()!!
                val savedJobs = body.jobs.map { it.toDomain() }
                Result.success(savedJobs)
            } else {
                Result.failure(Exception("Failed to fetch saved jobs: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveJob(jobId: String, notes: String?): Result<Unit> {
        return try {
            val request = SaveJobRequestDto(jobId = jobId, notes = notes)
            val response = jobsApi.saveJob(jobId, request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to save job"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unsaveJob(jobId: String): Result<Unit> {
        return try {
            val response = jobsApi.unsaveJob(jobId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to unsave job"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyToJob(request: JobApplicationRequest): Result<JobApplication> {
        return try {
            val requestDto = JobApplicationRequestDto(
                jobId = request.jobId,
                coverLetter = request.coverLetter,
                resumeUrl = request.resumeUrl,
                portfolioUrl = request.portfolioUrl,
                linkedInUrl = request.linkedInUrl,
                additionalNotes = request.additionalNotes
            )
            val response = jobsApi.applyToJob(request.jobId, requestDto)
            if (response.isSuccessful && response.body()?.success == true) {
                val application = response.body()!!.application!!.toDomain()
                Result.success(application)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to apply to job"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getApplications(
        status: ApplicationStatus?,
        page: Int,
        pageSize: Int
    ): Result<List<JobApplication>> {
        return try {
            val response = jobsApi.getApplications(
                status = status?.name,
                page = page,
                pageSize = pageSize
            )
            if (response.isSuccessful) {
                val body = response.body()!!
                val applications = body.applications.map { it.toDomain() }
                Result.success(applications)
            } else {
                Result.failure(Exception("Failed to fetch applications: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getApplicationById(applicationId: String): Result<JobApplication> {
        return try {
            val response = jobsApi.getApplicationById(applicationId)
            if (response.isSuccessful) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Failed to fetch application: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun withdrawApplication(applicationId: String): Result<Unit> {
        return try {
            val response = jobsApi.withdrawApplication(applicationId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to withdraw application"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getJobStats(): Result<JobStats> {
        return try {
            val response = jobsApi.getJobStats()
            if (response.isSuccessful) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Failed to fetch job stats: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getJobMatchAnalysis(jobId: String): Result<RecommendedJob> {
        return try {
            val response = jobsApi.getJobMatchAnalysis(jobId)
            if (response.isSuccessful) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Failed to fetch match analysis: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Extension functions for DTO to Domain mapping

    private fun JobDto.toDomain(): Job {
        return Job(
            id = id,
            title = title,
            company = company.toDomain(),
            location = location,
            locationType = LocationType.valueOf(locationType.uppercase().replace("-", "_")),
            employmentType = EmploymentType.fromString(employmentType),
            experienceLevel = ExperienceLevel.fromString(experienceLevel),
            description = description,
            requirements = requirements,
            responsibilities = responsibilities,
            requiredSkills = requiredSkills,
            preferredSkills = preferredSkills,
            salaryRange = salaryRange?.toDomain(),
            benefits = benefits,
            postedAt = parseDateTime(postedAt),
            applicationDeadline = applicationDeadline?.let { parseDateTime(it) },
            isRemote = isRemote,
            isSaved = isSaved,
            hasApplied = hasApplied,
            matchScore = matchScore
        )
    }

    private fun CompanyDto.toDomain(): Company {
        return Company(
            id = id,
            name = name,
            logoUrl = logoUrl,
            website = website,
            size = size?.let { CompanySize.valueOf(it.uppercase()) },
            industry = industry,
            description = description
        )
    }

    private fun SalaryRangeDto.toDomain(): SalaryRange {
        return SalaryRange(
            min = min,
            max = max,
            currency = currency,
            period = SalaryPeriod.valueOf(period.uppercase())
        )
    }

    private fun RecommendedJobDto.toDomain(): RecommendedJob {
        return RecommendedJob(
            job = job.toDomain(),
            matchScore = matchScore,
            matchReasons = matchReasons.map { it.toDomain() },
            skillMatches = skillMatches,
            skillGaps = skillGaps
        )
    }

    private fun MatchReasonDto.toDomain(): MatchReason {
        return MatchReason(
            category = MatchCategory.fromString(category),
            description = description,
            weight = weight,
            isPositive = isPositive
        )
    }

    private fun SavedJobDto.toDomain(): SavedJob {
        return SavedJob(
            job = job.toDomain(),
            savedAt = parseDateTime(savedAt),
            notes = notes
        )
    }

    private fun JobApplicationDto.toDomain(): JobApplication {
        return JobApplication(
            id = id,
            job = job.toDomain(),
            status = ApplicationStatus.fromString(status),
            appliedAt = parseDateTime(appliedAt),
            lastUpdatedAt = parseDateTime(lastUpdatedAt),
            coverLetter = coverLetter,
            resumeUrl = resumeUrl,
            notes = notes,
            nextSteps = nextSteps,
            interviewDate = interviewDate?.let { parseDateTime(it) }
        )
    }

    private fun JobStatsDto.toDomain(): JobStats {
        return JobStats(
            totalApplications = totalApplications,
            pendingApplications = pendingApplications,
            interviewsScheduled = interviewsScheduled,
            offersReceived = offersReceived,
            savedJobs = savedJobs,
            recommendedJobsCount = recommendedJobsCount
        )
    }

    private fun parseDateTime(dateString: String): LocalDateTime {
        return try {
            LocalDateTime.parse(dateString, dateTimeFormatter)
        } catch (e: Exception) {
            LocalDateTime.now()
        }
    }
}
