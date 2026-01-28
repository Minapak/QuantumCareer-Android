package com.swiftquantum.quantumcareer.presentation.ui.screen.rankings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.swiftquantum.quantumcareer.domain.model.*
import com.swiftquantum.quantumcareer.presentation.ui.component.*
import com.swiftquantum.quantumcareer.presentation.ui.theme.*
import com.swiftquantum.quantumcareer.presentation.viewmodel.RankingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRankScreen(
    viewModel: RankingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadFriendsRankings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Ranking") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingView(modifier = Modifier.padding(paddingValues))
            }
            uiState.error != null -> {
                ErrorView(
                    message = uiState.error ?: "Unknown error",
                    onRetry = { viewModel.refresh() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Main rank card
                    uiState.userRankingStats?.let { stats ->
                        item {
                            MainRankCard(stats = stats)
                        }

                        // Rank change indicator
                        item {
                            RankChangeCard(stats = stats)
                        }

                        // Statistics grid
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                StatCard(
                                    title = "Tests Completed",
                                    value = stats.testsCompleted.toString(),
                                    modifier = Modifier.weight(1f),
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Quiz,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                )
                                StatCard(
                                    title = "Avg. Score",
                                    value = "${stats.averagePercentage.toInt()}%",
                                    modifier = Modifier.weight(1f),
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Analytics,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                )
                            }
                        }

                        // Country/Institution ranking
                        if (stats.rankInCountry != null || stats.rankInInstitution != null) {
                            item {
                                SectionHeader(title = "Local Rankings")
                            }

                            item {
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        stats.rankInCountry?.let { countryRank ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.Flag,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "Country Rank",
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }
                                                Text(
                                                    text = "#$countryRank of ${stats.countryTotal}",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        stats.rankInInstitution?.let { instRank ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.School,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "Institution Rank",
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }
                                                Text(
                                                    text = "#$instRank of ${stats.institutionTotal}",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Achievements
                    if (uiState.achievements.isNotEmpty()) {
                        item {
                            SectionHeader(title = "Ranking Achievements")
                        }

                        items(uiState.achievements) { achievement ->
                            AchievementCard(achievement = achievement)
                        }
                    }

                    // Friends ranking
                    uiState.friendsRanking?.let { friendsRanking ->
                        if (friendsRanking.friends.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "Friends Ranking",
                                    action = {
                                        Text(
                                            text = "You: #${friendsRanking.userRankAmongFriends}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                )
                            }

                            items(friendsRanking.friends.take(5)) { friend ->
                                FriendRankCard(
                                    friend = friend,
                                    isCurrentUser = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MainRankCard(stats: UserRankingStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Global Rank",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "#${stats.currentRank}",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stats.topPercentileText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${stats.totalScore}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Total Score",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "#${stats.bestRank}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Best Rank",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun RankChangeCard(stats: UserRankingStats) {
    val change = stats.rankChange
    val status = stats.rankChangeStatus

    val (icon, color, text) = when (status) {
        RankChangeStatus.UP -> Triple(
            Icons.Default.TrendingUp,
            StatusPublished,
            "Up ${change} positions"
        )
        RankChangeStatus.DOWN -> Triple(
            Icons.Default.TrendingDown,
            StatusRejected,
            "Down ${kotlin.math.abs(change ?: 0)} positions"
        )
        RankChangeStatus.SAME -> Triple(
            Icons.Default.TrendingFlat,
            StatusUnderReview,
            "No change"
        )
        RankChangeStatus.NEW -> Triple(
            Icons.Default.NewReleases,
            MaterialTheme.colorScheme.primary,
            "New entry!"
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = "Since last week",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AchievementCard(achievement: RankingAchievement) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isAchieved) {
                BadgeGold.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (achievement.isAchieved) {
                    Icons.Default.EmojiEvents
                } else {
                    Icons.Default.Lock
                },
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (achievement.isAchieved) BadgeGold else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!achievement.isAchieved && achievement.progress > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { achievement.progress },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        text = "${(achievement.progress * 100).toInt()}% complete",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (achievement.isAchieved) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Achieved",
                    tint = StatusPublished
                )
            }
        }
    }
}

@Composable
private fun FriendRankCard(
    friend: RankedUser,
    isCurrentUser: Boolean
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
            Text(
                text = "#${friend.rank}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(40.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${friend.bestPercentage.toInt()}% best score",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            friend.highestBadge?.let { badge ->
                BadgeIcon(
                    tier = badge,
                    earned = true,
                    size = 24
                )
            }
        }
    }
}
