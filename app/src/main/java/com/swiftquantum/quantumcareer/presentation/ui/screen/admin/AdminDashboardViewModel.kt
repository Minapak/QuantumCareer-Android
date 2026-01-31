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

data class AdminDashboardUiState(
    val isLoading: Boolean = false,
    val stats: AdminStatsDto = AdminStatsDto(),
    val systemHealth: SystemHealthDto = SystemHealthDto(),
    val recentActivity: List<AdminActivityItemDto> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val adminApi: AdminApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // Load stats
                val statsResponse = adminApi.getStats()
                if (statsResponse.isSuccessful) {
                    statsResponse.body()?.let { stats ->
                        _uiState.update { it.copy(stats = stats) }
                    }
                }

                // Load system health
                val healthResponse = adminApi.getSystemHealth()
                if (healthResponse.isSuccessful) {
                    healthResponse.body()?.let { health ->
                        _uiState.update { it.copy(systemHealth = health) }
                    }
                }

                // Load recent activity
                val activityResponse = adminApi.getRecentActivity(limit = 20)
                if (activityResponse.isSuccessful) {
                    activityResponse.body()?.let { activityData ->
                        _uiState.update { it.copy(recentActivity = activityData.activities) }
                    }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Failed to load dashboard data")
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun refresh() {
        loadDashboardData()
    }
}
