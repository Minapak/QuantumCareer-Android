package com.swiftquantum.quantumcareer.data.api

import com.swiftquantum.quantumcareer.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface CareerPassportApi {

    // Circuit endpoints
    @GET("career-passport/circuits")
    suspend fun getCircuits(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("status") status: String? = null
    ): Response<CircuitListResponseDto>

    @GET("career-passport/circuits/{id}")
    suspend fun getCircuit(
        @Path("id") circuitId: String
    ): Response<PublishedCircuitDto>

    @POST("career-passport/circuits/publish")
    suspend fun publishCircuit(
        @Body request: PublishCircuitRequestDto
    ): Response<PublishedCircuitDto>

    @POST("career-passport/circuits/{id}/cite")
    suspend fun citeCircuit(
        @Path("id") circuitId: String,
        @Body request: CiteCircuitRequestDto
    ): Response<CiteCircuitResponseDto>

    @DELETE("career-passport/circuits/{id}")
    suspend fun deleteCircuit(
        @Path("id") circuitId: String
    ): Response<Unit>

    // Badge endpoints
    @GET("career-passport/badges")
    suspend fun getBadges(): Response<BadgeListResponseDto>

    // Citation endpoints
    @GET("career-passport/citations")
    suspend fun getCitationStats(): Response<CitationStatsDto>

    @GET("career-passport/citations/{circuitId}")
    suspend fun getCircuitCitations(
        @Path("circuitId") circuitId: String
    ): Response<List<CitationDetailDto>>

    // Profile endpoints
    @GET("career-passport/profile")
    suspend fun getProfile(): Response<PublicProfileDto>

    @PUT("career-passport/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequestDto
    ): Response<UpdateProfileResponseDto>

    @GET("career-passport/profile/public/{username}")
    suspend fun getPublicProfile(
        @Path("username") username: String
    ): Response<PublicProfileDto>

    // Dashboard
    @GET("career-passport/dashboard")
    suspend fun getDashboardStats(): Response<DashboardStatsDto>
}
