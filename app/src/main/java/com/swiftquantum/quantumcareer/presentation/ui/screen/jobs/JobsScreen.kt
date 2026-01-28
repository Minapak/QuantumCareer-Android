package com.swiftquantum.quantumcareer.presentation.ui.screen.jobs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.swiftquantum.quantumcareer.R
import com.swiftquantum.quantumcareer.domain.model.*
import com.swiftquantum.quantumcareer.presentation.ui.component.*
import com.swiftquantum.quantumcareer.presentation.viewmodel.JobsTab
import com.swiftquantum.quantumcareer.presentation.viewmodel.JobsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobsScreen(
    viewModel: JobsViewModel = hiltViewModel(),
    onNavigateToJobDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
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
                title = { Text(stringResource(R.string.jobs)) },
                actions = {
                    if (uiState.selectedTab == JobsTab.BROWSE) {
                        BadgedBox(
                            badge = {
                                if (uiState.filter.hasActiveFilters) {
                                    Badge { Text(uiState.filter.activeFilterCount.toString()) }
                                }
                            }
                        ) {
                            IconButton(onClick = { viewModel.showFilterSheet() }) {
                                Icon(Icons.Default.FilterList, contentDescription = stringResource(R.string.filter))
                            }
                        }
                    }
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.refresh))
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
        ) {
            // Job Stats Summary Card
            uiState.jobStats?.let { stats ->
                JobStatsCard(stats = stats, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }

            // Tab Row
            JobsTabRow(
                selectedTab = uiState.selectedTab,
                onTabSelected = { viewModel.selectTab(it) }
            )

            // Content based on selected tab
            when (uiState.selectedTab) {
                JobsTab.BROWSE -> BrowseJobsContent(
                    jobs = uiState.jobs,
                    filter = uiState.filter,
                    isLoading = uiState.isLoading,
                    isLoadingMore = uiState.isLoadingMore,
                    hasMore = uiState.hasMoreJobs,
                    onJobClick = onNavigateToJobDetail,
                    onSaveJob = { viewModel.toggleSaveJob(it) },
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    onSearch = { viewModel.searchJobs() },
                    onLoadMore = { viewModel.loadMoreJobs() },
                    onRefresh = { viewModel.loadJobs(refresh = true) }
                )
                JobsTab.RECOMMENDED -> RecommendedJobsContent(
                    recommendedJobs = uiState.recommendedJobs,
                    isLoading = uiState.isLoading,
                    onJobClick = onNavigateToJobDetail,
                    onSaveJob = { viewModel.toggleSaveJob(it.job) },
                    onApply = { viewModel.showApplicationSheet(it.job) },
                    onRefresh = { viewModel.loadRecommendedJobs() }
                )
                JobsTab.SAVED -> SavedJobsContent(
                    savedJobs = uiState.savedJobs,
                    isLoading = uiState.isLoading,
                    onJobClick = onNavigateToJobDetail,
                    onUnsave = { viewModel.unsaveJob(it.job.id) },
                    onRefresh = { viewModel.loadSavedJobs() }
                )
                JobsTab.APPLICATIONS -> ApplicationsContent(
                    applications = uiState.applications,
                    isLoading = uiState.isLoading,
                    onJobClick = onNavigateToJobDetail,
                    onWithdraw = { viewModel.withdrawApplication(it.id) },
                    onRefresh = { viewModel.loadApplications() }
                )
            }
        }

        // Filter Bottom Sheet
        if (uiState.showFilterSheet) {
            JobFilterBottomSheet(
                currentFilter = uiState.filter,
                onDismiss = { viewModel.hideFilterSheet() },
                onApplyFilter = {
                    viewModel.updateFilter(it)
                    viewModel.hideFilterSheet()
                },
                onClearFilters = {
                    viewModel.clearFilters()
                    viewModel.hideFilterSheet()
                }
            )
        }

        // Application Bottom Sheet
        if (uiState.showApplicationSheet && uiState.selectedJobForApplication != null) {
            JobApplicationSheet(
                job = uiState.selectedJobForApplication!!,
                isSubmitting = uiState.isLoading,
                onDismiss = { viewModel.hideApplicationSheet() },
                onSubmit = { coverLetter, resumeUrl ->
                    viewModel.applyToJob(coverLetter, resumeUrl)
                }
            )
        }
    }
}

@Composable
private fun JobStatsCard(
    stats: JobStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                value = stats.totalApplications.toString(),
                label = stringResource(R.string.applications)
            )
            StatItem(
                value = stats.interviewsScheduled.toString(),
                label = stringResource(R.string.interviews)
            )
            StatItem(
                value = stats.offersReceived.toString(),
                label = stringResource(R.string.offers)
            )
            StatItem(
                value = stats.savedJobs.toString(),
                label = stringResource(R.string.saved)
            )
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun JobsTabRow(
    selectedTab: JobsTab,
    onTabSelected: (JobsTab) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTab.ordinal,
        modifier = Modifier.fillMaxWidth(),
        edgePadding = 16.dp
    ) {
        JobsTab.entries.forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = when (tab) {
                            JobsTab.BROWSE -> stringResource(R.string.browse_jobs)
                            JobsTab.RECOMMENDED -> stringResource(R.string.recommended)
                            JobsTab.SAVED -> stringResource(R.string.saved_jobs)
                            JobsTab.APPLICATIONS -> stringResource(R.string.applications)
                        }
                    )
                },
                icon = {
                    Icon(
                        imageVector = when (tab) {
                            JobsTab.BROWSE -> if (selectedTab == tab) Icons.Filled.Search else Icons.Outlined.Search
                            JobsTab.RECOMMENDED -> if (selectedTab == tab) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome
                            JobsTab.SAVED -> if (selectedTab == tab) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder
                            JobsTab.APPLICATIONS -> if (selectedTab == tab) Icons.Filled.Description else Icons.Outlined.Description
                        },
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Composable
private fun BrowseJobsContent(
    jobs: List<Job>,
    filter: JobFilter,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    hasMore: Boolean,
    onJobClick: (String) -> Unit,
    onSaveJob: (Job) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit
) {
    val listState = rememberLazyListState()

    // Detect when scrolled to bottom
    LaunchedEffect(listState) {
        snapshotFlow {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleItem >= totalItems - 3
        }.collect { shouldLoadMore ->
            if (shouldLoadMore && hasMore && !isLoadingMore) {
                onLoadMore()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar
        SearchBar(
            query = filter.searchQuery,
            onQueryChange = onSearchQueryChange,
            onSearch = onSearch,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Active Filters Chips
        if (filter.hasActiveFilters) {
            ActiveFiltersRow(filter = filter)
        }

        when {
            isLoading && jobs.isEmpty() -> {
                LoadingView()
            }
            jobs.isEmpty() -> {
                EmptyView(
                    title = stringResource(R.string.no_jobs_found),
                    message = stringResource(R.string.no_jobs_found_message)
                )
            }
            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(jobs, key = { it.id }) { job ->
                        JobCard(
                            job = job,
                            onClick = { onJobClick(job.id) },
                            onSaveClick = { onSaveJob(job) }
                        )
                    }

                    if (isLoadingMore) {
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

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.search_jobs_placeholder)) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange(""); onSearch() }) {
                    Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.clear))
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
            onSearch = { onSearch() }
        ),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            imeAction = androidx.compose.ui.text.input.ImeAction.Search
        )
    )
}

@Composable
private fun ActiveFiltersRow(filter: JobFilter) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (filter.locationTypes.isNotEmpty()) {
            items(filter.locationTypes) { type ->
                FilterChip(
                    selected = true,
                    onClick = { },
                    label = { Text(type.displayName) }
                )
            }
        }
        if (filter.employmentTypes.isNotEmpty()) {
            items(filter.employmentTypes) { type ->
                FilterChip(
                    selected = true,
                    onClick = { },
                    label = { Text(type.displayName) }
                )
            }
        }
        if (filter.experienceLevels.isNotEmpty()) {
            items(filter.experienceLevels) { level ->
                FilterChip(
                    selected = true,
                    onClick = { },
                    label = { Text(level.shortName) }
                )
            }
        }
    }
}

@Composable
private fun RecommendedJobsContent(
    recommendedJobs: List<RecommendedJob>,
    isLoading: Boolean,
    onJobClick: (String) -> Unit,
    onSaveJob: (RecommendedJob) -> Unit,
    onApply: (RecommendedJob) -> Unit,
    onRefresh: () -> Unit
) {
    when {
        isLoading && recommendedJobs.isEmpty() -> {
            LoadingView()
        }
        recommendedJobs.isEmpty() -> {
            EmptyView(
                title = stringResource(R.string.no_recommendations),
                message = stringResource(R.string.no_recommendations_message)
            )
        }
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recommendedJobs, key = { it.job.id }) { recommendation ->
                    RecommendedJobCard(
                        recommendation = recommendation,
                        onClick = { onJobClick(recommendation.job.id) },
                        onSaveClick = { onSaveJob(recommendation) },
                        onApplyClick = { onApply(recommendation) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedJobsContent(
    savedJobs: List<SavedJob>,
    isLoading: Boolean,
    onJobClick: (String) -> Unit,
    onUnsave: (SavedJob) -> Unit,
    onRefresh: () -> Unit
) {
    when {
        isLoading && savedJobs.isEmpty() -> {
            LoadingView()
        }
        savedJobs.isEmpty() -> {
            EmptyView(
                title = stringResource(R.string.no_saved_jobs),
                message = stringResource(R.string.no_saved_jobs_message)
            )
        }
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(savedJobs, key = { it.job.id }) { savedJob ->
                    SavedJobCard(
                        savedJob = savedJob,
                        onClick = { onJobClick(savedJob.job.id) },
                        onUnsaveClick = { onUnsave(savedJob) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ApplicationsContent(
    applications: List<JobApplication>,
    isLoading: Boolean,
    onJobClick: (String) -> Unit,
    onWithdraw: (JobApplication) -> Unit,
    onRefresh: () -> Unit
) {
    when {
        isLoading && applications.isEmpty() -> {
            LoadingView()
        }
        applications.isEmpty() -> {
            EmptyView(
                title = stringResource(R.string.no_applications),
                message = stringResource(R.string.no_applications_message)
            )
        }
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(applications, key = { it.id }) { application ->
                    ApplicationCard(
                        application = application,
                        onClick = { onJobClick(application.job.id) },
                        onWithdrawClick = { onWithdraw(application) }
                    )
                }
            }
        }
    }
}

@Composable
fun JobCard(
    job: Job,
    onClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
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
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Company Logo
                    if (job.company.logoUrl != null) {
                        AsyncImage(
                            model = job.company.logoUrl,
                            contentDescription = job.company.name,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = job.company.name.first().toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = job.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = job.company.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onSaveClick) {
                    Icon(
                        imageVector = if (job.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (job.isSaved) stringResource(R.string.unsave) else stringResource(R.string.save_job),
                        tint = if (job.isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Location and Type
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${job.location} - ${job.locationType.displayName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tags Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SuggestionChip(
                    onClick = { },
                    label = { Text(job.employmentType.displayName, style = MaterialTheme.typography.labelSmall) }
                )
                SuggestionChip(
                    onClick = { },
                    label = { Text(job.experienceLevel.shortName, style = MaterialTheme.typography.labelSmall) }
                )
            }

            // Salary and Posted Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                job.salaryRange?.let { salary ->
                    Text(
                        text = salary.formattedRange,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = job.formattedPostedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Match Score if available
            job.matchScore?.let { score ->
                Spacer(modifier = Modifier.height(8.dp))
                MatchScoreIndicator(score = score)
            }
        }
    }
}

@Composable
fun RecommendedJobCard(
    recommendation: RecommendedJob,
    onClick: () -> Unit,
    onSaveClick: () -> Unit,
    onApplyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Match Score Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MatchScoreBadge(score = recommendation.matchScore)

                IconButton(onClick = onSaveClick) {
                    Icon(
                        imageVector = if (recommendation.job.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = null,
                        tint = if (recommendation.job.isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Job Info
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (recommendation.job.company.logoUrl != null) {
                    AsyncImage(
                        model = recommendation.job.company.logoUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = recommendation.job.company.name.first().toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recommendation.job.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = recommendation.job.company.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Match Reasons
            if (recommendation.matchReasons.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.why_good_match),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                recommendation.matchReasons.take(3).forEach { reason ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = reason.description,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.view_details))
                }
                Button(
                    onClick = onApplyClick,
                    modifier = Modifier.weight(1f),
                    enabled = !recommendation.job.hasApplied
                ) {
                    Text(
                        if (recommendation.job.hasApplied) stringResource(R.string.applied)
                        else stringResource(R.string.apply_now)
                    )
                }
            }
        }
    }
}

@Composable
private fun MatchScoreBadge(score: Float) {
    val percentage = (score * 100).toInt()
    val color = when {
        score >= 0.8f -> Color(0xFF4CAF50)
        score >= 0.6f -> Color(0xFF8BC34A)
        score >= 0.4f -> Color(0xFFFFEB3B)
        else -> Color(0xFFFF9800)
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = color
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$percentage% ${stringResource(R.string.match)}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun MatchScoreIndicator(score: Float) {
    val percentage = (score * 100).toInt()
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        LinearProgressIndicator(
            progress = { score },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = when {
                score >= 0.8f -> Color(0xFF4CAF50)
                score >= 0.6f -> Color(0xFF8BC34A)
                else -> Color(0xFFFFEB3B)
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SavedJobCard(
    savedJob: SavedJob,
    onClick: () -> Unit,
    onUnsaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Company Logo
            if (savedJob.job.company.logoUrl != null) {
                AsyncImage(
                    model = savedJob.job.company.logoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = savedJob.job.company.name.first().toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = savedJob.job.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = savedJob.job.company.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${savedJob.job.location} - ${savedJob.job.locationType.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onUnsaveClick) {
                Icon(
                    Icons.Filled.Bookmark,
                    contentDescription = stringResource(R.string.unsave),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ApplicationCard(
    application: JobApplication,
    onClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
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
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Company Logo
                    if (application.job.company.logoUrl != null) {
                        AsyncImage(
                            model = application.job.company.logoUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = application.job.company.name.first().toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = application.job.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = application.job.company.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                ApplicationStatusChip(status = application.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Application Timeline
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${stringResource(R.string.applied)} ${application.daysSinceApplied} ${stringResource(R.string.days_ago)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (application.status == ApplicationStatus.APPLIED ||
                    application.status == ApplicationStatus.REVIEWING) {
                    TextButton(onClick = onWithdrawClick) {
                        Text(
                            stringResource(R.string.withdraw),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Next Steps if available
            application.nextSteps?.let { steps ->
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = steps,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Interview Date if scheduled
            application.interviewDate?.let { date ->
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Event,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${stringResource(R.string.interview_scheduled)}: ${date.format(java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a"))}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ApplicationStatusChip(status: ApplicationStatus) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(status.color).copy(alpha = 0.15f)
    ) {
        Text(
            text = status.displayName,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color(status.color)
        )
    }
}
