package com.swiftquantum.quantumcareer.presentation.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.swiftquantum.quantumcareer.R
import com.swiftquantum.quantumcareer.data.dto.AdminUserItemDto
import com.swiftquantum.quantumcareer.presentation.ui.theme.QuantumColors

enum class UserFilter {
    ALL, ACTIVE, PRO, ADMIN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
    navController: NavController,
    viewModel: AdminUsersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(UserFilter.ALL) }
    var showUserDetail by remember { mutableStateOf<AdminUserItemDto?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.admin_users_title)) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(QuantumColors.Background)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.searchUsers(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text(stringResource(R.string.admin_search_users)) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            viewModel.searchUsers("")
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.clear))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = QuantumColors.Primary,
                    unfocusedBorderColor = QuantumColors.Border
                )
            )

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == UserFilter.ALL,
                    onClick = {
                        selectedFilter = UserFilter.ALL
                        viewModel.filterUsers(null)
                    },
                    label = { Text(stringResource(R.string.admin_filter_all)) }
                )
                FilterChip(
                    selected = selectedFilter == UserFilter.ACTIVE,
                    onClick = {
                        selectedFilter = UserFilter.ACTIVE
                        viewModel.filterUsers("active")
                    },
                    label = { Text(stringResource(R.string.admin_filter_active)) }
                )
                FilterChip(
                    selected = selectedFilter == UserFilter.PRO,
                    onClick = {
                        selectedFilter = UserFilter.PRO
                        viewModel.filterUsers("pro")
                    },
                    label = { Text(stringResource(R.string.admin_filter_pro)) }
                )
                FilterChip(
                    selected = selectedFilter == UserFilter.ADMIN,
                    onClick = {
                        selectedFilter = UserFilter.ADMIN
                        viewModel.filterUsers("admin")
                    },
                    label = { Text(stringResource(R.string.admin_filter_admin)) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // User List
            if (uiState.isLoading && uiState.users.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = QuantumColors.Primary)
                }
            } else if (uiState.users.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonOff,
                            contentDescription = null,
                            tint = QuantumColors.TextTertiary,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = stringResource(R.string.admin_users_empty),
                            color = QuantumColors.TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.filteredUsers) { user ->
                        UserCard(
                            user = user,
                            onClick = { showUserDetail = user }
                        )
                    }

                    if (uiState.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = QuantumColors.Primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // User Detail Dialog
    showUserDetail?.let { user ->
        UserDetailDialog(
            user = user,
            onDismiss = { showUserDetail = null },
            onGrantPremium = { tier, days ->
                viewModel.grantPremium(user.id, tier, days)
                showUserDetail = null
            },
            onBanUser = {
                viewModel.banUser(user.id)
                showUserDetail = null
            }
        )
    }
}

@Composable
private fun UserCard(
    user: AdminUserItemDto,
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
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(QuantumColors.Primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.displayNameOrUsername.first().uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = QuantumColors.Primary
                )
            }

            // User Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = user.displayNameOrUsername,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = QuantumColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (user.isAdmin == true) {
                        Surface(
                            color = QuantumColors.Gold,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Admin",
                                style = MaterialTheme.typography.labelSmall,
                                color = QuantumColors.Surface,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    } else if (user.isPro) {
                        Surface(
                            color = QuantumColors.Accent,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "PRO",
                                style = MaterialTheme.typography.labelSmall,
                                color = QuantumColors.Surface,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = user.email ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = QuantumColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Status Indicator
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (user.isActive == true) QuantumColors.Success
                        else QuantumColors.TextTertiary
                    )
            )
        }
    }
}

@Composable
private fun UserDetailDialog(
    user: AdminUserItemDto,
    onDismiss: () -> Unit,
    onGrantPremium: (String, Int) -> Unit,
    onBanUser: () -> Unit
) {
    var showGrantPremiumOptions by remember { mutableStateOf(false) }
    var showBanConfirmation by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(QuantumColors.Primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.displayNameOrUsername.first().uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = QuantumColors.Primary
                    )
                }
                Column {
                    Text(
                        text = user.displayNameOrUsername,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = user.email ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = QuantumColors.TextSecondary
                    )
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(label = "XP", value = "${user.totalXp ?: 0}")
                    StatItem(label = stringResource(R.string.level), value = "${user.currentLevel ?: 1}")
                    StatItem(label = stringResource(R.string.streak), value = "${user.currentStreak ?: 0}")
                }

                HorizontalDivider()

                // Subscription Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.admin_subscription),
                        color = QuantumColors.TextSecondary
                    )
                    Text(
                        text = user.subscriptionType?.replaceFirstChar { it.uppercase() } ?: "Free",
                        fontWeight = FontWeight.Medium
                    )
                }

                if (user.createdAt != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.admin_joined),
                            color = QuantumColors.TextSecondary
                        )
                        Text(
                            text = user.createdAt.take(10)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Grant Premium Button
                OutlinedButton(
                    onClick = { showGrantPremiumOptions = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Star, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.admin_grant_premium))
                }

                // Ban Button
                OutlinedButton(
                    onClick = { showBanConfirmation = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = QuantumColors.Error
                    )
                ) {
                    Icon(Icons.Default.Block, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.admin_ban_user))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )

    // Grant Premium Options Dialog
    if (showGrantPremiumOptions) {
        AlertDialog(
            onDismissRequest = { showGrantPremiumOptions = false },
            title = { Text(stringResource(R.string.admin_grant_premium)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "pro" to "Pro (30 days)",
                        "scholar" to "Scholar (30 days)",
                        "career" to "Career (30 days)"
                    ).forEach { (tier, label) ->
                        OutlinedButton(
                            onClick = {
                                onGrantPremium(tier, 30)
                                showGrantPremiumOptions = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showGrantPremiumOptions = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Ban Confirmation Dialog
    if (showBanConfirmation) {
        AlertDialog(
            onDismissRequest = { showBanConfirmation = false },
            title = { Text(stringResource(R.string.admin_ban_user)) },
            text = {
                Text(stringResource(R.string.admin_ban_user_confirm))
            },
            confirmButton = {
                Button(
                    onClick = {
                        onBanUser()
                        showBanConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = QuantumColors.Error
                    )
                ) {
                    Text(stringResource(R.string.admin_ban_user))
                }
            },
            dismissButton = {
                TextButton(onClick = { showBanConfirmation = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = QuantumColors.TextPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = QuantumColors.TextSecondary
        )
    }
}
