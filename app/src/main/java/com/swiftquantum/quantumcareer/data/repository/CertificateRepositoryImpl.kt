package com.swiftquantum.quantumcareer.data.repository

import com.swiftquantum.quantumcareer.data.api.CertificateApi
import com.swiftquantum.quantumcareer.data.dto.ShareCertificateRequestDto
import com.swiftquantum.quantumcareer.data.mapper.toDomain
import com.swiftquantum.quantumcareer.domain.model.*
import com.swiftquantum.quantumcareer.domain.repository.CertificateRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CertificateRepositoryImpl @Inject constructor(
    private val api: CertificateApi
) : CertificateRepository {

    override suspend fun getCertificates(): Result<CertificateSummary> {
        return try {
            val response = api.getCertificates()
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.toDomain())
                } ?: Result.failure(Exception("Failed to get certificates"))
            } else {
                Result.failure(Exception("Failed to get certificates: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCertificateById(certificateId: String): Result<Certificate> {
        return try {
            val response = api.getCertificateById(certificateId)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.toDomain())
                } ?: Result.failure(Exception("Certificate not found"))
            } else {
                Result.failure(Exception("Failed to get certificate: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyCertificate(verificationCode: String): Result<CertificateVerification> {
        return try {
            val response = api.verifyCertificate(verificationCode)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.toDomain())
                } ?: Result.failure(Exception("Failed to verify certificate"))
            } else {
                Result.failure(Exception("Failed to verify certificate: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun shareCertificate(request: ShareCertificateRequest): Result<ShareCertificateResult> {
        return try {
            val dto = ShareCertificateRequestDto(
                certificateId = request.certificateId,
                shareType = request.shareType.name.lowercase(),
                customMessage = request.customMessage
            )
            val response = api.shareCertificate(dto)
            if (response.isSuccessful) {
                response.body()?.let { responseDto ->
                    Result.success(
                        ShareCertificateResult(
                            success = responseDto.success,
                            shareUrl = responseDto.shareUrl,
                            message = responseDto.message
                        )
                    )
                } ?: Result.failure(Exception("Failed to share certificate"))
            } else {
                Result.failure(Exception("Failed to share certificate: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRenewalInfo(certificateId: String): Result<CertificateRenewalInfo> {
        return try {
            val response = api.getRenewalInfo(certificateId)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.toDomain())
                } ?: Result.failure(Exception("Failed to get renewal info"))
            } else {
                Result.failure(Exception("Failed to get renewal info: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCertificatePdfUrl(certificateId: String): Result<String> {
        return try {
            val response = api.getCertificatePdfUrl(certificateId)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.pdfUrl)
                } ?: Result.failure(Exception("Failed to get PDF URL"))
            } else {
                Result.failure(Exception("Failed to get PDF URL: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
