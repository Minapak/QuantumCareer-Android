package com.swiftquantum.quantumcareer.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Auth DTOs for SwiftQuantumBackend
 * v5.4.2: 구독 티어 필드 포함
 * QuantumCareer 티어: Free, Pro ($14.99)
 */

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String
)

@Serializable
data class LoginResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("token_type") val tokenType: String? = null,
    val user: UserDto? = null
)

@Serializable
data class UserDto(
    val id: Int,
    val email: String,
    val username: String,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("is_admin") val isAdmin: Boolean? = null,
    @SerialName("is_active") val isActive: Boolean? = null,
    // Backend subscription fields
    val tier: String? = null,
    @SerialName("is_premium") val isPremium: Boolean? = null,
    @SerialName("subscription_type") val subscriptionType: String? = null,
    @SerialName("subscription_tier") val subscriptionTier: String? = null,
    @SerialName("created_at") val createdAt: String? = null
) {
    /**
     * Pro 구독자 여부 (Free, Pro 티어 체크)
     */
    val isPro: Boolean
        get() {
            println("🔐 QuantumCareer Android UserDto.isPro check")
            println("   - isPremium: $isPremium")
            println("   - subscriptionType: $subscriptionType")
            println("   - subscriptionTier: $subscriptionTier")
            println("   - tier: $tier")

            val proTiers = listOf("pro", "master", "career", "scholar", "professional", "enterprise")

            // isPremium 직접 체크
            if (isPremium == true) {
                println("🔐 isPro: true (isPremium)")
                return true
            }

            // subscription_type 체크
            if (subscriptionType?.lowercase() in proTiers) {
                println("🔐 isPro: true (subscriptionType: $subscriptionType)")
                return true
            }

            // subscription_tier 체크
            if (subscriptionTier?.lowercase() in proTiers) {
                println("🔐 isPro: true (subscriptionTier: $subscriptionTier)")
                return true
            }

            // tier 체크
            if (tier?.lowercase() in proTiers) {
                println("🔐 isPro: true (tier: $tier)")
                return true
            }

            println("🔐 isPro: false")
            return false
        }
}
