package com.swiftquantum.quantumcareer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftquantum.quantumcareer.domain.model.*
import com.swiftquantum.quantumcareer.domain.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PeerReviewUiState(
    val isLoading: Boolean = false,
    val pendingReviews: List<PeerReview> = emptyList(),
    val myReviews: List<PeerReview> = emptyList(),
    val selectedReview: PeerReview? = null,
    val reviewerStats: ReviewerStats? = null,
    val selectedTab: Int = 0,
    val error: String? = null,
    val submitSuccess: Boolean = false
)

data class SubmitReviewFormState(
    val decision: ReviewDecision? = null,
    val comment: String = "",
    val technicalScore: Int = 3,
    val innovationScore: Int = 3,
    val documentationScore: Int = 3
)

@HiltViewModel
class PeerReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PeerReviewUiState())
    val uiState: StateFlow<PeerReviewUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(SubmitReviewFormState())
    val formState: StateFlow<SubmitReviewFormState> = _formState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        loadPendingReviews()
        loadMyReviews()
        loadReviewerStats()
    }

    private fun loadPendingReviews() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            reviewRepository.getPendingReviews()
                .onSuccess { reviews ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        pendingReviews = reviews
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    private fun loadMyReviews() {
        viewModelScope.launch {
            reviewRepository.getMyReviews()
                .onSuccess { reviews ->
                    _uiState.value = _uiState.value.copy(myReviews = reviews)
                }
        }
    }

    private fun loadReviewerStats() {
        viewModelScope.launch {
            reviewRepository.getReviewerStats()
                .onSuccess { stats ->
                    _uiState.value = _uiState.value.copy(reviewerStats = stats)
                }
        }
    }

    fun selectTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index)
    }

    fun selectReview(review: PeerReview) {
        _uiState.value = _uiState.value.copy(selectedReview = review)
        _formState.value = SubmitReviewFormState()
    }

    fun clearSelectedReview() {
        _uiState.value = _uiState.value.copy(selectedReview = null)
        _formState.value = SubmitReviewFormState()
    }

    fun updateDecision(decision: ReviewDecision) {
        _formState.value = _formState.value.copy(decision = decision)
    }

    fun updateComment(comment: String) {
        _formState.value = _formState.value.copy(comment = comment)
    }

    fun updateTechnicalScore(score: Int) {
        _formState.value = _formState.value.copy(technicalScore = score)
    }

    fun updateInnovationScore(score: Int) {
        _formState.value = _formState.value.copy(innovationScore = score)
    }

    fun updateDocumentationScore(score: Int) {
        _formState.value = _formState.value.copy(documentationScore = score)
    }

    fun claimReview(reviewId: String) {
        viewModelScope.launch {
            reviewRepository.claimReview(reviewId)
                .onSuccess { review ->
                    _uiState.value = _uiState.value.copy(selectedReview = review)
                    loadPendingReviews()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
        }
    }

    fun submitReview() {
        val review = _uiState.value.selectedReview ?: return
        val form = _formState.value

        if (form.decision == null) {
            _uiState.value = _uiState.value.copy(error = "Please select a decision")
            return
        }

        if (form.comment.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please provide a comment")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val request = SubmitReviewRequest(
                reviewId = review.id,
                decision = form.decision,
                comment = form.comment,
                technicalScore = form.technicalScore,
                innovationScore = form.innovationScore,
                documentationScore = form.documentationScore
            )

            reviewRepository.submitReview(request)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        submitSuccess = true,
                        selectedReview = null
                    )
                    _formState.value = SubmitReviewFormState()
                    loadData()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to submit review"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(submitSuccess = false)
    }
}
