package com.swiftquantum.quantumcareer.presentation.viewmodel

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

data class JobsUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val jobs: List<Job> = emptyList(),
    val recommendedJobs: List<RecommendedJob> = emptyList(),
    val savedJobs: List<SavedJob> = emptyList(),
    val applications: List<JobApplication> = emptyList(),
    val jobStats: JobStats? = null,
    val filter: JobFilter = JobFilter(),
    val hasMoreJobs: Boolean = false,
    val currentPage: Int = 1,
    val selectedTab: JobsTab = JobsTab.BROWSE,
    val error: String? = null,
    val showFilterSheet: Boolean = false,
    val showApplicationSheet: Boolean = false,
    val selectedJobForApplication: Job? = null
)

enum class JobsTab {
    BROWSE,
    RECOMMENDED,
    SAVED,
    APPLICATIONS
}

@HiltViewModel
class JobsViewModel @Inject constructor(
    private val jobsRepository: JobsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JobsUiState())
    val uiState: StateFlow<JobsUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        loadJobs()
        loadJobStats()
    }

    fun selectTab(tab: JobsTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
        when (tab) {
            JobsTab.BROWSE -> if (_uiState.value.jobs.isEmpty()) loadJobs()
            JobsTab.RECOMMENDED -> if (_uiState.value.recommendedJobs.isEmpty()) loadRecommendedJobs()
            JobsTab.SAVED -> loadSavedJobs()
            JobsTab.APPLICATIONS -> loadApplications()
        }
    }

    fun loadJobs(refresh: Boolean = false) {
        if (refresh) {
            _uiState.value = _uiState.value.copy(currentPage = 1, jobs = emptyList())
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = _uiState.value.jobs.isEmpty(),
                error = null
            )

            jobsRepository.getJobs(
                filter = _uiState.value.filter,
                page = 1,
                pageSize = 20
            ).onSuccess { (jobs, hasMore) ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    jobs = jobs,
                    hasMoreJobs = hasMore,
                    currentPage = 1
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Failed to load jobs"
                )
            }
        }
    }

    fun loadMoreJobs() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMoreJobs) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingMore = true)

            val nextPage = _uiState.value.currentPage + 1
            jobsRepository.getJobs(
                filter = _uiState.value.filter,
                page = nextPage,
                pageSize = 20
            ).onSuccess { (newJobs, hasMore) ->
                _uiState.value = _uiState.value.copy(
                    isLoadingMore = false,
                    jobs = _uiState.value.jobs + newJobs,
                    hasMoreJobs = hasMore,
                    currentPage = nextPage
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoadingMore = false)
            }
        }
    }

    fun loadRecommendedJobs() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = _uiState.value.recommendedJobs.isEmpty(),
                error = null
            )

            jobsRepository.getRecommendedJobs()
                .onSuccess { recommendations ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        recommendedJobs = recommendations
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load recommendations"
                    )
                }
        }
    }

    fun loadSavedJobs() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            jobsRepository.getSavedJobs()
                .onSuccess { savedJobs ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        savedJobs = savedJobs
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load saved jobs"
                    )
                }
        }
    }

    fun loadApplications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            jobsRepository.getApplications()
                .onSuccess { applications ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        applications = applications
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load applications"
                    )
                }
        }
    }

    private fun loadJobStats() {
        viewModelScope.launch {
            jobsRepository.getJobStats()
                .onSuccess { stats ->
                    _uiState.value = _uiState.value.copy(jobStats = stats)
                }
        }
    }

    fun updateFilter(filter: JobFilter) {
        _uiState.value = _uiState.value.copy(filter = filter)
        loadJobs(refresh = true)
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(filter = JobFilter())
        loadJobs(refresh = true)
    }

    fun updateSearchQuery(query: String) {
        val currentFilter = _uiState.value.filter
        _uiState.value = _uiState.value.copy(
            filter = currentFilter.copy(searchQuery = query)
        )
    }

    fun searchJobs() {
        loadJobs(refresh = true)
    }

    fun saveJob(jobId: String) {
        viewModelScope.launch {
            jobsRepository.saveJob(jobId)
                .onSuccess {
                    // Update local state
                    val updatedJobs = _uiState.value.jobs.map {
                        if (it.id == jobId) it.copy(isSaved = true) else it
                    }
                    val updatedRecommendedJobs = _uiState.value.recommendedJobs.map {
                        if (it.job.id == jobId) it.copy(job = it.job.copy(isSaved = true)) else it
                    }
                    _uiState.value = _uiState.value.copy(
                        jobs = updatedJobs,
                        recommendedJobs = updatedRecommendedJobs
                    )
                    loadSavedJobs()
                    loadJobStats()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to save job"
                    )
                }
        }
    }

    fun unsaveJob(jobId: String) {
        viewModelScope.launch {
            jobsRepository.unsaveJob(jobId)
                .onSuccess {
                    // Update local state
                    val updatedJobs = _uiState.value.jobs.map {
                        if (it.id == jobId) it.copy(isSaved = false) else it
                    }
                    val updatedRecommendedJobs = _uiState.value.recommendedJobs.map {
                        if (it.job.id == jobId) it.copy(job = it.job.copy(isSaved = false)) else it
                    }
                    val updatedSavedJobs = _uiState.value.savedJobs.filter { it.job.id != jobId }
                    _uiState.value = _uiState.value.copy(
                        jobs = updatedJobs,
                        recommendedJobs = updatedRecommendedJobs,
                        savedJobs = updatedSavedJobs
                    )
                    loadJobStats()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to remove saved job"
                    )
                }
        }
    }

    fun toggleSaveJob(job: Job) {
        if (job.isSaved) {
            unsaveJob(job.id)
        } else {
            saveJob(job.id)
        }
    }

    fun showFilterSheet() {
        _uiState.value = _uiState.value.copy(showFilterSheet = true)
    }

    fun hideFilterSheet() {
        _uiState.value = _uiState.value.copy(showFilterSheet = false)
    }

    fun showApplicationSheet(job: Job) {
        _uiState.value = _uiState.value.copy(
            showApplicationSheet = true,
            selectedJobForApplication = job
        )
    }

    fun hideApplicationSheet() {
        _uiState.value = _uiState.value.copy(
            showApplicationSheet = false,
            selectedJobForApplication = null
        )
    }

    fun applyToJob(coverLetter: String?, resumeUrl: String?) {
        val job = _uiState.value.selectedJobForApplication ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val request = JobApplicationRequest(
                jobId = job.id,
                coverLetter = coverLetter,
                resumeUrl = resumeUrl,
                portfolioUrl = null,
                linkedInUrl = null,
                additionalNotes = null
            )

            jobsRepository.applyToJob(request)
                .onSuccess { application ->
                    // Update local state
                    val updatedJobs = _uiState.value.jobs.map {
                        if (it.id == job.id) it.copy(hasApplied = true) else it
                    }
                    val updatedRecommendedJobs = _uiState.value.recommendedJobs.map {
                        if (it.job.id == job.id) it.copy(job = it.job.copy(hasApplied = true)) else it
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        jobs = updatedJobs,
                        recommendedJobs = updatedRecommendedJobs,
                        applications = listOf(application) + _uiState.value.applications,
                        showApplicationSheet = false,
                        selectedJobForApplication = null
                    )
                    loadJobStats()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to submit application"
                    )
                }
        }
    }

    fun withdrawApplication(applicationId: String) {
        viewModelScope.launch {
            jobsRepository.withdrawApplication(applicationId)
                .onSuccess {
                    val updatedApplications = _uiState.value.applications.map {
                        if (it.id == applicationId) it.copy(status = ApplicationStatus.WITHDRAWN) else it
                    }
                    _uiState.value = _uiState.value.copy(applications = updatedApplications)
                    loadJobStats()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to withdraw application"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun refresh() {
        when (_uiState.value.selectedTab) {
            JobsTab.BROWSE -> loadJobs(refresh = true)
            JobsTab.RECOMMENDED -> loadRecommendedJobs()
            JobsTab.SAVED -> loadSavedJobs()
            JobsTab.APPLICATIONS -> loadApplications()
        }
        loadJobStats()
    }
}
