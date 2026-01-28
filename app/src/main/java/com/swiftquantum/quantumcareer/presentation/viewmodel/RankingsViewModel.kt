package com.swiftquantum.quantumcareer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftquantum.quantumcareer.domain.model.*
import com.swiftquantum.quantumcareer.domain.repository.RankingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RankingsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    // Leaderboard state
    val leaderboard: Leaderboard? = null,
    val selectedRankingType: RankingType = RankingType.OVERALL,

    // User's rank state
    val userRankingStats: UserRankingStats? = null,

    // Friends ranking state
    val friendsRanking: FriendsRanking? = null,

    // Filter options
    val countries: List<RankingCountry> = emptyList(),
    val institutions: List<RankingInstitution> = emptyList(),
    val selectedCountry: String? = null,
    val selectedInstitution: String? = null,
    val minBadge: BadgeTier? = null,

    // Achievements state
    val achievements: List<RankingAchievement> = emptyList(),

    // Pagination
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val isLoadingMore: Boolean = false
)

@HiltViewModel
class RankingsViewModel @Inject constructor(
    private val rankingsRepository: RankingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RankingsUiState())
    val uiState: StateFlow<RankingsUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Load leaderboard
            loadLeaderboard()

            // Load user's rank
            rankingsRepository.getMyRank()
                .onSuccess { stats ->
                    _uiState.value = _uiState.value.copy(userRankingStats = stats)
                }

            // Load countries for filtering
            rankingsRepository.getCountries()
                .onSuccess { countries ->
                    _uiState.value = _uiState.value.copy(countries = countries)
                }

            // Load achievements
            rankingsRepository.getAchievements()
                .onSuccess { achievements ->
                    _uiState.value = _uiState.value.copy(achievements = achievements)
                }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    private suspend fun loadLeaderboard() {
        val filter = RankingFilter(
            type = _uiState.value.selectedRankingType,
            country = _uiState.value.selectedCountry,
            institution = _uiState.value.selectedInstitution,
            minBadge = _uiState.value.minBadge,
            page = 1,
            perPage = 50
        )

        rankingsRepository.getLeaderboard(filter)
            .onSuccess { leaderboard ->
                _uiState.value = _uiState.value.copy(
                    leaderboard = leaderboard,
                    currentPage = 1,
                    hasMorePages = leaderboard.entries.size >= 50
                )
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    error = error.message ?: "Failed to load leaderboard"
                )
            }
    }

    fun selectRankingType(type: RankingType) {
        if (type == _uiState.value.selectedRankingType) return

        _uiState.value = _uiState.value.copy(
            selectedRankingType = type,
            selectedCountry = null,
            selectedInstitution = null,
            currentPage = 1
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            loadLeaderboard()
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun selectCountry(country: String?) {
        _uiState.value = _uiState.value.copy(
            selectedCountry = country,
            selectedInstitution = null,
            currentPage = 1
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Load institutions for the selected country
            if (country != null) {
                rankingsRepository.getInstitutions(country = country)
                    .onSuccess { institutions ->
                        _uiState.value = _uiState.value.copy(institutions = institutions)
                    }
            } else {
                _uiState.value = _uiState.value.copy(institutions = emptyList())
            }

            loadLeaderboard()
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun selectInstitution(institution: String?) {
        _uiState.value = _uiState.value.copy(
            selectedInstitution = institution,
            currentPage = 1
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            loadLeaderboard()
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun setMinBadge(badge: BadgeTier?) {
        _uiState.value = _uiState.value.copy(
            minBadge = badge,
            currentPage = 1
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            loadLeaderboard()
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun loadMoreEntries() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMorePages) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingMore = true)

            val nextPage = _uiState.value.currentPage + 1
            val filter = RankingFilter(
                type = _uiState.value.selectedRankingType,
                country = _uiState.value.selectedCountry,
                institution = _uiState.value.selectedInstitution,
                minBadge = _uiState.value.minBadge,
                page = nextPage,
                perPage = 50
            )

            rankingsRepository.getLeaderboard(filter)
                .onSuccess { newLeaderboard ->
                    val currentEntries = _uiState.value.leaderboard?.entries ?: emptyList()
                    val updatedLeaderboard = newLeaderboard.copy(
                        entries = currentEntries + newLeaderboard.entries
                    )
                    _uiState.value = _uiState.value.copy(
                        leaderboard = updatedLeaderboard,
                        currentPage = nextPage,
                        hasMorePages = newLeaderboard.entries.size >= 50,
                        isLoadingMore = false
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoadingMore = false)
                }
        }
    }

    fun loadFriendsRankings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            rankingsRepository.getFriendsRankings()
                .onSuccess { friendsRanking ->
                    _uiState.value = _uiState.value.copy(
                        friendsRanking = friendsRanking,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to load friends rankings",
                        isLoading = false
                    )
                }
        }
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(
            selectedCountry = null,
            selectedInstitution = null,
            minBadge = null,
            currentPage = 1
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            loadLeaderboard()
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun refresh() {
        loadInitialData()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
