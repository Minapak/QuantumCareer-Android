package com.swiftquantum.quantumcareer.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftquantum.quantumcareer.domain.model.*
import com.swiftquantum.quantumcareer.domain.repository.JobsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JobDetailUiState(
    val isLoading: Boolean = false,
    val job: Job? = null,
    val matchAnalysis: RecommendedJob? = null,
    val error: String? = null,
    val showApplicationSheet: Boolean = false,
    val applicationSubmitting: Boolean = false,
    val applicationSuccess: Boolean = false
)

@HiltViewModel
class JobDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val jobsRepository: JobsRepository
) : ViewModel() {

    private val jobId: String = savedStateHandle.get<String>("jobId") ?: ""

    private val _uiState = MutableStateFlow(JobDetailUiState())
    val uiState: StateFlow<JobDetailUiState> = _uiState.asStateFlow()

    init {
        if (jobId.isNotBlank()) {
            loadJobDetail()
        }
    }

    fun loadJobDetail() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            jobsRepository.getJobById(jobId)
                .onSuccess { job ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        job = job
                    )
                    loadMatchAnalysis()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load job details"
                    )
                }
        }
    }

    private fun loadMatchAnalysis() {
        viewModelScope.launch {
            jobsRepository.getJobMatchAnalysis(jobId)
                .onSuccess { analysis ->
                    _uiState.value = _uiState.value.copy(matchAnalysis = analysis)
                }
        }
    }

    fun toggleSaveJob() {
        val job = _uiState.value.job ?: return

        viewModelScope.launch {
            if (job.isSaved) {
                jobsRepository.unsaveJob(job.id)
                    .onSuccess {
                        _uiState.value = _uiState.value.copy(
                            job = job.copy(isSaved = false)
                        )
                    }
                    .onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            error = error.message ?: "Failed to remove saved job"
                        )
                    }
            } else {
                jobsRepository.saveJob(job.id)
                    .onSuccess {
                        _uiState.value = _uiState.value.copy(
                            job = job.copy(isSaved = true)
                        )
                    }
                    .onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            error = error.message ?: "Failed to save job"
                        )
                    }
            }
        }
    }

    fun showApplicationSheet() {
        _uiState.value = _uiState.value.copy(showApplicationSheet = true)
    }

    fun hideApplicationSheet() {
        _uiState.value = _uiState.value.copy(showApplicationSheet = false)
    }

    fun applyToJob(coverLetter: String?, resumeUrl: String?) {
        val job = _uiState.value.job ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(applicationSubmitting = true)

            val request = JobApplicationRequest(
                jobId = job.id,
                coverLetter = coverLetter,
                resumeUrl = resumeUrl,
                portfolioUrl = null,
                linkedInUrl = null,
                additionalNotes = null
            )

            jobsRepository.applyToJob(request)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        applicationSubmitting = false,
                        applicationSuccess = true,
                        showApplicationSheet = false,
                        job = job.copy(hasApplied = true)
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        applicationSubmitting = false,
                        error = error.message ?: "Failed to submit application"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearApplicationSuccess() {
        _uiState.value = _uiState.value.copy(applicationSuccess = false)
    }

    fun refresh() {
        loadJobDetail()
    }
}
