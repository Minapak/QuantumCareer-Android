package com.swiftquantum.quantumcareer.presentation.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.swiftquantum.quantumcareer.R
import com.swiftquantum.quantumcareer.data.dto.*
import com.swiftquantum.quantumcareer.presentation.ui.theme.QuantumColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = QuantumColors.Accent
                        )
                        Text(stringResource(R.string.admin_dashboard))
                    }
                },
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
                // Stats Cards Row
                item {
                    Text(
                        text = stringResource(R.string.admin_overview),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = QuantumColors.TextPrimary
                    )
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            StatCard(
                                title = stringResource(R.string.admin_total_users),
                                value = "${uiState.stats.totalUsers}",
                                subtitle = "+${uiState.stats.newUsersToday} ${stringResource(R.string.admin_today)}",
                                icon = Icons.Default.People,
                                iconColor = QuantumColors.Primary
                            )
                        }
                        item {
                            StatCard(
                                title = stringResource(R.string.admin_active_users),
                                value = "${uiState.stats.activeUsers}",
                                subtitle = "${stringResource(R.string.admin_this_week)}: ${uiState.stats.newUsersWeek}",
                                icon = Icons.Default.TrendingUp,
                                iconColor = QuantumColors.Accent
                            )
                        }
                        item {
                            StatCard(
                                title = stringResource(R.string.admin_total_quizzes),
                                value = "${uiState.stats.totalQuizzes}",
                                subtitle = "${stringResource(R.string.admin_today)}: ${uiState.stats.quizzesCompletedToday}",
                                icon = Icons.Default.Quiz,
                                iconColor = QuantumColors.Warning
                            )
                        }
                        item {
                            StatCard(
                                title = stringResource(R.string.admin_certificates),
                                value = "${uiState.stats.totalCertificates}",
                                subtitle = "${stringResource(R.string.admin_today)}: ${uiState.stats.certificatesIssuedToday}",
                                icon = Icons.Default.Verified,
                                iconColor = QuantumColors.Success
                            )
                        }
                    }
                }

                // System Health
                item {
                    Text(
                        text = stringResource(R.string.admin_system_health),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = QuantumColors.TextPrimary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    SystemHealthCard(health = uiState.systemHealth)
                }

                // Quick Actions
                item {
                    Text(
                        text = stringResource(R.string.admin_quick_actions),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = QuantumColors.TextPrimary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.admin_add_question),
                            icon = Icons.Default.Add,
                            onClick = { navController.navigate("admin_content") }
                        )
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.admin_add_job),
                            icon = Icons.Default.Work,
                            onClick = { navController.navigate("admin_content") }
                        )
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.admin_view_reports),
                            icon = Icons.Default.Assessment,
                            onClick = { /* TODO */ }
                        )
                    }
                }

                // Recent Activity
                item {
                    Text(
                        text = stringResource(R.string.admin_recent_activity),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = QuantumColors.TextPrimary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (uiState.recentActivity.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = QuantumColors.Surface)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.admin_no_recent_activity),
                                    color = QuantumColors.TextSecondary
                                )
                            }
                        }
                    }
                } else {
                    items(uiState.recentActivity.take(10)) { activity ->
                        ActivityItem(activity = activity)
                    }
                }

                // Navigation Cards
                item {
                    Text(
                        text = stringResource(R.string.admin_management),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = QuantumColors.TextPrimary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        AdminNavigationCard(
                            title = stringResource(R.string.admin_users_management),
                            subtitle = stringResource(R.string.admin_users_management_desc),
                            icon = Icons.Default.People,
                            onClick = { navController.navigate("admin_users") }
                        )
                        AdminNavigationCard(
                            title = stringResource(R.string.admin_content_management),
                            subtitle = stringResource(R.string.admin_content_management_desc),
                            icon = Icons.Default.Article,
                            onClick = { navController.navigate("admin_content") }
                        )
                        AdminNavigationCard(
                            title = stringResource(R.string.admin_settings_title),
                            subtitle = stringResource(R.string.admin_settings_desc),
                            icon = Icons.Default.Settings,
                            onClick = { navController.navigate("admin_settings") }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color
) {
    Card(
        modifier = Modifier.width(160.dp),
        colors = CardDefaults.cardColors(containerColor = QuantumColors.Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = QuantumColors.TextPrimary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = QuantumColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = QuantumColors.Accent
            )
        }
    }
}

@Composable
private fun SystemHealthCard(health: SystemHealthDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = QuantumColors.Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HealthIndicator(
                    label = "API",
                    status = health.apiStatus
                )
                HealthIndicator(
                    label = stringResource(R.string.admin_database),
                    status = health.databaseStatus
                )
                HealthIndicator(
                    label = stringResource(R.string.admin_storage),
                    status = health.storageStatus
                )
                HealthIndicator(
                    label = stringResource(R.string.admin_cache),
                    status = health.cacheStatus
                )
            }

            if (health.lastBackup != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Backup,
                        contentDescription = null,
                        tint = QuantumColors.TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${stringResource(R.string.admin_last_backup)}: ${health.lastBackup}",
                        style = MaterialTheme.typography.labelSmall,
                        color = QuantumColors.TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun HealthIndicator(
    label: String,
    status: String
) {
    val color = when (status.lowercase()) {
        "healthy", "ok", "online" -> QuantumColors.Success
        "degraded", "slow" -> QuantumColors.Warning
        else -> QuantumColors.Error
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = QuantumColors.TextSecondary
        )
    }
}

@Composable
private fun QuickActionButton(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = QuantumColors.Surface),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = QuantumColors.Primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = QuantumColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ActivityItem(activity: AdminActivityItemDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = QuantumColors.Surface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val icon = when (activity.type.lowercase()) {
                "user_registered" -> Icons.Default.PersonAdd
                "quiz_completed" -> Icons.Default.Quiz
                "certificate_issued" -> Icons.Default.Verified
                "job_posted" -> Icons.Default.Work
                "login" -> Icons.Default.Login
                else -> Icons.Default.Info
            }

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = QuantumColors.Primary,
                modifier = Modifier.size(20.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = QuantumColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (activity.username != null) {
                    Text(
                        text = activity.username,
                        style = MaterialTheme.typography.labelSmall,
                        color = QuantumColors.TextSecondary
                    )
                }
            }

            Text(
                text = activity.timestamp.take(10),
                style = MaterialTheme.typography.labelSmall,
                color = QuantumColors.TextTertiary
            )
        }
    }
}

@Composable
private fun AdminNavigationCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = QuantumColors.Surface),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
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
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(QuantumColors.Primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = QuantumColors.Primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
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
