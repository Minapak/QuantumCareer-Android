package com.swiftquantum.quantumcareer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftquantum.quantumcareer.data.auth.AuthData
import com.swiftquantum.quantumcareer.data.auth.SharedAuthManager
import com.swiftquantum.quantumcareer.domain.model.CareerBadge
import com.swiftquantum.quantumcareer.domain.model.CircuitStatus
import com.swiftquantum.quantumcareer.domain.model.PublishedCircuit
import com.swiftquantum.quantumcareer.domain.model.PublicProfile
import com.swiftquantum.quantumcareer.domain.model.UpdateProfileRequest
import com.swiftquantum.quantumcareer.domain.repository.CareerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

enum class ProfileTab {
    OVERVIEW,
    CERTIFICATES,
    BADGES,
    CIRCUITS,
    PORTFOLIO
}

data class ContributionDay(
    val date: LocalDate,
    val count: Int,
    val level: Int // 0-4 for intensity
)

data class SkillBreakdown(
    val logic: Float,
    val innovation: Float,
    val contribution: Float,
    val stability: Float,
    val speed: Float
)

data class IndustryReadinessScore(
    val score: Int, // 0-100
    val level: String, // "Beginner", "Intermediate", "Advanced", "Expert"
    val evidenceBreakdown: Map<String, Int> // category -> points
)

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: PublicProfile? = null,
    val isEditing: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false,
    // Shared auth state
    val isLoggedIn: Boolean = false,
    val authData: AuthData? = null,
    val showLogoutDialog: Boolean = false,
    val isLoggingOut: Boolean = false,
    // New tab-related state
    val selectedTab: ProfileTab = ProfileTab.OVERVIEW,
    val contributionData: List<ContributionDay> = emptyList(),
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val industryReadinessScore: IndustryReadinessScore? = null,
    val certificates: List<CertificateInfo> = emptyList(),
    val earnedBadges: List<CareerBadge> = emptyList(),
    val inProgressBadges: List<ProfileBadgeProgress> = emptyList(),
    val publishedCircuits: List<PublishedCircuit> = emptyList(),
    val skillBreakdown: SkillBreakdown? = null
)

data class CertificateInfo(
    val id: String,
    val name: String,
    val level: String,
    val issuedAt: LocalDate,
    val expiresAt: LocalDate?,
    val verificationCode: String,
    val linkedInShareUrl: String?
)

data class ProfileBadgeProgress(
    val badge: CareerBadge,
    val currentProgress: Int,
    val targetProgress: Int
) {
    val progressPercentage: Float
        get() = if (targetProgress > 0) currentProgress.toFloat() / targetProgress else 0f
}

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
    private val careerRepository: CareerRepository,
    private val sharedAuthManager: SharedAuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _editForm = MutableStateFlow(ProfileEditFormState())
    val editForm: StateFlow<ProfileEditFormState> = _editForm.asStateFlow()

    init {
        loadProfile()
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

    fun showLogoutDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showLogoutDialog = show)
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoggingOut = true)
            sharedAuthManager.clearAuth()
            _uiState.value = _uiState.value.copy(
                isLoggingOut = false,
                showLogoutDialog = false,
                isLoggedIn = false,
                authData = null
            )
        }
    }

    fun selectTab(tab: ProfileTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
        when (tab) {
            ProfileTab.OVERVIEW -> loadOverviewData()
            ProfileTab.CERTIFICATES -> loadCertificates()
            ProfileTab.BADGES -> loadBadges()
            ProfileTab.CIRCUITS -> loadCircuits()
            ProfileTab.PORTFOLIO -> loadPortfolio()
        }
    }

    private fun loadOverviewData() {
        viewModelScope.launch {
            // Load contribution timeline (mock data for now)
            val contributionData = generateMockContributionData()
            val streakInfo = calculateStreakInfo(contributionData)

            // Load industry readiness score
            val readinessScore = IndustryReadinessScore(
                score = 72,
                level = "Intermediate",
                evidenceBreakdown = mapOf(
                    "Certifications" to 25,
                    "Published Circuits" to 20,
                    "Peer Reviews" to 15,
                    "Quiz Scores" to 12
                )
            )

            _uiState.value = _uiState.value.copy(
                contributionData = contributionData,
                currentStreak = streakInfo.first,
                longestStreak = streakInfo.second,
                industryReadinessScore = readinessScore
            )
        }
    }

    private fun generateMockContributionData(): List<ContributionDay> {
        val today = LocalDate.now()
        return (0 until 365).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong())
            val count = (0..5).random()
            ContributionDay(
                date = date,
                count = count,
                level = minOf(count, 4)
            )
        }.reversed()
    }

    private fun calculateStreakInfo(data: List<ContributionDay>): Pair<Int, Int> {
        var currentStreak = 0
        var longestStreak = 0
        var tempStreak = 0

        data.sortedByDescending { it.date }.forEach { day ->
            if (day.count > 0) {
                tempStreak++
                if (currentStreak == 0) currentStreak = tempStreak
            } else {
                if (tempStreak > longestStreak) longestStreak = tempStreak
                tempStreak = 0
            }
        }
        if (tempStreak > longestStreak) longestStreak = tempStreak

        return Pair(currentStreak, longestStreak)
    }

    private fun loadCertificates() {
        viewModelScope.launch {
            // Mock certificate data
            val certificates = listOf(
                CertificateInfo(
                    id = "cert-1",
                    name = "Quantum Computing Foundation",
                    level = "Foundation",
                    issuedAt = LocalDate.now().minusMonths(6),
                    expiresAt = LocalDate.now().plusMonths(18),
                    verificationCode = "QCF-2024-001234",
                    linkedInShareUrl = "https://linkedin.com/share/cert/qcf-001234"
                ),
                CertificateInfo(
                    id = "cert-2",
                    name = "Quantum Algorithm Associate",
                    level = "Associate",
                    issuedAt = LocalDate.now().minusMonths(3),
                    expiresAt = LocalDate.now().plusMonths(21),
                    verificationCode = "QAA-2024-005678",
                    linkedInShareUrl = "https://linkedin.com/share/cert/qaa-005678"
                )
            )
            _uiState.value = _uiState.value.copy(certificates = certificates)
        }
    }

    private fun loadBadges() {
        viewModelScope.launch {
            careerRepository.getBadges()
                .onSuccess { badgeCollection ->
                    val badges = badgeCollection.badges
                    val earned = badges.filter { it.earned }
                    val inProgress = badges.filter { !it.earned }.map { badge ->
                        val requirements = badge.tier.requirements
                        ProfileBadgeProgress(
                            badge = badge,
                            currentProgress = badge.progress?.percentage?.toInt() ?: 0,
                            targetProgress = 100
                        )
                    }
                    _uiState.value = _uiState.value.copy(
                        earnedBadges = earned,
                        inProgressBadges = inProgress
                    )
                }
        }
    }

    private fun loadCircuits() {
        viewModelScope.launch {
            careerRepository.getCircuits()
                .onSuccess { circuits ->
                    _uiState.value = _uiState.value.copy(
                        publishedCircuits = circuits.filter { it.status == CircuitStatus.PUBLISHED }
                    )
                }
        }
    }

    private fun loadPortfolio() {
        viewModelScope.launch {
            // Mock skill breakdown data
            val skillBreakdown = SkillBreakdown(
                logic = 0.85f,
                innovation = 0.72f,
                contribution = 0.68f,
                stability = 0.91f,
                speed = 0.76f
            )
            _uiState.value = _uiState.value.copy(skillBreakdown = skillBreakdown)
        }
    }

    fun exportPdfPortfolio() {
        // TODO: Implement PDF export
    }

    fun shareProfile() {
        // TODO: Implement profile sharing
    }

    fun shareOnLinkedIn(certificateId: String) {
        // TODO: Implement LinkedIn sharing
    }
}
