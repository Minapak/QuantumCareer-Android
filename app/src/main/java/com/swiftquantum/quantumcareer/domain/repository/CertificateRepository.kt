package com.swiftquantum.quantumcareer.domain.repository

import com.swiftquantum.quantumcareer.domain.model.*

interface CertificateRepository {

    /**
     * Get all certificates for the current user.
     */
    suspend fun getCertificates(): Result<CertificateSummary>

    /**
     * Get a specific certificate by ID.
     */
    suspend fun getCertificateById(certificateId: String): Result<Certificate>

    /**
     * Verify a certificate using its verification code.
     */
    suspend fun verifyCertificate(verificationCode: String): Result<CertificateVerification>

    /**
     * Share a certificate.
     */
    suspend fun shareCertificate(request: ShareCertificateRequest): Result<ShareCertificateResult>

    /**
     * Get renewal information for a certificate.
     */
    suspend fun getRenewalInfo(certificateId: String): Result<CertificateRenewalInfo>

    /**
     * Get the PDF download URL for a certificate.
     */
    suspend fun getCertificatePdfUrl(certificateId: String): Result<String>
}
