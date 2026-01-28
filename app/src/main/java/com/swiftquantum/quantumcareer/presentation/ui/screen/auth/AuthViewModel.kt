package com.swiftquantum.quantumcareer.presentation.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftquantum.quantumcareer.data.auth.AuthData
import com.swiftquantum.quantumcareer.data.auth.SharedAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val authData: AuthData? = null,
    val error: String? = null
)

sealed class AuthEvent {
    data object LoginSuccess : AuthEvent()
    data object RegisterSuccess : AuthEvent()
    data object LogoutSuccess : AuthEvent()
    data class Error(val message: String) : AuthEvent()
    data object PasswordResetSent : AuthEvent()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val sharedAuthManager: SharedAuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            sharedAuthManager.observeAuthState().collect { authData ->
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = authData.isLoggedIn,
                    authData = authData
                )
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please fill in all fields")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // TODO: Replace with actual API call
                kotlinx.coroutines.delay(1000)

                val userId = "user_${System.currentTimeMillis()}"
                val accessToken = "token_${System.currentTimeMillis()}"
                val refreshToken = "refresh_${System.currentTimeMillis()}"

                sharedAuthManager.saveAuth(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    userId = userId,
                    userEmail = email,
                    userName = email.substringBefore("@")
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true
                )
                _events.emit(AuthEvent.LoginSuccess)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Login failed"
                )
                _events.emit(AuthEvent.Error(e.message ?: "Login failed"))
            }
        }
    }

    fun register(email: String, password: String, confirmPassword: String, name: String) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please fill in all fields")
            return
        }

        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(error = "Passwords do not match")
            return
        }

        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(error = "Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                kotlinx.coroutines.delay(1000)

                val userId = "user_${System.currentTimeMillis()}"
                val accessToken = "token_${System.currentTimeMillis()}"
                val refreshToken = "refresh_${System.currentTimeMillis()}"

                sharedAuthManager.saveAuth(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    userId = userId,
                    userEmail = email,
                    userName = name
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true
                )
                _events.emit(AuthEvent.RegisterSuccess)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Registration failed"
                )
                _events.emit(AuthEvent.Error(e.message ?: "Registration failed"))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                sharedAuthManager.clearAuth()
                _uiState.value = AuthUiState()
                _events.emit(AuthEvent.LogoutSuccess)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Logout failed"
                )
            }
        }
    }

    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please enter your email")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                kotlinx.coroutines.delay(1000)
                _uiState.value = _uiState.value.copy(isLoading = false)
                _events.emit(AuthEvent.PasswordResetSent)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to send reset email"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
