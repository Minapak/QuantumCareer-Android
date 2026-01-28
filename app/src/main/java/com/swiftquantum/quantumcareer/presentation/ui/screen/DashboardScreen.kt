package com.swiftquantum.quantumcareer.presentation.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.swiftquantum.quantumcareer.R
import com.swiftquantum.quantumcareer.domain.model.ActivityItem
import com.swiftquantum.quantumcareer.domain.model.ActivityType
import com.swiftquantum.quantumcareer.presentation.ui.component.*
import com.swiftquantum.quantumcareer.presentation.viewmodel.DashboardViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToCircuits: () -> Unit,
    onNavigateToReviews: () -> Unit,
    onNavigateToBadges: () -> Unit,
    onNavigateToCitations: () -> Unit,
    onNavigateToJobs: () -> Unit = {},
    onNavigateToJobDetail: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.dashboard)) },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.refresh))
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
            uiState.stats != null -> {
                val stats = uiState.stats!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Welcome Section with Badge
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.career_overview),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                CurrentBadgeDisplay(tier = stats.currentBadgeTier)
                            }

                            stats.nextBadgeProgress?.let { progress ->
                                CircularProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.size(48.dp),
                                    strokeWidth = 4.dp
                                )
                            }
                        }
                    }

                    // Quick Stats Cards (iOS-style)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickStatCard(
                            title = stringResource(R.string.best_score),
                            value = "85%",
                            icon = Icons.Default.EmojiEvents,
                            color = Color(0xFFFFD700),
                            modifier = Modifier.weight(1f)
                        )
                        QuickStatCard(
                            title = stringResource(R.string.global_rank),
                            value = "#${stats.hIndex * 10}",
                            icon = Icons.Default.Leaderboard,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f)
                        )
                        QuickStatCard(
                            title = stringResource(R.string.certificates),
                            value = "${stats.totalPublications / 5}",
                            icon = Icons.Default.Verified,
                            color = Color(0xFF2196F3),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Industry Readiness Score
                    IndustryReadinessCard(
                        score = 72,
                        level = "Intermediate",
                        evidenceBreakdown = mapOf(
                            stringResource(R.string.certifications) to 25,
                            stringResource(R.string.publications) to 20,
                            stringResource(R.string.peer_review) to 15,
                            stringResource(R.string.quiz) to 12
                        )
                    )

                    // Featured Jobs Section
                    FeaturedJobsSection(
                        onSeeAll = onNavigateToJobs,
                        onJobClick = onNavigateToJobDetail
                    )

                    // Stats Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = stringResource(R.string.publications),
                            value = stats.totalPublications.toString(),
                            modifier = Modifier.weight(1f),
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Article,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                        StatCard(
                            title = stringResource(R.string.citations),
                            value = stats.totalCitations.toString(),
                            modifier = Modifier.weight(1f),
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.FormatQuote,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = stringResource(R.string.h_index),
                            value = stats.hIndex.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = stringResource(R.string.i10_index),
                            value = stats.i10Index.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Pending Reviews Card
                    if (stats.pendingReviews > 0) {
                        Card(
                            onClick = onNavigateToReviews,
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.RateReview,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = stringResource(R.string.pending_reviews),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${stats.pendingReviews} ${stringResource(R.string.circuits_waiting)}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null
                                )
                            }
                        }
                    }

                    // Quick Actions
                    SectionHeader(title = stringResource(R.string.quick_actions))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionCard(
                            icon = Icons.Default.Article,
                            title = stringResource(R.string.circuits),
                            onClick = onNavigateToCircuits,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionCard(
                            icon = Icons.Default.EmojiEvents,
                            title = stringResource(R.string.badges),
                            onClick = onNavigateToBadges,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionCard(
                            icon = Icons.Default.Analytics,
                            title = stringResource(R.string.citations),
                            onClick = onNavigateToCitations,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Recent Activity with Expandable Detail
                    if (stats.recentActivity.isNotEmpty()) {
                        SectionHeader(
                            title = stringResource(R.string.recent_activity),
                            action = {
                                TextButton(onClick = { }) {
                                    Text(stringResource(R.string.see_all))
                                }
                            }
                        )

                        stats.recentActivity.take(5).forEach { activity ->
                            ExpandableActivityItemCard(activity = activity)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = color
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun IndustryReadinessCard(
    score: Int,
    level: String,
    evidenceBreakdown: Map<String, Int>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.industry_readiness_score),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = level,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$score",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.evidence_breakdown),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            evidenceBreakdown.forEach { (category, points) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "+$points pts",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun FeaturedJobsSection(
    onSeeAll: () -> Unit,
    onJobClick: (String) -> Unit
) {
    Column {
        SectionHeader(
            title = stringResource(R.string.featured_jobs),
            action = {
                TextButton(onClick = onSeeAll) {
                    Text(stringResource(R.string.see_all))
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Mock featured jobs
            items(listOf(
                FeaturedJobData("1", "Quantum Software Engineer", "IBM", "Remote", 92),
                FeaturedJobData("2", "Research Scientist", "Google", "Mountain View", 87),
                FeaturedJobData("3", "Quantum Algorithm Developer", "Microsoft", "Hybrid", 85)
            )) { job ->
                FeaturedJobCard(
                    title = job.title,
                    company = job.company,
                    location = job.location,
                    matchScore = job.matchScore,
                    onClick = { onJobClick(job.id) }
                )
            }
        }
    }
}

private data class FeaturedJobData(
    val id: String,
    val title: String,
    val company: String,
    val location: String,
    val matchScore: Int
)

@Composable
private fun FeaturedJobCard(
    title: String,
    company: String,
    location: String,
    matchScore: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(260.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = company,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                MatchScoreBadge(score = matchScore)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = location,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MatchScoreBadge(score: Int) {
    val color = when {
        score >= 90 -> Color(0xFF4CAF50)
        score >= 75 -> Color(0xFF8BC34A)
        score >= 60 -> Color(0xFFFFEB3B)
        else -> Color(0xFFFF9800)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = "$score%",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
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
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun ExpandableActivityItemCard(
    activity: ActivityItem,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val icon = when (activity.type) {
        ActivityType.CIRCUIT_PUBLISHED -> Icons.Default.Publish
        ActivityType.CIRCUIT_CITED -> Icons.Default.FormatQuote
        ActivityType.REVIEW_COMPLETED -> Icons.Default.RateReview
        ActivityType.BADGE_EARNED -> Icons.Default.EmojiEvents
        ActivityType.OFFER_RECEIVED -> Icons.Default.Mail
        ActivityType.PROFILE_VIEWED -> Icons.Default.Visibility
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = activity.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = activity.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = if (expanded) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = activity.createdAt.format(DateTimeFormatter.ofPattern("MMM d")),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // Expanded details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = activity.createdAt.format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a")),
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { /* TODO: View details */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.view_details))
                }
            }
        }
    }
}
