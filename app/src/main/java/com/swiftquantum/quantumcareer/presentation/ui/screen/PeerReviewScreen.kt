package com.swiftquantum.quantumcareer.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.swiftquantum.quantumcareer.R
import com.swiftquantum.quantumcareer.presentation.ui.component.*
import com.swiftquantum.quantumcareer.presentation.viewmodel.PeerReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeerReviewScreen(
    viewModel: PeerReviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    val reviewSubmittedMessage = stringResource(R.string.review_submitted_success)
    LaunchedEffect(uiState.submitSuccess) {
        if (uiState.submitSuccess) {
            snackbarHostState.showSnackbar(reviewSubmittedMessage)
            viewModel.clearSuccess()
        }
    }

    Scaffold(
        topBar = {
            if (uiState.selectedReview != null) {
                TopAppBar(
                    title = { Text(stringResource(R.string.submit_review_title)) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelectedReview() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text(stringResource(R.string.peer_review_title)) },
                    actions = {
                        IconButton(onClick = { viewModel.loadData() }) {
                            Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.refresh))
                        }
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.selectedReview != null) {
            // Review Detail View
            val review = uiState.selectedReview!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Circuit Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = review.circuitTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "by ${review.circuitAuthor}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ReviewSubmitForm(
                    qasmCode = review.qasmCode,
                    selectedDecision = formState.decision,
                    comment = formState.comment,
                    technicalScore = formState.technicalScore,
                    innovationScore = formState.innovationScore,
                    documentationScore = formState.documentationScore,
                    onDecisionChange = { viewModel.updateDecision(it) },
                    onCommentChange = { viewModel.updateComment(it) },
                    onTechnicalScoreChange = { viewModel.updateTechnicalScore(it) },
                    onInnovationScoreChange = { viewModel.updateInnovationScore(it) },
                    onDocumentationScoreChange = { viewModel.updateDocumentationScore(it) },
                    onSubmit = { viewModel.submitReview() },
                    isLoading = uiState.isLoading,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            // Review List View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Reviewer Stats Card
                uiState.reviewerStats?.let { stats ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
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
                                    text = stringResource(R.string.reviewer_stats),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                ReviewerLevelBadge(level = stats.reviewerLevel)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = stats.totalReviews.toString(),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = stringResource(R.string.total),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = stats.approvedCount.toString(),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = stringResource(R.string.approved),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = stats.rejectedCount.toString(),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = stringResource(R.string.rejected),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }

                            stats.reviewsUntilNextLevel?.let { remaining ->
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = stringResource(R.string.reviews_until_next_level, remaining),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                // Tabs
                TabRow(selectedTabIndex = uiState.selectedTab) {
                    Tab(
                        selected = uiState.selectedTab == 0,
                        onClick = { viewModel.selectTab(0) },
                        text = { Text(stringResource(R.string.pending_reviews_count, uiState.pendingReviews.size)) }
                    )
                    Tab(
                        selected = uiState.selectedTab == 1,
                        onClick = { viewModel.selectTab(1) },
                        text = { Text(stringResource(R.string.my_reviews)) }
                    )
                }

                when {
                    uiState.isLoading -> {
                        LoadingView()
                    }
                    uiState.selectedTab == 0 && uiState.pendingReviews.isEmpty() -> {
                        EmptyView(
                            title = stringResource(R.string.no_pending_reviews),
                            message = stringResource(R.string.no_pending_reviews_message)
                        )
                    }
                    uiState.selectedTab == 1 && uiState.myReviews.isEmpty() -> {
                        EmptyView(
                            title = stringResource(R.string.no_reviews_yet),
                            message = stringResource(R.string.no_reviews_yet_message)
                        )
                    }
                    else -> {
                        val reviews = if (uiState.selectedTab == 0) {
                            uiState.pendingReviews
                        } else {
                            uiState.myReviews
                        }

                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(reviews) { review ->
                                ReviewCard(
                                    review = review,
                                    onClaim = if (uiState.selectedTab == 0) {
                                        { viewModel.claimReview(review.id) }
                                    } else null,
                                    onClick = { viewModel.selectReview(review) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
