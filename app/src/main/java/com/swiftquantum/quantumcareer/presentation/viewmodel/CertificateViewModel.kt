package com.swiftquantum.quantumcareer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftquantum.quantumcareer.domain.model.*
import com.swiftquantum.quantumcareer.domain.repository.CertificateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CertificateUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    // Certificates list state
    val certificateSummary: CertificateSummary? = null,

    // Selected certificate state
    val selectedCertificate: Certificate? = null,
    val renewalInfo: CertificateRenewalInfo? = null,

    // Verification state
    val verificationCode: String = "",
    val verificationResult: CertificateVerification? = null,
    val isVerifying: Boolean = false,

    // Share state
    val isSharing: Boolean = false,
    val shareResult: ShareCertificateResult? = null,

    // PDF download state
    val pdfUrl: String? = null,
    val isLoadingPdf: Boolean = false
)

@HiltViewModel
class CertificateViewModel @Inject constructor(
    private val certificateRepository: CertificateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CertificateUiState())
    val uiState: StateFlow<CertificateUiState> = _uiState.asStateFlow()

    init {
        loadCertificates()
    }

    fun loadCertificates() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            certificateRepository.getCertificates()
                .onSuccess { summary ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        certificateSummary = summary
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load certificates"
                    )
                }
        }
    }

    fun selectCertificate(certificateId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            certificateRepository.getCertificateById(certificateId)
                .onSuccess { certificate ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        selectedCertificate = certificate
                    )
                    // Also load renewal info
                    loadRenewalInfo(certificateId)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load certificate"
                    )
                }
        }
    }

    private fun loadRenewalInfo(certificateId: String) {
        viewModelScope.launch {
            certificateRepository.getRenewalInfo(certificateId)
                .onSuccess { renewalInfo ->
                    _uiState.value = _uiState.value.copy(renewalInfo = renewalInfo)
                }
        }
    }

    fun clearSelectedCertificate() {
        _uiState.value = _uiState.value.copy(
            selectedCertificate = null,
            renewalInfo = null,
            pdfUrl = null
        )
    }

    fun updateVerificationCode(code: String) {
        _uiState.value = _uiState.value.copy(
            verificationCode = code,
            verificationResult = null
        )
    }

    fun verifyCertificate() {
        val code = _uiState.value.verificationCode.replace("-", "").trim()
        if (code.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isVerifying = true, verificationResult = null)

            certificateRepository.verifyCertificate(code)
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        isVerifying = false,
                        verificationResult = result
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isVerifying = false,
                        verificationResult = CertificateVerification(
                            code = code,
                            isValid = false,
                            certificate = null,
                            errorMessage = error.message ?: "Verification failed"
                        )
                    )
                }
        }
    }

    fun clearVerification() {
        _uiState.value = _uiState.value.copy(
            verificationCode = "",
            verificationResult = null
        )
    }

    fun shareCertificate(shareType: CertificateShareType, customMessage: String? = null) {
        val certificate = _uiState.value.selectedCertificate ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSharing = true, shareResult = null)

            val request = ShareCertificateRequest(
                certificateId = certificate.id,
                shareType = shareType,
                customMessage = customMessage
            )

            certificateRepository.shareCertificate(request)
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        isSharing = false,
                        shareResult = result
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isSharing = false,
                        error = error.message ?: "Failed to share certificate"
                    )
                }
        }
    }

    fun clearShareResult() {
        _uiState.value = _uiState.value.copy(shareResult = null)
    }

    fun loadCertificatePdf() {
        val certificate = _uiState.value.selectedCertificate ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingPdf = true)

            certificateRepository.getCertificatePdfUrl(certificate.id)
                .onSuccess { url ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingPdf = false,
                        pdfUrl = url
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingPdf = false,
                        error = error.message ?: "Failed to get PDF"
                    )
                }
        }
    }

    fun clearPdfUrl() {
        _uiState.value = _uiState.value.copy(pdfUrl = null)
    }

    fun refresh() {
        loadCertificates()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
