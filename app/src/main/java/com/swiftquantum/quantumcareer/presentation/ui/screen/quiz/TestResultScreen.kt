package com.swiftquantum.quantumcareer.presentation.ui.screen.quiz

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
fun TestResultScreen(
    viewModel: QuizViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onViewCertificate: (String) -> Unit,
    onRetakeTest: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val result = uiState.testResult

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Test Results") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearTestResult()
                        onNavigateBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (result?.passed == true) {
                        IconButton(onClick = { /* Share result */ }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingView(
                    modifier = Modifier.padding(paddingValues),
                    message = "Loading results..."
                )
            }
            result == null -> {
                ErrorView(
                    message = "No results available",
                    onRetry = { onNavigateBack() },
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
                    // Result Header
                    item {
                        ResultHeaderCard(result = result)
                    }

                    // Badge Earned
                    if (result.passed && result.badgeEarned != null) {
                        item {
                            BadgeEarnedCard(
                                badge = result.badgeEarned,
                                certificateId = result.certificateId,
                                onViewCertificate = onViewCertificate
                            )
                        }
                    }

                    // Score Breakdown
                    item {
                        SectionHeader(title = "Score Breakdown")
                    }

                    item {
                        ScoreBreakdownChart(result = result)
                    }

                    // Category Performance
                    item {
                        SectionHeader(title = "Performance by Category")
                    }

                    items(result.categoryBreakdown) { breakdown ->
                        CategoryPerformanceCard(breakdown = breakdown)
                    }

                    // Statistics
                    item {
                        SectionHeader(title = "Test Statistics")
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                title = "Time Spent",
                                value = result.formattedTimeSpent,
                                modifier = Modifier.weight(1f),
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            )
                            StatCard(
                                title = "Accuracy",
                                value = "${result.scorePercentage.toInt()}%",
                                modifier = Modifier.weight(1f),
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            )
                        }
                    }

                    // Action Buttons
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (result.passed && result.certificateId != null) {
                                Button(
                                    onClick = { onViewCertificate(result.certificateId) },
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("View Certificate")
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.clearTestResult()
                                    onRetakeTest()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Take Another Test")
                            }

                            TextButton(
                                onClick = {
                                    viewModel.clearTestResult()
                                    onNavigateBack()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Back to Dashboard")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultHeaderCard(result: TestResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (result.passed) {
                StatusPublished.copy(alpha = 0.1f)
            } else {
                StatusRejected.copy(alpha = 0.1f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (result.passed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = if (result.passed) StatusPublished else StatusRejected
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (result.passed) "Congratulations!" else "Better Luck Next Time",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Score Circle
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                ScoreCircle(
                    score = result.score,
                    maxScore = result.maxScore,
                    passingScore = result.passingScore
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${result.score} / ${result.maxScore} points",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${result.scorePercentage.toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (result.passed) {
                    "You passed! Minimum required: ${result.passedPercentage.toInt()}%"
                } else {
                    "Required ${result.passedPercentage.toInt()}% to pass"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ScoreCircle(
    score: Int,
    maxScore: Int,
    passingScore: Int
) {
    val progress = if (maxScore > 0) score.toFloat() / maxScore else 0f
    val passingProgress = if (maxScore > 0) passingScore.toFloat() / maxScore else 0f
    val passed = score >= passingScore

    val primaryColor = if (passed) StatusPublished else StatusRejected
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant

    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 12.dp.toPx()
        val diameter = size.minDimension - strokeWidth
        val topLeft = Offset(
            (size.width - diameter) / 2,
            (size.height - diameter) / 2
        )

        // Background circle
        drawArc(
            color = backgroundColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = Size(diameter, diameter),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Passing threshold marker
        drawArc(
            color = Color.Gray.copy(alpha = 0.5f),
            startAngle = -90f + (passingProgress * 360f) - 2f,
            sweepAngle = 4f,
            useCenter = false,
            topLeft = topLeft,
            size = Size(diameter, diameter),
            style = Stroke(width = strokeWidth + 4.dp.toPx(), cap = StrokeCap.Round)
        )

        // Progress arc
        drawArc(
            color = primaryColor,
            startAngle = -90f,
            sweepAngle = progress * 360f,
            useCenter = false,
            topLeft = topLeft,
            size = Size(diameter, diameter),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun BadgeEarnedCard(
    badge: BadgeTier,
    certificateId: String?,
    onViewCertificate: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (badge) {
                BadgeTier.BRONZE -> BadgeBronze.copy(alpha = 0.2f)
                BadgeTier.SILVER -> BadgeSilver.copy(alpha = 0.3f)
                BadgeTier.GOLD -> BadgeGold.copy(alpha = 0.2f)
                BadgeTier.PLATINUM -> BadgePlatinum.copy(alpha = 0.3f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BadgeIcon(
                tier = badge,
                earned = true,
                size = 72,
                showGlow = true
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Badge Earned!",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${badge.displayName} Certificate",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (certificateId != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { onViewCertificate(certificateId) },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("View Certificate")
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreBreakdownChart(result: TestResult) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Simple bar chart using Canvas
            val categories = result.categoryBreakdown
            val maxPoints = categories.maxOfOrNull { it.totalPoints } ?: 1

            categories.forEach { breakdown ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = breakdown.category.displayName.take(8),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.width(60.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(20.dp)
                    ) {
                        // Background bar (total possible)
                        LinearProgressIndicator(
                            progress = { breakdown.totalPoints.toFloat() / maxPoints },
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            trackColor = Color.Transparent,
                        )
                        // Earned bar
                        LinearProgressIndicator(
                            progress = { breakdown.earnedPoints.toFloat() / maxPoints },
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = Color.Transparent,
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${breakdown.earnedPoints}/${breakdown.totalPoints}",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.width(40.dp),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryPerformanceCard(breakdown: CategoryBreakdown) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = breakdown.category.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${(breakdown.accuracy * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        breakdown.accuracy >= 0.8f -> StatusPublished
                        breakdown.accuracy >= 0.6f -> StatusUnderReview
                        else -> StatusRejected
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { breakdown.accuracy },
                modifier = Modifier.fillMaxWidth(),
                color = when {
                    breakdown.accuracy >= 0.8f -> StatusPublished
                    breakdown.accuracy >= 0.6f -> StatusUnderReview
                    else -> StatusRejected
                },
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${breakdown.correctAnswers}/${breakdown.totalQuestions} correct",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${breakdown.earnedPoints}/${breakdown.totalPoints} pts",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
