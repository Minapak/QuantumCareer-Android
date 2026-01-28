package com.swiftquantum.quantumcareer.presentation.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.swiftquantum.quantumcareer.R
import com.swiftquantum.quantumcareer.presentation.viewmodel.AppLanguage
import com.swiftquantum.quantumcareer.presentation.viewmodel.SettingsViewModel
import com.swiftquantum.quantumcareer.presentation.viewmodel.SubscriptionTier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onNavigateToEditProfile: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Account Header
            AccountHeader(
                isLoggedIn = uiState.isLoggedIn,
                userName = uiState.userName,
                userEmail = uiState.userEmail,
                avatarUrl = uiState.userAvatarUrl,
                onSignIn = onNavigateToAuth,
                onEditProfile = onNavigateToEditProfile,
                modifier = Modifier.padding(16.dp)
            )

            // Account Section
            if (uiState.isLoggedIn) {
                SettingsSection(title = stringResource(R.string.account)) {
                    SettingsItem(
                        icon = Icons.Default.Person,
                        title = stringResource(R.string.edit_profile),
                        onClick = onNavigateToEditProfile
                    )
                    SettingsItem(
                        icon = Icons.Default.Security,
                        title = stringResource(R.string.account_security),
                        onClick = { /* TODO */ }
                    )
                }
            }

            // Notifications Section
            SettingsSection(title = stringResource(R.string.notifications)) {
                SettingsSwitchItem(
                    icon = Icons.Default.Notifications,
                    title = stringResource(R.string.notifications),
                    subtitle = stringResource(R.string.notifications_subtitle),
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = { viewModel.setNotificationsEnabled(it) }
                )

                if (uiState.notificationsEnabled) {
                    SettingsSwitchItem(
                        icon = Icons.Default.Email,
                        title = stringResource(R.string.email_notifications),
                        subtitle = stringResource(R.string.email_notifications_subtitle),
                        checked = uiState.emailNotificationsEnabled,
                        onCheckedChange = { viewModel.setEmailNotificationsEnabled(it) }
                    )
                    SettingsSwitchItem(
                        icon = Icons.Default.PhoneAndroid,
                        title = stringResource(R.string.push_notifications),
                        subtitle = stringResource(R.string.push_notifications_subtitle),
                        checked = uiState.pushNotificationsEnabled,
                        onCheckedChange = { viewModel.setPushNotificationsEnabled(it) }
                    )
                }
            }

            // Appearance Section
            SettingsSection(title = stringResource(R.string.appearance)) {
                SettingsItem(
                    icon = Icons.Default.Language,
                    title = stringResource(R.string.language),
                    subtitle = "${uiState.selectedLanguage.nativeName} (${uiState.selectedLanguage.displayName})",
                    onClick = { viewModel.showLanguageDialog(true) }
                )
                SettingsSwitchItem(
                    icon = Icons.Default.DarkMode,
                    title = stringResource(R.string.dark_mode),
                    subtitle = stringResource(R.string.dark_mode_subtitle),
                    checked = uiState.isDarkMode,
                    onCheckedChange = { viewModel.setDarkMode(it) }
                )
            }

            // Subscription Section
            SettingsSection(title = stringResource(R.string.subscription)) {
                SubscriptionCard(
                    currentTier = uiState.subscriptionTier,
                    onUpgrade = { /* TODO: Navigate to subscription screen */ }
                )
            }

            // Integrations Section
            SettingsSection(title = stringResource(R.string.integrations)) {
                SettingsItem(
                    icon = Icons.Default.Link,
                    title = "LinkedIn",
                    subtitle = if (uiState.linkedInConnected)
                        stringResource(R.string.connected)
                    else
                        stringResource(R.string.not_connected),
                    trailing = {
                        if (uiState.linkedInConnected) {
                            TextButton(onClick = { viewModel.disconnectLinkedIn() }) {
                                Text(stringResource(R.string.disconnect), color = MaterialTheme.colorScheme.error)
                            }
                        } else {
                            Button(onClick = { viewModel.connectLinkedIn() }) {
                                Text(stringResource(R.string.connect))
                            }
                        }
                    },
                    onClick = { }
                )
            }

            // Support Section
            SettingsSection(title = stringResource(R.string.support)) {
                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.Help,
                    title = stringResource(R.string.help_faq),
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://swiftquantum.com/help"))
                        context.startActivity(intent)
                    }
                )
                SettingsItem(
                    icon = Icons.Default.Feedback,
                    title = stringResource(R.string.send_feedback),
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:support@swiftquantum.com")
                            putExtra(Intent.EXTRA_SUBJECT, "QuantumCareer Feedback")
                        }
                        context.startActivity(intent)
                    }
                )
            }

            // About Section
            SettingsSection(title = stringResource(R.string.about)) {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = stringResource(R.string.app_version),
                    subtitle = uiState.appVersion,
                    onClick = { }
                )
                SettingsItem(
                    icon = Icons.Default.Description,
                    title = stringResource(R.string.privacy_policy),
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://swiftquantum.com/privacy"))
                        context.startActivity(intent)
                    }
                )
                SettingsItem(
                    icon = Icons.Default.Gavel,
                    title = stringResource(R.string.terms_of_service),
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://swiftquantum.com/terms"))
                        context.startActivity(intent)
                    }
                )
            }

            // Logout Button
            if (uiState.isLoggedIn) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.showLogoutDialog(true) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.logout))
                }
            }

            // App Tagline
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.app_tagline),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Logout Confirmation Dialog
        if (uiState.showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.showLogoutDialog(false) },
                title = { Text(stringResource(R.string.logout)) },
                text = { Text(stringResource(R.string.logout_confirmation)) },
                confirmButton = {
                    Button(
                        onClick = { viewModel.logout() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(stringResource(R.string.logout))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.showLogoutDialog(false) }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }

        // Language Selection Dialog
        if (uiState.showLanguageDialog) {
            LanguageSelectionDialog(
                currentLanguage = uiState.selectedLanguage,
                onLanguageSelected = { viewModel.setLanguage(it) },
                onDismiss = { viewModel.showLanguageDialog(false) }
            )
        }
    }
}

@Composable
private fun AccountHeader(
    isLoggedIn: Boolean,
    userName: String?,
    userEmail: String?,
    avatarUrl: String?,
    onSignIn: () -> Unit,
    onEditProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoggedIn) {
                // Avatar
                if (avatarUrl != null) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (userName?.firstOrNull()?.uppercase() ?: "U"),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = userName ?: "User",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                userEmail?.let { email ->
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(onClick = onEditProfile) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.edit_profile))
                }
            } else {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.guest_mode),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = stringResource(R.string.sign_in_to_sync),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onSignIn) {
                    Text(stringResource(R.string.sign_in))
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(content = content)
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (trailing != null) {
            trailing()
        } else {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SubscriptionCard(
    currentTier: SubscriptionTier,
    onUpgrade: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = when (currentTier) {
                        SubscriptionTier.FREE -> MaterialTheme.colorScheme.onSurfaceVariant
                        SubscriptionTier.PRO -> MaterialTheme.colorScheme.primary
                        SubscriptionTier.ENTERPRISE -> MaterialTheme.colorScheme.tertiary
                    }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(R.string.current_plan),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = currentTier.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (currentTier == SubscriptionTier.FREE) {
                Button(onClick = onUpgrade) {
                    Text(stringResource(R.string.upgrade_to_pro))
                }
            }
        }

        if (currentTier == SubscriptionTier.FREE) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.pro_features_intro),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            SubscriptionTier.PRO.features.take(3).forEach { feature ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageSelectionDialog(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_language)) },
        text = {
            Column {
                AppLanguage.entries.forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(language) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = getFlagEmoji(language.flag),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = language.nativeName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (language == currentLanguage) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = language.displayName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (language == currentLanguage) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

private fun getFlagEmoji(countryCode: String): String {
    val firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
    val secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6
    return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
}
