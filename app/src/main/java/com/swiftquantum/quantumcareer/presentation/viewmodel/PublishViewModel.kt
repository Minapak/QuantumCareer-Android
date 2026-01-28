package com.swiftquantum.quantumcareer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftquantum.quantumcareer.domain.model.PublishCircuitRequest
import com.swiftquantum.quantumcareer.domain.model.PublishedCircuit
import com.swiftquantum.quantumcareer.domain.repository.CareerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PublishUiState(
    val title: String = "",
    val description: String = "",
    val qasmCode: String = "",
    val tags: String = "",
    val isPublic: Boolean = true,
    val isLoading: Boolean = false,
    val publishedCircuit: PublishedCircuit? = null,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class PublishViewModel @Inject constructor(
    private val careerRepository: CareerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublishUiState())
    val uiState: StateFlow<PublishUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun updateQasmCode(qasmCode: String) {
        _uiState.value = _uiState.value.copy(qasmCode = qasmCode)
    }

    fun updateTags(tags: String) {
        _uiState.value = _uiState.value.copy(tags = tags)
    }

    fun updateIsPublic(isPublic: Boolean) {
        _uiState.value = _uiState.value.copy(isPublic = isPublic)
    }

    fun publishCircuit() {
        val state = _uiState.value

        if (state.title.isBlank()) {
            _uiState.value = state.copy(error = "Title is required")
            return
        }

        if (state.qasmCode.isBlank()) {
            _uiState.value = state.copy(error = "QASM code is required")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val tags = state.tags.split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            val request = PublishCircuitRequest(
                title = state.title,
                description = state.description,
                qasmCode = state.qasmCode,
                tags = tags,
                isPublic = state.isPublic
            )

            careerRepository.publishCircuit(request)
                .onSuccess { circuit ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        publishedCircuit = circuit,
                        isSuccess = true,
                        error = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to publish circuit"
                    )
                }
        }
    }

    fun resetForm() {
        _uiState.value = PublishUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false, publishedCircuit = null)
    }
}
