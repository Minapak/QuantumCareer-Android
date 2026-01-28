package com.swiftquantum.quantumcareer.data.api

import com.swiftquantum.quantumcareer.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface TalentApi {

    @POST("career-passport/talent/search")
    suspend fun searchTalent(
        @Body request: TalentSearchRequestDto
    ): Response<TalentSearchResponseDto>

    @GET("career-passport/talent/profile/{userId}")
    suspend fun getTalentProfile(
        @Path("userId") userId: String
    ): Response<TalentProfileDto>

    @POST("career-passport/talent/scout")
    suspend fun scoutTalent(
        @Body request: ScoutRequestDto
    ): Response<ScoutResponseDto>

    @GET("career-passport/talent/offers")
    suspend fun getOffers(
        @Query("type") type: String? = null,
        @Query("status") status: String? = null
    ): Response<TalentOffersResponseDto>

    @GET("career-passport/talent/offers/sent")
    suspend fun getSentOffers(): Response<TalentOffersResponseDto>

    @POST("career-passport/talent/offers/{id}/respond")
    suspend fun respondToOffer(
        @Path("id") offerId: String,
        @Body request: RespondToOfferRequestDto
    ): Response<ScoutResponseDto>

    @DELETE("career-passport/talent/offers/{id}")
    suspend fun withdrawOffer(
        @Path("id") offerId: String
    ): Response<Unit>
}
