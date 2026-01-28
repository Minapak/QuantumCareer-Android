package com.swiftquantum.quantumcareer.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftquantum.quantumcareer.data.auth.SharedAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class SettingsUiState(
    val isLoading: Boolean = false,
    val userName: String? = null,
    val userEmail: String? = null,
    val userAvatarUrl: String? = null,
    val isLoggedIn: Boolean = false,
    val selectedLanguage: AppLanguage = AppLanguage.ENGLISH,
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val emailNotificationsEnabled: Boolean = true,
    val pushNotificationsEnabled: Boolean = true,
    val linkedInConnected: Boolean = false,
    val subscriptionTier: SubscriptionTier = SubscriptionTier.FREE,
    val appVersion: String = "1.0.0",
    val showLogoutDialog: Boolean = false,
    val showLanguageDialog: Boolean = false,
    val error: String? = null
)

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val flag: String
) {
    ENGLISH("en", "English", "English", "US"),
    KOREAN("ko", "Korean", "한국어", "KR"),
    JAPANESE("ja", "Japanese", "日本語", "JP"),
    CHINESE("zh", "Chinese", "中文", "CN"),
    GERMAN("de", "German", "Deutsch", "DE");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code == code } ?: ENGLISH
        }
    }
}

enum class SubscriptionTier {
    FREE,
    PRO,
    ENTERPRISE;

    val displayName: String
        get() = when (this) {
            FREE -> "Free"
            PRO -> "Pro"
            ENTERPRISE -> "Enterprise"
        }

    val features: List<String>
        get() = when (this) {
            FREE -> listOf(
                "Basic job search",
                "5 applications per month",
                "Standard profile"
            )
            PRO -> listOf(
                "Unlimited job search",
                "Unlimited applications",
                "AI job matching",
                "Priority support",
                "Advanced analytics"
            )
            ENTERPRISE -> listOf(
                "Everything in Pro",
                "Team management",
                "Custom integrations",
                "Dedicated support"
            )
        }
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authManager: SharedAuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authManager.observeAuthState().collect { authData ->
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = authData.isLoggedIn,
                    userName = authData.userName,
                    userEmail = authData.userEmail,
                    userAvatarUrl = null // AuthData doesn't include avatar URL
                )
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // Load settings from SharedPreferences or DataStore
            val prefs = context.getSharedPreferences("quantum_career_settings", Context.MODE_PRIVATE)

            val languageCode = prefs.getString("language", Locale.getDefault().language) ?: "en"
            val isDarkMode = prefs.getBoolean("dark_mode", false)
            val notificationsEnabled = prefs.getBoolean("notifications_enabled", true)
            val emailNotifications = prefs.getBoolean("email_notifications", true)
            val pushNotifications = prefs.getBoolean("push_notifications", true)
            val linkedInConnected = prefs.getBoolean("linkedin_connected", false)
            val subscriptionTier = prefs.getString("subscription_tier", "FREE") ?: "FREE"

            val packageInfo = try {
                context.packageManager.getPackageInfo(context.packageName, 0)
            } catch (e: Exception) {
                null
            }

            _uiState.value = _uiState.value.copy(
                selectedLanguage = AppLanguage.fromCode(languageCode),
                isDarkMode = isDarkMode,
                notificationsEnabled = notificationsEnabled,
                emailNotificationsEnabled = emailNotifications,
                pushNotificationsEnabled = pushNotifications,
                linkedInConnected = linkedInConnected,
                subscriptionTier = SubscriptionTier.valueOf(subscriptionTier),
                appVersion = packageInfo?.versionName ?: "1.0.0"
            )
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences("quantum_career_settings", Context.MODE_PRIVATE)
            prefs.edit().putString("language", language.code).apply()

            _uiState.value = _uiState.value.copy(
                selectedLanguage = language,
                showLanguageDialog = false
            )

            // Note: In a real app, you would also update the app's locale
            // and potentially restart the activity
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences("quantum_career_settings", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("dark_mode", enabled).apply()

            _uiState.value = _uiState.value.copy(isDarkMode = enabled)

            // Note: In a real app, you would also update the theme
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences("quantum_career_settings", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("notifications_enabled", enabled).apply()

            _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
        }
    }

    fun setEmailNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences("quantum_career_settings", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("email_notifications", enabled).apply()

            _uiState.value = _uiState.value.copy(emailNotificationsEnabled = enabled)
        }
    }

    fun setPushNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences("quantum_career_settings", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("push_notifications", enabled).apply()

            _uiState.value = _uiState.value.copy(pushNotificationsEnabled = enabled)
        }
    }

    fun connectLinkedIn() {
        viewModelScope.launch {
            // TODO: Implement LinkedIn OAuth flow
            val prefs = context.getSharedPreferences("quantum_career_settings", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("linkedin_connected", true).apply()

            _uiState.value = _uiState.value.copy(linkedInConnected = true)
        }
    }

    fun disconnectLinkedIn() {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences("quantum_career_settings", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("linkedin_connected", false).apply()

            _uiState.value = _uiState.value.copy(linkedInConnected = false)
        }
    }

    fun showLogoutDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showLogoutDialog = show)
    }

    fun showLanguageDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showLanguageDialog = show)
    }

    fun logout() {
        viewModelScope.launch {
            authManager.clearAuth()
            _uiState.value = _uiState.value.copy(
                showLogoutDialog = false,
                isLoggedIn = false,
                userName = null,
                userEmail = null,
                userAvatarUrl = null
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
