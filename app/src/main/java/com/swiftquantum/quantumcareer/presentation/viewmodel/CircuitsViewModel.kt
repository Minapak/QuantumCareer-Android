package com.swiftquantum.quantumcareer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftquantum.quantumcareer.domain.model.CircuitStatus
import com.swiftquantum.quantumcareer.domain.model.PublishedCircuit
import com.swiftquantum.quantumcareer.domain.repository.CareerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CircuitsUiState(
    val isLoading: Boolean = false,
    val circuits: List<PublishedCircuit> = emptyList(),
    val selectedFilter: CircuitStatus? = null,
    val error: String? = null
)

@HiltViewModel
class CircuitsViewModel @Inject constructor(
    private val careerRepository: CareerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CircuitsUiState())
    val uiState: StateFlow<CircuitsUiState> = _uiState.asStateFlow()

    init {
        loadCircuits()
    }

    fun loadCircuits(status: CircuitStatus? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                selectedFilter = status
            )

            careerRepository.getCircuits(status = status)
                .onSuccess { circuits ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        circuits = circuits,
                        error = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load circuits"
                    )
                }
        }
    }

    fun filterByStatus(status: CircuitStatus?) {
        loadCircuits(status)
    }

    fun deleteCircuit(circuitId: String) {
        viewModelScope.launch {
            careerRepository.deleteCircuit(circuitId)
                .onSuccess {
                    loadCircuits(_uiState.value.selectedFilter)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to delete circuit"
                    )
                }
        }
    }

    fun refresh() {
        loadCircuits(_uiState.value.selectedFilter)
    }
}
