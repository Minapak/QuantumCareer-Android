package com.swiftquantum.quantumcareer.data.api

import com.swiftquantum.quantumcareer.data.dto.LoginRequest
import com.swiftquantum.quantumcareer.data.dto.LoginResponse
import com.swiftquantum.quantumcareer.data.dto.RegisterRequest
import com.swiftquantum.quantumcareer.data.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Auth API for SwiftQuantumBackend
 * Base URL: https://api.swiftquantum.tech/api/v1
 */
interface AuthApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): LoginResponse

    @POST("auth/logout")
    suspend fun logout()

    @POST("auth/refresh")
    suspend fun refreshToken(): LoginResponse

    @GET("users/me")
    suspend fun getCurrentUser(): UserDto
}
