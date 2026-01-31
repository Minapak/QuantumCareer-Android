package com.swiftquantum.quantumcareer.presentation.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftquantum.quantumcareer.data.api.AuthApi
import com.swiftquantum.quantumcareer.data.auth.AuthData
import com.swiftquantum.quantumcareer.data.auth.SharedAuthManager
import com.swiftquantum.quantumcareer.data.dto.LoginRequest
import com.swiftquantum.quantumcareer.data.dto.RegisterRequest
import com.swiftquantum.quantumcareer.data.dto.UserDto
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
    val currentUser: UserDto? = null,
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
    private val sharedAuthManager: SharedAuthManager,
    private val authApi: AuthApi
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
                // SwiftQuantumBackend API 호출
                println("🌐 QuantumCareer: Logging in with $email")
                val response = authApi.login(LoginRequest(email, password))

                // 사용자 정보 가져오기
                val user = response.user ?: authApi.getCurrentUser()

                sharedAuthManager.saveAuth(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken ?: "",
                    userId = user.id.toString(),
                    userEmail = user.email,
                    userName = user.fullName ?: user.username
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    currentUser = user
                )

                println("✅ QuantumCareer: Login successful for ${user.email}, isPro=${user.isPro}")
                _events.emit(AuthEvent.LoginSuccess)

            } catch (e: Exception) {
                println("❌ QuantumCareer: Login failed: ${e.message}")
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
                // SwiftQuantumBackend API 호출
                println("🌐 QuantumCareer: Registering $email")
                val response = authApi.register(RegisterRequest(email, password, name))

                // 사용자 정보 가져오기
                val user = response.user ?: authApi.getCurrentUser()

                sharedAuthManager.saveAuth(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken ?: "",
                    userId = user.id.toString(),
                    userEmail = user.email,
                    userName = user.fullName ?: user.username
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    currentUser = user
                )

                println("✅ QuantumCareer: Registration successful for ${user.email}")
                _events.emit(AuthEvent.RegisterSuccess)

            } catch (e: Exception) {
                println("❌ QuantumCareer: Registration failed: ${e.message}")
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
                // 백엔드 로그아웃 호출 (실패해도 로컬 로그아웃 진행)
                try {
                    authApi.logout()
                } catch (e: Exception) {
                    println("⚠️ QuantumCareer: Backend logout failed: ${e.message}")
                }

                sharedAuthManager.clearAuth()
                _uiState.value = AuthUiState()
                println("🔓 QuantumCareer: User logged out")
                _events.emit(AuthEvent.LogoutSuccess)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Logout failed"
                )
            }
        }
    }

    /**
     * 사용자 프로필 동기화 (백엔드에서 최신 정보 로드)
     */
    fun syncUserProfile() {
        viewModelScope.launch {
            try {
                val user = authApi.getCurrentUser()
                _uiState.value = _uiState.value.copy(currentUser = user)
                println("✅ QuantumCareer: User profile synced, isPro=${user.isPro}")
            } catch (e: Exception) {
                println("⚠️ QuantumCareer: Failed to sync user profile: ${e.message}")
            }
        }
    }

    /**
     * 현재 사용자가 Pro 구독자인지 확인
     */
    fun isPro(): Boolean {
        return _uiState.value.currentUser?.isPro ?: _uiState.value.authData?.isPro ?: false
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
