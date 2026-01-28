package com.swiftquantum.quantumcareer.data.api

import com.swiftquantum.quantumcareer.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface CertificateApi {

    /**
     * Get all certificates for the current user.
     */
    @GET("career-passport/certificates")
    suspend fun getCertificates(): Response<CertificateListResponseDto>

    /**
     * Get details of a specific certificate.
     */
    @GET("career-passport/certificates/{id}")
    suspend fun getCertificateById(
        @Path("id") certificateId: String
    ): Response<CertificateDto>

    /**
     * Verify a certificate using its verification code.
     */
    @GET("career-passport/certificates/verify/{code}")
    suspend fun verifyCertificate(
        @Path("code") verificationCode: String
    ): Response<CertificateVerificationResponseDto>

    /**
     * Share a certificate.
     */
    @POST("career-passport/certificates/share")
    suspend fun shareCertificate(
        @Body request: ShareCertificateRequestDto
    ): Response<ShareCertificateResponseDto>

    /**
     * Get certificate renewal information.
     */
    @GET("career-passport/certificates/{id}/renewal")
    suspend fun getRenewalInfo(
        @Path("id") certificateId: String
    ): Response<CertificateRenewalInfoDto>

    /**
     * Download certificate as PDF (returns PDF URL).
     */
    @GET("career-passport/certificates/{id}/pdf")
    suspend fun getCertificatePdfUrl(
        @Path("id") certificateId: String
    ): Response<CertificatePdfResponseDto>
}
