package com.swiftquantum.quantumcareer.presentation.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val selectedLanguage: String = "en",  // Default to English
    val onboardingCompleted: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    companion object {
        private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
        private val SELECTED_LANGUAGE_KEY = stringPreferencesKey("selected_language")
    }

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        checkOnboardingStatus()
    }

    private fun checkOnboardingStatus() {
        viewModelScope.launch {
            val prefs = dataStore.data.first()
            val completed = prefs[ONBOARDING_COMPLETED_KEY] ?: false
            val language = prefs[SELECTED_LANGUAGE_KEY] ?: "en"

            // Apply saved language
            if (completed && language.isNotEmpty()) {
                val localeList = LocaleListCompat.forLanguageTags(language)
                AppCompatDelegate.setApplicationLocales(localeList)
            }

            _uiState.update {
                it.copy(
                    onboardingCompleted = completed,
                    selectedLanguage = language,
                    isLoading = false
                )
            }
        }
    }

    fun selectLanguage(languageCode: String) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[SELECTED_LANGUAGE_KEY] = languageCode
            }

            // Apply language immediately
            val localeList = LocaleListCompat.forLanguageTags(languageCode)
            AppCompatDelegate.setApplicationLocales(localeList)

            _uiState.update { it.copy(selectedLanguage = languageCode) }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[ONBOARDING_COMPLETED_KEY] = true
            }
            _uiState.update { it.copy(onboardingCompleted = true) }
        }
    }

    fun resetOnboarding() {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[ONBOARDING_COMPLETED_KEY] = false
            }
            _uiState.update {
                OnboardingUiState(
                    isLoading = false,
                    onboardingCompleted = false
                )
            }
        }
    }
}
