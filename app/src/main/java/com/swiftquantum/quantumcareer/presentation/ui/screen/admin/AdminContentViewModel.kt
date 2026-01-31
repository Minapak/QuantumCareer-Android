package com.swiftquantum.quantumcareer.presentation.ui.screen.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftquantum.quantumcareer.data.api.AdminApi
import com.swiftquantum.quantumcareer.data.dto.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminContentUiState(
    val isLoading: Boolean = false,
    val questions: List<AdminQuestionItemDto> = emptyList(),
    val jobs: List<AdminJobItemDto> = emptyList(),
    val badges: List<AdminBadgeItemDto> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class AdminContentViewModel @Inject constructor(
    private val adminApi: AdminApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminContentUiState())
    val uiState: StateFlow<AdminContentUiState> = _uiState.asStateFlow()

    // ============================================
    // Questions
    // ============================================

    fun loadQuestions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val response = adminApi.getQuestions()
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        _uiState.update { it.copy(questions = data.questions) }
                    }
                } else {
                    _uiState.update {
                        it.copy(errorMessage = "Failed to load questions: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Failed to load questions")
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun createQuestion(request: CreateQuestionRequest) {
        viewModelScope.launch {
            try {
                val response = adminApi.createQuestion(request)
                if (response.isSuccessful) {
                    loadQuestions()
                } else {
                    _uiState.update {
                        it.copy(errorMessage = "Failed to create question: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Failed to create question")
                }
            }
        }
    }

    fun deleteQuestion(questionId: Int) {
        viewModelScope.launch {
            try {
                val response = adminApi.deleteQuestion(questionId)
                if (response.isSuccessful) {
                    _uiState.update { state ->
                        state.copy(questions = state.questions.filter { it.id != questionId })
                    }
                } else {
                    _uiState.update {
                        it.copy(errorMessage = "Failed to delete question: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Failed to delete question")
                }
            }
        }
    }

    // ============================================
    // Jobs
    // ============================================

    fun loadJobs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val response = adminApi.getJobs()
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        _uiState.update { it.copy(jobs = data.jobs) }
                    }
                } else {
                    _uiState.update {
                        it.copy(errorMessage = "Failed to load jobs: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Failed to load jobs")
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun createJob(request: CreateJobRequest) {
        viewModelScope.launch {
            try {
                val response = adminApi.createJob(request)
                if (response.isSuccessful) {
                    loadJobs()
                } else {
                    _uiState.update {
                        it.copy(errorMessage = "Failed to create job: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Failed to create job")
                }
            }
        }
    }

    fun deleteJob(jobId: Int) {
        viewModelScope.launch {
            try {
                val response = adminApi.deleteJob(jobId)
                if (response.isSuccessful) {
                    _uiState.update { state ->
                        state.copy(jobs = state.jobs.filter { it.id != jobId })
                    }
                } else {
                    _uiState.update {
                        it.copy(errorMessage = "Failed to delete job: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Failed to delete job")
                }
            }
        }
    }

    fun toggleJobActive(jobId: Int) {
        viewModelScope.launch {
            try {
                val response = adminApi.toggleJobActive(jobId)
                if (response.isSuccessful) {
                    response.body()?.let { updatedJob ->
                        _uiState.update { state ->
                            state.copy(
                                jobs = state.jobs.map {
                                    if (it.id == jobId) updatedJob else it
                                }
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(errorMessage = "Failed to toggle job status: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Failed to toggle job status")
                }
            }
        }
    }

    // ============================================
    // Badges
    // ============================================

    fun loadBadges() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val response = adminApi.getBadges()
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        _uiState.update { it.copy(badges = data.badges) }
                    }
                } else {
                    _uiState.update {
                        it.copy(errorMessage = "Failed to load badges: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Failed to load badges")
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
