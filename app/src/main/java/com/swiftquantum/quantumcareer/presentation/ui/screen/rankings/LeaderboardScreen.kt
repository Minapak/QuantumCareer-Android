package com.swiftquantum.quantumcareer.presentation.ui.screen.rankings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.swiftquantum.quantumcareer.domain.model.*
import com.swiftquantum.quantumcareer.presentation.ui.component.*
import com.swiftquantum.quantumcareer.presentation.ui.theme.*
import com.swiftquantum.quantumcareer.presentation.viewmodel.RankingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: RankingsViewModel = hiltViewModel(),
    onNavigateToMyRank: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilterSheet by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Load more when reaching end of list
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                val totalItems = uiState.leaderboard?.entries?.size ?: 0
                if (lastVisibleIndex != null && lastVisibleIndex >= totalItems - 5) {
                    viewModel.loadMoreEntries()
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Global Rankings") },
                actions = {
                    IconButton(onClick = { showFilterSheet = true }) {
                        Badge(
                            modifier = Modifier.size(8.dp),
                            containerColor = if (uiState.selectedCountry != null || uiState.selectedInstitution != null) {
                                MaterialTheme.colorScheme.primary
                            } else Color.Transparent
                        ) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter")
                        }
                    }
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToMyRank,
                icon = { Icon(Icons.Default.Person, contentDescription = null) },
                text = { Text("My Rank") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Ranking type tabs
            ScrollableTabRow(
                selectedTabIndex = RankingType.entries.indexOf(uiState.selectedRankingType),
                modifier = Modifier.fillMaxWidth()
            ) {
                RankingType.entries.forEach { type ->
                    Tab(
                        selected = uiState.selectedRankingType == type,
                        onClick = { viewModel.selectRankingType(type) },
                        text = { Text(type.displayName) }
                    )
                }
            }

            when {
                uiState.isLoading && uiState.leaderboard == null -> {
                    LoadingView()
                }
                uiState.error != null && uiState.leaderboard == null -> {
                    ErrorView(
                        message = uiState.error ?: "Unknown error",
                        onRetry = { viewModel.refresh() }
                    )
                }
                uiState.leaderboard != null -> {
                    val leaderboard = uiState.leaderboard!!

                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Top 3 podium
                        if (leaderboard.topThree.isNotEmpty()) {
                            item {
                                TopThreePodium(
                                    topThree = leaderboard.topThree,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        // Stats
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${leaderboard.totalParticipants} participants",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (leaderboard.isFiltered) {
                                    TextButton(onClick = { viewModel.clearFilters() }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Clear filters")
                                    }
                                }
                            }
                        }

                        // User's rank if not in top entries
                        leaderboard.userRank?.let { userRank ->
                            if (userRank.rank > 50) {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = "Your Position",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            RankedUserRow(
                                                user = userRank,
                                                isCurrentUser = true
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Rest of the list (4th place onwards)
                        items(leaderboard.restOfList) { user ->
                            val isCurrentUser = user.userId == leaderboard.userRank?.userId
                            RankedUserRow(
                                user = user,
                                isCurrentUser = isCurrentUser
                            )
                        }

                        // Loading more indicator
                        if (uiState.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Filter bottom sheet
    if (showFilterSheet) {
        FilterBottomSheet(
            countries = uiState.countries,
            institutions = uiState.institutions,
            selectedCountry = uiState.selectedCountry,
            selectedInstitution = uiState.selectedInstitution,
            minBadge = uiState.minBadge,
            onCountrySelected = { viewModel.selectCountry(it) },
            onInstitutionSelected = { viewModel.selectInstitution(it) },
            onMinBadgeSelected = { viewModel.setMinBadge(it) },
            onDismiss = { showFilterSheet = false }
        )
    }
}

@Composable
private fun TopThreePodium(
    topThree: List<RankedUser>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2nd place
        if (topThree.size > 1) {
            PodiumItem(
                user = topThree[1],
                podiumHeight = 80.dp,
                medalColor = BadgeSilver
            )
        }

        // 1st place
        if (topThree.isNotEmpty()) {
            PodiumItem(
                user = topThree[0],
                podiumHeight = 100.dp,
                medalColor = BadgeGold
            )
        }

        // 3rd place
        if (topThree.size > 2) {
            PodiumItem(
                user = topThree[2],
                podiumHeight = 60.dp,
                medalColor = BadgeBronze
            )
        }
    }
}

@Composable
private fun PodiumItem(
    user: RankedUser,
    podiumHeight: androidx.compose.ui.unit.Dp,
    medalColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        // Avatar
        Box(contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            // Medal
            Surface(
                shape = CircleShape,
                color = medalColor,
                modifier = Modifier.size(24.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.rank.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = user.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            textAlign = TextAlign.Center
        )

        Text(
            text = user.formattedScore,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Podium
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(podiumHeight)
                .background(
                    color = medalColor.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                )
        )
    }
}

@Composable
private fun RankedUserRow(
    user: RankedUser,
    isCurrentUser: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentUser) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Text(
                text = "#${user.rank}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(48.dp),
                color = when {
                    user.rank <= 10 -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            // Avatar
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Name and info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (isCurrentUser) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "(You)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    user.countryCode?.let { code ->
                        Text(
                            text = code,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    user.institution?.let { institution ->
                        Text(
                            text = institution.take(20),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Badges
            user.highestBadge?.let { badge ->
                BadgeIcon(
                    tier = badge,
                    earned = true,
                    size = 28
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Score
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = user.formattedScore,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${user.bestPercentage.toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    countries: List<RankingCountry>,
    institutions: List<RankingInstitution>,
    selectedCountry: String?,
    selectedInstitution: String?,
    minBadge: BadgeTier?,
    onCountrySelected: (String?) -> Unit,
    onInstitutionSelected: (String?) -> Unit,
    onMinBadgeSelected: (BadgeTier?) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Filter Rankings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Country filter
            Text(
                text = "Country",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            var countryExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = countryExpanded,
                onExpandedChange = { countryExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedCountry ?: "All Countries",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = countryExpanded,
                    onDismissRequest = { countryExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All Countries") },
                        onClick = {
                            onCountrySelected(null)
                            countryExpanded = false
                        }
                    )
                    countries.forEach { country ->
                        DropdownMenuItem(
                            text = {
                                Row {
                                    country.flagEmoji?.let { Text(it) }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(country.name)
                                }
                            },
                            onClick = {
                                onCountrySelected(country.code)
                                countryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Minimum badge filter
            Text(
                text = "Minimum Badge",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = minBadge == null,
                    onClick = { onMinBadgeSelected(null) },
                    label = { Text("All") }
                )
                BadgeTier.entries.forEach { badge ->
                    FilterChip(
                        selected = minBadge == badge,
                        onClick = { onMinBadgeSelected(badge) },
                        label = { Text(badge.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Apply Filters")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
