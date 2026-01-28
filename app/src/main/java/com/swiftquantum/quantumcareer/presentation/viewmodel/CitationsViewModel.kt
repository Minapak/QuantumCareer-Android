package com.swiftquantum.quantumcareer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftquantum.quantumcareer.domain.model.CitationStats
import com.swiftquantum.quantumcareer.domain.repository.CareerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CitationsUiState(
    val isLoading: Boolean = false,
    val stats: CitationStats? = null,
    val error: String? = null
)

@HiltViewModel
class CitationsViewModel @Inject constructor(
    private val careerRepository: CareerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitationsUiState())
    val uiState: StateFlow<CitationsUiState> = _uiState.asStateFlow()

    init {
        loadCitationStats()
    }

    fun loadCitationStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            careerRepository.getCitationStats()
                .onSuccess { stats ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        stats = stats,
                        error = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load citation stats"
                    )
                }
        }
    }

    fun refresh() {
        loadCitationStats()
    }
}
