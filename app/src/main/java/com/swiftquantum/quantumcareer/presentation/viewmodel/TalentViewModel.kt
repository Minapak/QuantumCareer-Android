package com.swiftquantum.quantumcareer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftquantum.quantumcareer.domain.model.*
import com.swiftquantum.quantumcareer.domain.repository.TalentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TalentUiState(
    val isLoading: Boolean = false,
    val searchResults: List<TalentProfile> = emptyList(),
    val receivedOffers: List<TalentOffer> = emptyList(),
    val sentOffers: List<TalentOffer> = emptyList(),
    val selectedProfile: TalentProfile? = null,
    val selectedTab: Int = 0,
    val error: String? = null,
    val scoutSuccess: Boolean = false
)

data class TalentSearchFormState(
    val query: String = "",
    val minHIndex: String = "",
    val minPublications: String = "",
    val badgeTier: BadgeTier? = null
)

data class ScoutFormState(
    val message: String = "",
    val offerType: OfferType = OfferType.JOB,
    val organization: String = "",
    val position: String = "",
    val details: String = ""
)

@HiltViewModel
class TalentViewModel @Inject constructor(
    private val talentRepository: TalentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TalentUiState())
    val uiState: StateFlow<TalentUiState> = _uiState.asStateFlow()

    private val _searchForm = MutableStateFlow(TalentSearchFormState())
    val searchForm: StateFlow<TalentSearchFormState> = _searchForm.asStateFlow()

    private val _scoutForm = MutableStateFlow(ScoutFormState())
    val scoutForm: StateFlow<ScoutFormState> = _scoutForm.asStateFlow()

    init {
        loadOffers()
    }

    fun selectTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index)
    }

    fun updateSearchQuery(query: String) {
        _searchForm.value = _searchForm.value.copy(query = query)
    }

    fun updateMinHIndex(value: String) {
        _searchForm.value = _searchForm.value.copy(minHIndex = value)
    }

    fun updateMinPublications(value: String) {
        _searchForm.value = _searchForm.value.copy(minPublications = value)
    }

    fun updateBadgeTier(tier: BadgeTier?) {
        _searchForm.value = _searchForm.value.copy(badgeTier = tier)
    }

    fun searchTalent() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val form = _searchForm.value
            val criteria = TalentSearchCriteria(
                query = form.query.takeIf { it.isNotBlank() },
                minHIndex = form.minHIndex.toIntOrNull(),
                minPublications = form.minPublications.toIntOrNull(),
                badgeTier = form.badgeTier
            )

            talentRepository.searchTalent(criteria)
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        searchResults = result.profiles,
                        error = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Search failed"
                    )
                }
        }
    }

    fun selectProfile(profile: TalentProfile) {
        _uiState.value = _uiState.value.copy(selectedProfile = profile)
        _scoutForm.value = ScoutFormState()
    }

    fun clearSelectedProfile() {
        _uiState.value = _uiState.value.copy(selectedProfile = null)
    }

    fun updateScoutMessage(message: String) {
        _scoutForm.value = _scoutForm.value.copy(message = message)
    }

    fun updateOfferType(type: OfferType) {
        _scoutForm.value = _scoutForm.value.copy(offerType = type)
    }

    fun updateOrganization(org: String) {
        _scoutForm.value = _scoutForm.value.copy(organization = org)
    }

    fun updatePosition(position: String) {
        _scoutForm.value = _scoutForm.value.copy(position = position)
    }

    fun updateDetails(details: String) {
        _scoutForm.value = _scoutForm.value.copy(details = details)
    }

    fun scoutTalent() {
        val profile = _uiState.value.selectedProfile ?: return
        val form = _scoutForm.value

        if (form.message.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Message is required")
            return
        }

        if (form.organization.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Organization is required")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val request = ScoutRequest(
                targetUserId = profile.userId,
                message = form.message,
                offerType = form.offerType,
                organization = form.organization,
                position = form.position.takeIf { it.isNotBlank() },
                details = form.details.takeIf { it.isNotBlank() }
            )

            talentRepository.scoutTalent(request)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        scoutSuccess = true,
                        selectedProfile = null
                    )
                    _scoutForm.value = ScoutFormState()
                    loadOffers()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to send offer"
                    )
                }
        }
    }

    fun loadOffers() {
        viewModelScope.launch {
            talentRepository.getOffers()
                .onSuccess { offers ->
                    _uiState.value = _uiState.value.copy(receivedOffers = offers)
                }

            talentRepository.getSentOffers()
                .onSuccess { offers ->
                    _uiState.value = _uiState.value.copy(sentOffers = offers)
                }
        }
    }

    fun respondToOffer(offerId: String, accept: Boolean, message: String? = null) {
        viewModelScope.launch {
            talentRepository.respondToOffer(offerId, accept, message)
                .onSuccess {
                    loadOffers()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to respond to offer"
                    )
                }
        }
    }

    fun withdrawOffer(offerId: String) {
        viewModelScope.launch {
            talentRepository.withdrawOffer(offerId)
                .onSuccess {
                    loadOffers()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to withdraw offer"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(scoutSuccess = false)
    }
}
