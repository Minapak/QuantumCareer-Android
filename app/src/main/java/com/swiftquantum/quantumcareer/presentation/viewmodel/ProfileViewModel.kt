package com.swiftquantum.quantumcareer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftquantum.quantumcareer.domain.model.PublicProfile
import com.swiftquantum.quantumcareer.domain.model.UpdateProfileRequest
import com.swiftquantum.quantumcareer.domain.repository.CareerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: PublicProfile? = null,
    val isEditing: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

data class ProfileEditFormState(
    val displayName: String = "",
    val bio: String = "",
    val institution: String = "",
    val location: String = "",
    val website: String = "",
    val specializations: String = "",
    val isPublic: Boolean = true,
    val isAvailableForHire: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val careerRepository: CareerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _editForm = MutableStateFlow(ProfileEditFormState())
    val editForm: StateFlow<ProfileEditFormState> = _editForm.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            careerRepository.getProfile()
                .onSuccess { profile ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        profile = profile,
                        error = null
                    )
                    populateEditForm(profile)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load profile"
                    )
                }
        }
    }

    private fun populateEditForm(profile: PublicProfile) {
        _editForm.value = ProfileEditFormState(
            displayName = profile.displayName,
            bio = profile.bio ?: "",
            institution = profile.institution ?: "",
            location = profile.location ?: "",
            website = profile.website ?: "",
            specializations = profile.specializations.joinToString(", "),
            isPublic = profile.isPublic
        )
    }

    fun startEditing() {
        _uiState.value.profile?.let { populateEditForm(it) }
        _uiState.value = _uiState.value.copy(isEditing = true)
    }

    fun cancelEditing() {
        _uiState.value = _uiState.value.copy(isEditing = false)
        _uiState.value.profile?.let { populateEditForm(it) }
    }

    fun updateDisplayName(name: String) {
        _editForm.value = _editForm.value.copy(displayName = name)
    }

    fun updateBio(bio: String) {
        _editForm.value = _editForm.value.copy(bio = bio)
    }

    fun updateInstitution(institution: String) {
        _editForm.value = _editForm.value.copy(institution = institution)
    }

    fun updateLocation(location: String) {
        _editForm.value = _editForm.value.copy(location = location)
    }

    fun updateWebsite(website: String) {
        _editForm.value = _editForm.value.copy(website = website)
    }

    fun updateSpecializations(specializations: String) {
        _editForm.value = _editForm.value.copy(specializations = specializations)
    }

    fun updateIsPublic(isPublic: Boolean) {
        _editForm.value = _editForm.value.copy(isPublic = isPublic)
    }

    fun updateIsAvailableForHire(isAvailable: Boolean) {
        _editForm.value = _editForm.value.copy(isAvailableForHire = isAvailable)
    }

    fun saveProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val form = _editForm.value
            val specializations = form.specializations.split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            val request = UpdateProfileRequest(
                displayName = form.displayName.takeIf { it.isNotBlank() },
                bio = form.bio.takeIf { it.isNotBlank() },
                institution = form.institution.takeIf { it.isNotBlank() },
                location = form.location.takeIf { it.isNotBlank() },
                website = form.website.takeIf { it.isNotBlank() },
                specializations = specializations.takeIf { it.isNotEmpty() },
                isPublic = form.isPublic,
                isAvailableForHire = form.isAvailableForHire
            )

            careerRepository.updateProfile(request)
                .onSuccess { profile ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        profile = profile,
                        isEditing = false,
                        saveSuccess = true,
                        error = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to save profile"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    fun refresh() {
        loadProfile()
    }
}
