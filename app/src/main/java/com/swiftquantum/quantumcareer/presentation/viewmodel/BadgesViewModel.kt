package com.swiftquantum.quantumcareer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftquantum.quantumcareer.domain.model.BadgeCollection
import com.swiftquantum.quantumcareer.domain.model.CareerBadge
import com.swiftquantum.quantumcareer.domain.repository.CareerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BadgesUiState(
    val isLoading: Boolean = false,
    val badgeCollection: BadgeCollection? = null,
    val selectedBadge: CareerBadge? = null,
    val error: String? = null
)

@HiltViewModel
class BadgesViewModel @Inject constructor(
    private val careerRepository: CareerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BadgesUiState())
    val uiState: StateFlow<BadgesUiState> = _uiState.asStateFlow()

    init {
        loadBadges()
    }

    fun loadBadges() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            careerRepository.getBadges()
                .onSuccess { collection ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        badgeCollection = collection,
                        error = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load badges"
                    )
                }
        }
    }

    fun selectBadge(badge: CareerBadge) {
        _uiState.value = _uiState.value.copy(selectedBadge = badge)
    }

    fun clearSelectedBadge() {
        _uiState.value = _uiState.value.copy(selectedBadge = null)
    }

    fun refresh() {
        loadBadges()
    }
}
