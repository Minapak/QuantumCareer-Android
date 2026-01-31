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

data class AdminSettingsUiState(
    val isLoading: Boolean = false,
    val settings: AdminSettingsDto = AdminSettingsDto(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class AdminSettingsViewModel @Inject constructor(
    private val adminApi: AdminApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminSettingsUiState())
    val uiState: StateFlow<AdminSettingsUiState> = _uiState.asStateFlow()

    fun loadSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val response = adminApi.getSettings()
                if (response.isSuccessful) {
                    response.body()?.let { settings ->
                        _uiState.update { it.copy(settings = settings) }
                    }
                } else {
                    _uiState.update {
                        it.copy(errorMessage = "Failed to load settings: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Failed to load settings")
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateMaintenanceMode(enabled: Boolean) {
        updateSettings(maintenanceMode = enabled)
    }

    fun updateRegistrationEnabled(enabled: Boolean) {
        updateSettings(registrationEnabled = enabled)
    }

    fun updateEmailVerificationRequired(required: Boolean) {
        updateSettings(emailVerificationRequired = required)
    }

    private fun updateSettings(
        maintenanceMode: Boolean? = null,
        registrationEnabled: Boolean? = null,
        emailVerificationRequired: Boolean? = null,
        maxLoginAttempts: Int? = null,
        sessionTimeoutHours: Int? = null
    ) {
        viewModelScope.launch {
            try {
                val request = UpdateAdminSettingsRequest(
                    maintenanceMode = maintenanceMode,
                    registrationEnabled = registrationEnabled,
                    emailVerificationRequired = emailVerificationRequired,
                    maxLoginAttempts = maxLoginAttempts,
                    sessionTimeoutHours = sessionTimeoutHours
                )

                val response = adminApi.updateSettings(request)
                if (response.isSuccessful) {
                    response.body()?.let { settings ->
                        _uiState.update {
                            it.copy(
                                settings = settings,
                                successMessage = "Settings updated successfully"
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(errorMessage = "Failed to update settings: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Failed to update settings")
                }
            }
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            try {
                val response = adminApi.clearCache()
                if (response.isSuccessful) {
                    _uiState.update { it.copy(successMessage = "Cache cleared successfully") }
                } else {
                    _uiState.update {
                        it.copy(errorMessage = "Failed to clear cache: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Failed to clear cache")
                }
            }
        }
    }

    fun triggerBackup() {
        viewModelScope.launch {
            try {
                val response = adminApi.triggerBackup()
                if (response.isSuccessful) {
                    _uiState.update { it.copy(successMessage = "Backup started successfully") }
                } else {
                    _uiState.update {
                        it.copy(errorMessage = "Failed to start backup: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Failed to start backup")
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
