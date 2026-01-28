package com.swiftquantum.quantumcareer.data.api

import com.swiftquantum.quantumcareer.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface PeerReviewApi {

    @GET("career-passport/reviews/pending")
    suspend fun getPendingReviews(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<PendingReviewsResponseDto>

    @GET("career-passport/reviews/my-reviews")
    suspend fun getMyReviews(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<PendingReviewsResponseDto>

    @GET("career-passport/reviews/{id}")
    suspend fun getReview(
        @Path("id") reviewId: String
    ): Response<PeerReviewDto>

    @POST("career-passport/reviews/submit")
    suspend fun submitReview(
        @Body request: SubmitReviewRequestDto
    ): Response<SubmitReviewResponseDto>

    @GET("career-passport/reviews/stats")
    suspend fun getReviewerStats(): Response<ReviewerStatsDto>

    @POST("career-passport/reviews/{id}/claim")
    suspend fun claimReview(
        @Path("id") reviewId: String
    ): Response<PeerReviewDto>

    @POST("career-passport/reviews/{id}/release")
    suspend fun releaseReview(
        @Path("id") reviewId: String
    ): Response<Unit>
}
