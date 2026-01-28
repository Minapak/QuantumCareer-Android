package com.swiftquantum.quantumcareer.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ============== Response DTOs ==============

@Serializable
data class CertificateDto(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("user_name") val userName: String,
    @SerialName("tier") val tier: String,
    @SerialName("score") val score: Int,
    @SerialName("max_score") val maxScore: Int,
    @SerialName("issue_date") val issueDate: String,
    @SerialName("expiry_date") val expiryDate: String,
    @SerialName("verification_code") val verificationCode: String,
    @SerialName("doi_url") val doiUrl: String,
    @SerialName("test_session_id") val testSessionId: String
)

@Serializable
data class CertificateListResponseDto(
    @SerialName("certificates") val certificates: List<CertificateDto>,
    @SerialName("total_certificates") val totalCertificates: Int,
    @SerialName("active_certificates") val activeCertificates: Int,
    @SerialName("expired_certificates") val expiredCertificates: Int,
    @SerialName("highest_tier") val highestTier: String?
)

@Serializable
data class CertificateVerificationResponseDto(
    @SerialName("code") val code: String,
    @SerialName("is_valid") val isValid: Boolean,
    @SerialName("certificate") val certificate: CertificateDto?,
    @SerialName("error_message") val errorMessage: String?
)

// ============== Request DTOs ==============

@Serializable
data class ShareCertificateRequestDto(
    @SerialName("certificate_id") val certificateId: String,
    @SerialName("share_type") val shareType: String,
    @SerialName("custom_message") val customMessage: String? = null
)

@Serializable
data class ShareCertificateResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("share_url") val shareUrl: String?,
    @SerialName("message") val message: String?
)

@Serializable
data class CertificateRenewalInfoDto(
    @SerialName("certificate") val certificate: CertificateDto,
    @SerialName("can_renew") val canRenew: Boolean,
    @SerialName("days_until_expiry") val daysUntilExpiry: Int,
    @SerialName("renewal_test_session_id") val renewalTestSessionId: String?
)

@Serializable
data class CertificatePdfResponseDto(
    @SerialName("pdf_url") val pdfUrl: String,
    @SerialName("expires_at") val expiresAt: String
)
