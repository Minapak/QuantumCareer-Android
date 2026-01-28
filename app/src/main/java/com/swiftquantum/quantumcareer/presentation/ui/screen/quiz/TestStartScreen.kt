package com.swiftquantum.quantumcareer.presentation.ui.screen.quiz

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
import com.swiftquantum.quantumcareer.presentation.viewmodel.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestStartScreen(
    viewModel: QuizViewModel = hiltViewModel(),
    onStartTest: () -> Unit,
    onResumeTest: () -> Unit,
    onViewResult: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showRulesDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Certification Test") },
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
                    // Header Card
                    item {
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
                                Icon(
                                    imageVector = Icons.Default.Quiz,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Quantum Computing Certification",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "50 Questions | 90 Minutes | Earn Your Badge",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // Active Session Card
                    if (uiState.hasActiveSession) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = StatusUnderReview.copy(alpha = 0.2f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp),
                                        tint = StatusUnderReview
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Test In Progress",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        uiState.session?.let { session ->
                                            Text(
                                                text = "${session.answeredCount}/${session.totalQuestions} questions answered",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Button(onClick = onResumeTest) {
                                        Text("Resume")
                                    }
                                }
                            }
                        }
                    }

                    // Start Test Button (only if no active session)
                    if (!uiState.hasActiveSession) {
                        item {
                            Button(
                                onClick = { showRulesDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Start Certification Test",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }

                    // Badge Tiers Info
                    item {
                        SectionHeader(title = "Certification Tiers")
                    }

                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                BadgeTierRow(
                                    tier = BadgeTier.PLATINUM,
                                    requirement = "95% or higher",
                                    color = BadgePlatinum
                                )
                                HorizontalDivider()
                                BadgeTierRow(
                                    tier = BadgeTier.GOLD,
                                    requirement = "85% - 94%",
                                    color = BadgeGold
                                )
                                HorizontalDivider()
                                BadgeTierRow(
                                    tier = BadgeTier.SILVER,
                                    requirement = "75% - 84%",
                                    color = BadgeSilver
                                )
                                HorizontalDivider()
                                BadgeTierRow(
                                    tier = BadgeTier.BRONZE,
                                    requirement = "60% - 74%",
                                    color = BadgeBronze
                                )
                            }
                        }
                    }

                    // Test History
                    uiState.testHistory?.let { history ->
                        if (history.totalAttempts > 0) {
                            item {
                                SectionHeader(title = "Your History")
                            }

                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    StatCard(
                                        title = "Attempts",
                                        value = history.totalAttempts.toString(),
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatCard(
                                        title = "Best Score",
                                        value = "${history.bestScore}",
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatCard(
                                        title = "Pass Rate",
                                        value = "${(history.passRate * 100).toInt()}%",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            items(history.results.take(3)) { result ->
                                TestHistoryCard(
                                    result = result,
                                    onClick = { onViewResult(result.sessionId) }
                                )
                            }
                        }
                    }

                    // Categories Overview
                    if (uiState.categories.isNotEmpty()) {
                        item {
                            SectionHeader(title = "Question Categories")
                        }

                        items(uiState.categories) { category ->
                            CategoryInfoCard(category = category)
                        }
                    }
                }
            }
        }
    }

    // Test Rules Dialog
    if (showRulesDialog) {
        AlertDialog(
            onDismissRequest = { showRulesDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null
                )
            },
            title = {
                Text("Test Rules")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    RuleItem(text = "50 questions covering 6 categories")
                    RuleItem(text = "90 minutes time limit")
                    RuleItem(text = "Points: Easy (1), Medium (2), Hard (3), Expert (5)")
                    RuleItem(text = "Minimum 60% to earn certification")
                    RuleItem(text = "Cannot pause or go back to previous questions")
                    RuleItem(text = "Certificate valid for 2 years")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRulesDialog = false
                        viewModel.startTest()
                        onStartTest()
                    }
                ) {
                    Text("Start Test")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRulesDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun BadgeTierRow(
    tier: BadgeTier,
    requirement: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BadgeIcon(
            tier = tier,
            earned = true,
            size = 40
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tier.displayName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = requirement,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RuleItem(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun TestHistoryCard(
    result: TestResult,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            result.badgeEarned?.let { tier ->
                BadgeIcon(
                    tier = tier,
                    earned = true,
                    size = 48
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Score: ${result.score}/${result.maxScore}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = result.formattedTimeSpent,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (result.passed) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Passed",
                    tint = StatusPublished
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = "Failed",
                    tint = StatusRejected
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CategoryInfoCard(
    category: com.swiftquantum.quantumcareer.domain.repository.CategoryStats
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = category.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            category.userAccuracy?.let { accuracy ->
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${(accuracy * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Accuracy",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
