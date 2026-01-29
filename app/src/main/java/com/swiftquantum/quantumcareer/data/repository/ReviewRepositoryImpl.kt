package com.swiftquantum.quantumcareer.data.repository

import com.swiftquantum.quantumcareer.data.api.PeerReviewApi
import com.swiftquantum.quantumcareer.data.dto.SubmitReviewRequestDto
import com.swiftquantum.quantumcareer.data.mapper.toDomain
import com.swiftquantum.quantumcareer.domain.model.*
import com.swiftquantum.quantumcareer.domain.repository.ReviewRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepositoryImpl @Inject constructor(
    private val api: PeerReviewApi
) : ReviewRepository {

    override suspend fun getPendingReviews(page: Int, perPage: Int): Result<List<PeerReview>> {
        return try {
            val response = api.getPendingReviews(page, perPage)
            if (response.isSuccessful) {
                Result.success(response.body()?.reviews?.map { it.toDomain() } ?: emptyList())
            } else {
                // Return empty list for guest/offline mode
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            // Return empty list on API failure
            Result.success(emptyList())
        }
    }

    override suspend fun getMyReviews(page: Int, perPage: Int): Result<List<PeerReview>> {
        return try {
            val response = api.getMyReviews(page, perPage)
            if (response.isSuccessful) {
                Result.success(response.body()?.reviews?.map { it.toDomain() } ?: emptyList())
            } else {
                // Return empty list for guest/offline mode
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            // Return empty list on API failure
            Result.success(emptyList())
        }
    }

    override suspend fun getReview(reviewId: String): Result<PeerReview> {
        return try {
            val response = api.getReview(reviewId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomain())
                } ?: Result.failure(Exception("Review not found"))
            } else {
                Result.failure(Exception("Failed to get review: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitReview(request: SubmitReviewRequest): Result<Boolean> {
        return try {
            val dto = SubmitReviewRequestDto(
                reviewId = request.reviewId,
                decision = request.decision.name.lowercase(),
                comment = request.comment,
                technicalScore = request.technicalScore,
                innovationScore = request.innovationScore,
                documentationScore = request.documentationScore
            )
            val response = api.submitReview(dto)
            if (response.isSuccessful) {
                Result.success(response.body()?.success ?: false)
            } else {
                Result.failure(Exception("Failed to submit review: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReviewerStats(): Result<ReviewerStats> {
        return try {
            val response = api.getReviewerStats()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomain())
                } ?: Result.success(getDefaultReviewerStats())
            } else {
                // Return default stats for guest/offline mode
                Result.success(getDefaultReviewerStats())
            }
        } catch (e: Exception) {
            // Return default stats on API failure
            Result.success(getDefaultReviewerStats())
        }
    }

    private fun getDefaultReviewerStats(): ReviewerStats {
        return ReviewerStats(
            totalReviews = 0,
            approvedCount = 0,
            rejectedCount = 0,
            reviewerLevel = ReviewerLevel.JUNIOR,
            reviewsUntilNextLevel = ReviewerLevel.SENIOR.requiredReviews
        )
    }

    override suspend fun claimReview(reviewId: String): Result<PeerReview> {
        return try {
            val response = api.claimReview(reviewId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomain())
                } ?: Result.failure(Exception("Failed to claim review"))
            } else {
                Result.failure(Exception("Failed to claim review: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun releaseReview(reviewId: String): Result<Boolean> {
        return try {
            val response = api.releaseReview(reviewId)
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            // Return false on API failure - non-critical operation
            Result.success(false)
        }
    }
}
