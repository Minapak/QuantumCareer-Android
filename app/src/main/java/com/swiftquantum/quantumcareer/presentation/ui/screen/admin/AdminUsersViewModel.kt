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

data class AdminUsersUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val users: List<AdminUserItemDto> = emptyList(),
    val filteredUsers: List<AdminUserItemDto> = emptyList(),
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val searchQuery: String = "",
    val currentFilter: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class AdminUsersViewModel @Inject constructor(
    private val adminApi: AdminApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUsersUiState())
    val uiState: StateFlow<AdminUsersUiState> = _uiState.asStateFlow()

    fun loadUsers(page: Int = 1) {
        viewModelScope.launch {
            if (page == 1) {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoadingMore = true) }
            }

            try {
                val response = adminApi.getUsers(
                    page = page,
                    search = _uiState.value.searchQuery.takeIf { it.isNotBlank() },
                    filter = _uiState.value.currentFilter
                )

                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        val newUsers = if (page == 1) {
                            data.users
                        } else {
                            _uiState.value.users + data.users
                        }

                        _uiState.update {
                            it.copy(
                                users = newUsers,
                                filteredUsers = applyLocalFilter(newUsers, it.currentFilter),
                                currentPage = data.page,
                                totalPages = data.totalPages
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(errorMessage = "Failed to load users: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Failed to load users")
                }
            } finally {
                _uiState.update {
                    it.copy(isLoading = false, isLoadingMore = false)
                }
            }
        }
    }

    fun searchUsers(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        if (query.isBlank()) {
            _uiState.update {
                it.copy(filteredUsers = applyLocalFilter(it.users, it.currentFilter))
            }
        } else {
            val filtered = _uiState.value.users.filter { user ->
                user.username?.contains(query, ignoreCase = true) == true ||
                        user.email?.contains(query, ignoreCase = true) == true ||
                        user.displayName?.contains(query, ignoreCase = true) == true
            }
            _uiState.update { it.copy(filteredUsers = filtered) }
        }
    }

    fun filterUsers(filter: String?) {
        _uiState.update { it.copy(currentFilter = filter) }
        _uiState.update {
            it.copy(filteredUsers = applyLocalFilter(it.users, filter))
        }
    }

    private fun applyLocalFilter(users: List<AdminUserItemDto>, filter: String?): List<AdminUserItemDto> {
        return when (filter?.lowercase()) {
            "active" -> users.filter { it.isActive == true }
            "pro" -> users.filter { it.isPro }
            "admin" -> users.filter { it.isAdmin == true }
            else -> users
        }
    }

    fun loadMoreUsers() {
        val state = _uiState.value
        if (!state.isLoadingMore && state.currentPage < state.totalPages) {
            loadUsers(state.currentPage + 1)
        }
    }

    fun grantPremium(userId: Int, tier: String, days: Int) {
        viewModelScope.launch {
            try {
                val response = adminApi.grantPremium(
                    userId = userId,
                    request = GrantPremiumRequest(
                        userId = userId,
                        subscriptionType = tier,
                        days = days
                    )
                )

                if (response.isSuccessful) {
                    // Refresh the user list
                    loadUsers()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to grant premium: ${e.message}")
                }
            }
        }
    }

    fun banUser(userId: Int, reason: String? = null) {
        viewModelScope.launch {
            try {
                val response = adminApi.banUser(
                    userId = userId,
                    request = BanUserRequest(userId = userId, reason = reason)
                )

                if (response.isSuccessful) {
                    // Refresh the user list
                    loadUsers()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to ban user: ${e.message}")
                }
            }
        }
    }

    fun unbanUser(userId: Int) {
        viewModelScope.launch {
            try {
                val response = adminApi.unbanUser(userId)

                if (response.isSuccessful) {
                    // Refresh the user list
                    loadUsers()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to unban user: ${e.message}")
                }
            }
        }
    }
}
