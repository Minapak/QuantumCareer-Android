package com.swiftquantum.quantumcareer.domain.repository

import com.swiftquantum.quantumcareer.domain.model.*

interface ReviewRepository {
    suspend fun getPendingReviews(page: Int = 1, perPage: Int = 20): Result<List<PeerReview>>
    suspend fun getMyReviews(page: Int = 1, perPage: Int = 20): Result<List<PeerReview>>
    suspend fun getReview(reviewId: String): Result<PeerReview>
    suspend fun submitReview(request: SubmitReviewRequest): Result<Boolean>
    suspend fun getReviewerStats(): Result<ReviewerStats>
    suspend fun claimReview(reviewId: String): Result<PeerReview>
    suspend fun releaseReview(reviewId: String): Result<Boolean>
}
