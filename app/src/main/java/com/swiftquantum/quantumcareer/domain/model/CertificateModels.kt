package com.swiftquantum.quantumcareer.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Represents a quantum computing certification earned through the test system.
 */
data class Certificate(
    val id: String,
    val userId: String,
    val userName: String,
    val tier: BadgeTier,
    val score: Int,
    val maxScore: Int,
    val issueDate: LocalDate,
    val expiryDate: LocalDate,
    val verificationCode: String,
    val doiUrl: String,
    val testSessionId: String
) {
    val scorePercentage: Float
        get() = if (maxScore > 0) score.toFloat() / maxScore * 100 else 0f

    val isExpired: Boolean
        get() = LocalDate.now().isAfter(expiryDate)

    val isValid: Boolean
        get() = !isExpired

    val formattedVerificationCode: String
        get() = verificationCode.chunked(4).joinToString("-")

    val certificateTitle: String
        get() = "Quantum Computing ${tier.displayName} Certificate"

    val doiDisplay: String
        get() = doiUrl.removePrefix("https://doi.org/")

    companion object {
        // Certificate validity period in years
        const val VALIDITY_YEARS = 2

        // Badge tier thresholds (percentage)
        fun getTierForPercentage(percentage: Float): BadgeTier? {
            return when {
                percentage >= 95 -> BadgeTier.PLATINUM
                percentage >= 85 -> BadgeTier.GOLD
                percentage >= 75 -> BadgeTier.SILVER
                percentage >= 60 -> BadgeTier.BRONZE
                else -> null
            }
        }

        // Minimum percentage to pass and earn a certificate
        const val MINIMUM_PASSING_PERCENTAGE = 60f
    }
}

/**
 * Result of certificate verification.
 */
data class CertificateVerification(
    val code: String,
    val isValid: Boolean,
    val certificate: Certificate?,
    val errorMessage: String?
) {
    val statusMessage: String
        get() = when {
            !isValid && errorMessage != null -> errorMessage
            !isValid -> "Certificate not found or invalid"
            certificate?.isExpired == true -> "Certificate has expired"
            else -> "Certificate is valid"
        }
}

/**
 * Share options for a certificate.
 */
enum class CertificateShareType {
    LINKEDIN,
    TWITTER,
    EMAIL,
    DOWNLOAD_PDF,
    COPY_LINK;

    val displayName: String
        get() = when (this) {
            LINKEDIN -> "Share on LinkedIn"
            TWITTER -> "Share on Twitter"
            EMAIL -> "Share via Email"
            DOWNLOAD_PDF -> "Download PDF"
            COPY_LINK -> "Copy Verification Link"
        }
}

/**
 * Request to share a certificate.
 */
data class ShareCertificateRequest(
    val certificateId: String,
    val shareType: CertificateShareType,
    val customMessage: String? = null
)

/**
 * Response from sharing a certificate.
 */
data class ShareCertificateResult(
    val success: Boolean,
    val shareUrl: String?,
    val message: String?
)

/**
 * Summary of user's certificates.
 */
data class CertificateSummary(
    val certificates: List<Certificate>,
    val totalCertificates: Int,
    val activeCertificates: Int,
    val expiredCertificates: Int,
    val highestTier: BadgeTier?
) {
    val hasValidCertificate: Boolean
        get() = activeCertificates > 0

    val latestCertificate: Certificate?
        get() = certificates.maxByOrNull { it.issueDate }
}

/**
 * Certificate renewal information.
 */
data class CertificateRenewalInfo(
    val certificate: Certificate,
    val canRenew: Boolean,
    val daysUntilExpiry: Int,
    val renewalTestSessionId: String?
) {
    val needsRenewal: Boolean
        get() = daysUntilExpiry <= 30

    val isUrgent: Boolean
        get() = daysUntilExpiry <= 7

    val statusText: String
        get() = when {
            certificate.isExpired -> "Expired - Retake test to renew"
            isUrgent -> "Expires in $daysUntilExpiry days - Renew now!"
            needsRenewal -> "Expires in $daysUntilExpiry days - Consider renewing"
            else -> "Valid for $daysUntilExpiry more days"
        }
}
