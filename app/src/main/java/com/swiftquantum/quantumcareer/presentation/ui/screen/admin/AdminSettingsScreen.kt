package com.swiftquantum.quantumcareer.presentation.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.swiftquantum.quantumcareer.R
import com.swiftquantum.quantumcareer.data.dto.AdminSettingsDto
import com.swiftquantum.quantumcareer.presentation.ui.theme.QuantumColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(
    navController: NavController,
    viewModel: AdminSettingsViewModel = hiltViewModel(),
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutConfirmation by remember { mutableStateOf(false) }
    var showClearCacheConfirmation by remember { mutableStateOf(false) }
    var showBackupConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadSettings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.admin_settings_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = QuantumColors.Surface
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = QuantumColors.Primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(QuantumColors.Background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Admin Account
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = QuantumColors.Surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(QuantumColors.Accent.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = QuantumColors.Accent,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Admin",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = QuantumColors.TextPrimary
                                )
                                Text(
                                    text = "admin@swiftquantum.io",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = QuantumColors.TextSecondary
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Surface(
                                        color = QuantumColors.Accent,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "Super Admin",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = QuantumColors.Surface,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // System Settings Section
                item {
                    Text(
                        text = stringResource(R.string.admin_system_settings),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = QuantumColors.TextSecondary
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = QuantumColors.Surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column {
                            SettingsToggleItem(
                                icon = Icons.Outlined.Construction,
                                title = stringResource(R.string.admin_maintenance_mode),
                                subtitle = stringResource(R.string.admin_maintenance_mode_desc),
                                checked = uiState.settings.maintenanceMode,
                                onCheckedChange = { viewModel.updateMaintenanceMode(it) }
                            )

                            HorizontalDivider(color = QuantumColors.Border)

                            SettingsToggleItem(
                                icon = Icons.Outlined.PersonAdd,
                                title = stringResource(R.string.admin_registration_enabled),
                                subtitle = stringResource(R.string.admin_registration_enabled_desc),
                                checked = uiState.settings.registrationEnabled,
                                onCheckedChange = { viewModel.updateRegistrationEnabled(it) }
                            )

                            HorizontalDivider(color = QuantumColors.Border)

                            SettingsToggleItem(
                                icon = Icons.Outlined.MarkEmailRead,
                                title = stringResource(R.string.admin_email_verification),
                                subtitle = stringResource(R.string.admin_email_verification_desc),
                                checked = uiState.settings.emailVerificationRequired,
                                onCheckedChange = { viewModel.updateEmailVerificationRequired(it) }
                            )

                            HorizontalDivider(color = QuantumColors.Border)

                            SettingsToggleItem(
                                icon = Icons.Outlined.Password,
                                title = stringResource(R.string.admin_strong_password),
                                subtitle = stringResource(R.string.admin_strong_password_desc),
                                checked = uiState.settings.requireStrongPassword,
                                onCheckedChange = { /* TODO */ }
                            )
                        }
                    }
                }

                // Security Settings
                item {
                    Text(
                        text = stringResource(R.string.admin_security_settings),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = QuantumColors.TextSecondary
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = QuantumColors.Surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column {
                            SettingsInfoItem(
                                icon = Icons.Outlined.LockPerson,
                                title = stringResource(R.string.admin_max_login_attempts),
                                value = "${uiState.settings.maxLoginAttempts}"
                            )

                            HorizontalDivider(color = QuantumColors.Border)

                            SettingsInfoItem(
                                icon = Icons.Outlined.Timer,
                                title = stringResource(R.string.admin_session_timeout),
                                value = "${uiState.settings.sessionTimeoutHours} ${stringResource(R.string.hours)}"
                            )

                            HorizontalDivider(color = QuantumColors.Border)

                            SettingsInfoItem(
                                icon = Icons.Outlined.Key,
                                title = stringResource(R.string.admin_min_password_length),
                                value = "${uiState.settings.minPasswordLength} ${stringResource(R.string.characters)}"
                            )
                        }
                    }
                }

                // Actions
                item {
                    Text(
                        text = stringResource(R.string.admin_actions),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = QuantumColors.TextSecondary
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = QuantumColors.Surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column {
                            SettingsActionItem(
                                icon = Icons.Outlined.Backup,
                                title = stringResource(R.string.admin_backup_database),
                                subtitle = stringResource(R.string.admin_backup_database_desc),
                                onClick = { showBackupConfirmation = true }
                            )

                            HorizontalDivider(color = QuantumColors.Border)

                            SettingsActionItem(
                                icon = Icons.Outlined.ClearAll,
                                title = stringResource(R.string.admin_clear_cache),
                                subtitle = stringResource(R.string.admin_clear_cache_desc),
                                onClick = { showClearCacheConfirmation = true }
                            )
                        }
                    }
                }

                // Logout Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = QuantumColors.Surface),
                        shape = RoundedCornerShape(12.dp),
                        onClick = { showLogoutConfirmation = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = null,
                                tint = QuantumColors.Error
                            )
                            Text(
                                text = stringResource(R.string.admin_exit_admin_mode),
                                style = MaterialTheme.typography.bodyLarge,
                                color = QuantumColors.Error
                            )
                        }
                    }
                }

                // Version Info
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "QuantumCareer Admin v5.6.0",
                            style = MaterialTheme.typography.labelSmall,
                            color = QuantumColors.TextTertiary
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            icon = { Icon(Icons.Default.Logout, contentDescription = null) },
            title = { Text(stringResource(R.string.admin_exit_admin_mode)) },
            text = { Text(stringResource(R.string.admin_exit_admin_mode_confirm)) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirmation = false
                        onLogout()
                        navController.navigateUp()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = QuantumColors.Error)
                ) {
                    Text(stringResource(R.string.admin_exit))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmation = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Clear Cache Confirmation
    if (showClearCacheConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearCacheConfirmation = false },
            icon = { Icon(Icons.Outlined.ClearAll, contentDescription = null) },
            title = { Text(stringResource(R.string.admin_clear_cache)) },
            text = { Text(stringResource(R.string.admin_clear_cache_confirm)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearCache()
                        showClearCacheConfirmation = false
                    }
                ) {
                    Text(stringResource(R.string.admin_clear))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheConfirmation = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Backup Confirmation
    if (showBackupConfirmation) {
        AlertDialog(
            onDismissRequest = { showBackupConfirmation = false },
            icon = { Icon(Icons.Outlined.Backup, contentDescription = null) },
            title = { Text(stringResource(R.string.admin_backup_database)) },
            text = { Text(stringResource(R.string.admin_backup_database_confirm)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.triggerBackup()
                        showBackupConfirmation = false
                    }
                ) {
                    Text(stringResource(R.string.admin_backup))
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackupConfirmation = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = QuantumColors.TextSecondary
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = QuantumColors.TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = QuantumColors.TextSecondary
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = QuantumColors.Primary,
                checkedTrackColor = QuantumColors.Primary.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
private fun SettingsInfoItem(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = QuantumColors.TextSecondary
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = QuantumColors.TextPrimary,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = QuantumColors.TextSecondary
        )
    }
}

@Composable
private fun SettingsActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = QuantumColors.Surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = QuantumColors.TextSecondary
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = QuantumColors.TextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = QuantumColors.TextSecondary
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = QuantumColors.TextTertiary
            )
        }
    }
}
