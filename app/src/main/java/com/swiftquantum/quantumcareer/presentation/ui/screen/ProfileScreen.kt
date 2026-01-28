package com.swiftquantum.quantumcareer.presentation.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.swiftquantum.quantumcareer.R
import com.swiftquantum.quantumcareer.domain.model.CareerBadge
import com.swiftquantum.quantumcareer.domain.model.PublishedCircuit
import com.swiftquantum.quantumcareer.presentation.ui.component.*
import com.swiftquantum.quantumcareer.presentation.viewmodel.*
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToAuth: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val editForm by viewModel.editForm.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar("Profile updated successfully!")
            viewModel.clearSuccess()
        }
    }

    // Load initial tab data
    LaunchedEffect(Unit) {
        viewModel.selectTab(ProfileTab.OVERVIEW)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) stringResource(R.string.edit_profile) else stringResource(R.string.profile)) },
                actions = {
                    if (!uiState.isEditing) {
                        IconButton(onClick = { viewModel.startEditing() }) {
                            Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit))
                        }
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
                        }
                        IconButton(onClick = { viewModel.refresh() }) {
                            Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.refresh))
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            uiState.isLoading && uiState.profile == null -> {
                LoadingView(modifier = Modifier.padding(paddingValues))
            }
            uiState.error != null && uiState.profile == null -> {
                ErrorView(
                    message = uiState.error ?: "Unknown error",
                    onRetry = { viewModel.refresh() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.profile != null -> {
                val profile = uiState.profile!!

                if (uiState.isEditing) {
                    EditProfileContent(
                        editForm = editForm,
                        isLoading = uiState.isLoading,
                        onDisplayNameChange = { viewModel.updateDisplayName(it) },
                        onBioChange = { viewModel.updateBio(it) },
                        onInstitutionChange = { viewModel.updateInstitution(it) },
                        onLocationChange = { viewModel.updateLocation(it) },
                        onWebsiteChange = { viewModel.updateWebsite(it) },
                        onSpecializationsChange = { viewModel.updateSpecializations(it) },
                        onIsPublicChange = { viewModel.updateIsPublic(it) },
                        onIsAvailableForHireChange = { viewModel.updateIsAvailableForHire(it) },
                        onSave = { viewModel.saveProfile() },
                        onCancel = { viewModel.cancelEditing() },
                        modifier = Modifier.padding(paddingValues)
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        // Account Card (top of screen)
                        AccountCard(
                            isLoggedIn = uiState.isLoggedIn,
                            userName = uiState.authData?.userName,
                            userEmail = uiState.authData?.userEmail,
                            onSignIn = onNavigateToAuth,
                            onCreateAccount = onNavigateToAuth,
                            onLogout = { viewModel.showLogoutDialog(true) },
                            modifier = Modifier.padding(16.dp)
                        )

                        // Profile Header
                        ProfileHeader(
                            profile = profile,
                            onShareProfile = { viewModel.shareProfile() },
                            onExportPdf = { viewModel.exportPdfPortfolio() },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tab Row
                        ProfileTabRow(
                            selectedTab = uiState.selectedTab,
                            onTabSelected = { viewModel.selectTab(it) }
                        )

                        // Tab Content
                        when (uiState.selectedTab) {
                            ProfileTab.OVERVIEW -> OverviewTabContent(
                                contributionData = uiState.contributionData,
                                currentStreak = uiState.currentStreak,
                                longestStreak = uiState.longestStreak,
                                readinessScore = uiState.industryReadinessScore,
                                modifier = Modifier.weight(1f)
                            )
                            ProfileTab.CERTIFICATES -> CertificatesTabContent(
                                certificates = uiState.certificates,
                                onShareOnLinkedIn = { viewModel.shareOnLinkedIn(it) },
                                modifier = Modifier.weight(1f)
                            )
                            ProfileTab.BADGES -> BadgesTabContent(
                                earnedBadges = uiState.earnedBadges,
                                inProgressBadges = uiState.inProgressBadges,
                                modifier = Modifier.weight(1f)
                            )
                            ProfileTab.CIRCUITS -> CircuitsTabContent(
                                circuits = uiState.publishedCircuits,
                                modifier = Modifier.weight(1f)
                            )
                            ProfileTab.PORTFOLIO -> PortfolioTabContent(
                                skillBreakdown = uiState.skillBreakdown,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Logout confirmation dialog
                if (uiState.showLogoutDialog) {
                    AlertDialog(
                        onDismissRequest = { viewModel.showLogoutDialog(false) },
                        title = { Text(stringResource(R.string.sign_out)) },
                        text = { Text(stringResource(R.string.logout_confirmation)) },
                        confirmButton = {
                            Button(
                                onClick = { viewModel.logout() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text(stringResource(R.string.sign_out))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { viewModel.showLogoutDialog(false) }) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    profile: com.swiftquantum.quantumcareer.domain.model.PublicProfile,
    onShareProfile: () -> Unit,
    onExportPdf: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            if (profile.avatarUrl != null) {
                AsyncImage(
                    model = profile.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profile.displayName.first().uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = profile.displayName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = "@${profile.username}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            CurrentBadgeDisplay(tier = profile.badgeTier)

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ProfileStatItem(
                    value = profile.totalPublications.toString(),
                    label = stringResource(R.string.publications)
                )
                ProfileStatItem(
                    value = profile.totalCitations.toString(),
                    label = stringResource(R.string.citations)
                )
                ProfileStatItem(
                    value = profile.hIndex.toString(),
                    label = stringResource(R.string.h_index)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onShareProfile) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.share_profile))
                }
                Button(onClick = onExportPdf) {
                    Icon(
                        Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.export_pdf))
                }
            }
        }
    }
}

@Composable
private fun ProfileStatItem(
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
private fun ProfileTabRow(
    selectedTab: ProfileTab,
    onTabSelected: (ProfileTab) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTab.ordinal,
        modifier = Modifier.fillMaxWidth(),
        edgePadding = 16.dp
    ) {
        ProfileTab.entries.forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = when (tab) {
                            ProfileTab.OVERVIEW -> stringResource(R.string.overview)
                            ProfileTab.CERTIFICATES -> stringResource(R.string.certificates)
                            ProfileTab.BADGES -> stringResource(R.string.badges)
                            ProfileTab.CIRCUITS -> stringResource(R.string.circuits)
                            ProfileTab.PORTFOLIO -> stringResource(R.string.portfolio)
                        }
                    )
                },
                icon = {
                    Icon(
                        imageVector = when (tab) {
                            ProfileTab.OVERVIEW -> if (selectedTab == tab) Icons.Filled.Dashboard else Icons.Outlined.Dashboard
                            ProfileTab.CERTIFICATES -> if (selectedTab == tab) Icons.Filled.Verified else Icons.Outlined.Verified
                            ProfileTab.BADGES -> if (selectedTab == tab) Icons.Filled.EmojiEvents else Icons.Outlined.EmojiEvents
                            ProfileTab.CIRCUITS -> if (selectedTab == tab) Icons.Filled.Memory else Icons.Outlined.Memory
                            ProfileTab.PORTFOLIO -> if (selectedTab == tab) Icons.Filled.Assessment else Icons.Outlined.Assessment
                        },
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Composable
private fun OverviewTabContent(
    contributionData: List<ContributionDay>,
    currentStreak: Int,
    longestStreak: Int,
    readinessScore: IndustryReadinessScore?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Industry Readiness Score
        readinessScore?.let { score ->
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
                                text = score.level,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${score.score}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.evidence_breakdown),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    score.evidenceBreakdown.forEach { (category, points) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
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

        // Contribution Timeline Heatmap
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.contribution_timeline),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = currentStreak.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.current_streak),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = longestStreak.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(R.string.longest_streak),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 52-week grid heatmap
                ContributionHeatmap(
                    data = contributionData.takeLast(364), // 52 weeks
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.less),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    (0..4).forEach { level ->
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(getContributionColor(level))
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.more),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ContributionHeatmap(
    data: List<ContributionDay>,
    modifier: Modifier = Modifier
) {
    val weeks = data.chunked(7)

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(weeks) { week ->
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                week.forEach { day ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(getContributionColor(day.level))
                    )
                }
            }
        }
    }
}

@Composable
private fun getContributionColor(level: Int): Color {
    val primary = MaterialTheme.colorScheme.primary
    return when (level) {
        0 -> MaterialTheme.colorScheme.surfaceVariant
        1 -> primary.copy(alpha = 0.25f)
        2 -> primary.copy(alpha = 0.5f)
        3 -> primary.copy(alpha = 0.75f)
        else -> primary
    }
}

@Composable
private fun CertificatesTabContent(
    certificates: List<CertificateInfo>,
    onShareOnLinkedIn: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    if (certificates.isEmpty()) {
        EmptyView(
            title = stringResource(R.string.no_certificates),
            message = stringResource(R.string.no_certificates_message),
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(certificates) { cert ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = cert.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = when (cert.level) {
                                        "Foundation" -> Color(0xFFCD7F32).copy(alpha = 0.2f)
                                        "Associate" -> Color(0xFFC0C0C0).copy(alpha = 0.2f)
                                        "Professional" -> Color(0xFFFFD700).copy(alpha = 0.2f)
                                        else -> Color(0xFFE5E4E2).copy(alpha = 0.2f)
                                    }
                                ) {
                                    Text(
                                        text = cert.level,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.issued),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = cert.issuedAt.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            cert.expiresAt?.let { expires ->
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = stringResource(R.string.expires),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = expires.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { /* TODO: View certificate */ },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    Icons.Default.Visibility,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(stringResource(R.string.view))
                            }

                            Button(
                                onClick = {
                                    cert.linkedInShareUrl?.let { url ->
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(intent)
                                    }
                                    onShareOnLinkedIn(cert.id)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(stringResource(R.string.linkedin))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgesTabContent(
    earnedBadges: List<CareerBadge>,
    inProgressBadges: List<ProfileBadgeProgress>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Earned Badges Section
        if (earnedBadges.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.earned_badges),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(earnedBadges) { badge ->
                        EarnedBadgeCard(badge = badge)
                    }
                }
            }
        }

        // In Progress Badges Section
        if (inProgressBadges.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.in_progress),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(inProgressBadges) { progress ->
                InProgressBadgeCard(progress = progress)
            }
        }

        if (earnedBadges.isEmpty() && inProgressBadges.isEmpty()) {
            item {
                EmptyView(
                    title = stringResource(R.string.no_badges),
                    message = stringResource(R.string.no_badges_message)
                )
            }
        }
    }
}

@Composable
private fun EarnedBadgeCard(badge: CareerBadge) {
    Card(
        modifier = Modifier.width(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.EmojiEvents,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = badge.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun InProgressBadgeCard(progress: ProfileBadgeProgress) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.EmojiEvents,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = progress.badge.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = progress.badge.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = { progress.progressPercentage },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${progress.currentProgress}/${progress.targetProgress}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun CircuitsTabContent(
    circuits: List<PublishedCircuit>,
    modifier: Modifier = Modifier
) {
    if (circuits.isEmpty()) {
        EmptyView(
            title = stringResource(R.string.no_circuits),
            message = stringResource(R.string.no_circuits_message),
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(circuits) { circuit ->
                CircuitCard(circuit = circuit)
            }
        }
    }
}

@Composable
private fun CircuitCard(circuit: PublishedCircuit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = circuit.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = circuit.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${circuit.viewCount}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.FormatQuote,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${circuit.citationCount}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Memory,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${circuit.qubitCount} qubits",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun PortfolioTabContent(
    skillBreakdown: SkillBreakdown?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Skill Breakdown Visualization
        skillBreakdown?.let { skills ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.skill_breakdown),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    SkillBar(
                        label = stringResource(R.string.logic),
                        value = skills.logic,
                        color = Color(0xFF4CAF50)
                    )
                    SkillBar(
                        label = stringResource(R.string.innovation),
                        value = skills.innovation,
                        color = Color(0xFF2196F3)
                    )
                    SkillBar(
                        label = stringResource(R.string.contribution),
                        value = skills.contribution,
                        color = Color(0xFFFF9800)
                    )
                    SkillBar(
                        label = stringResource(R.string.stability),
                        value = skills.stability,
                        color = Color(0xFF9C27B0)
                    )
                    SkillBar(
                        label = stringResource(R.string.speed),
                        value = skills.speed,
                        color = Color(0xFFE91E63)
                    )
                }
            }
        }
    }
}

@Composable
private fun SkillBar(
    label: String,
    value: Float,
    color: Color
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${(value * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { value },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun EditProfileContent(
    editForm: ProfileEditFormState,
    isLoading: Boolean,
    onDisplayNameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onInstitutionChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onWebsiteChange: (String) -> Unit,
    onSpecializationsChange: (String) -> Unit,
    onIsPublicChange: (Boolean) -> Unit,
    onIsAvailableForHireChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = editForm.displayName,
            onValueChange = onDisplayNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.display_name)) },
            singleLine = true
        )

        OutlinedTextField(
            value = editForm.bio,
            onValueChange = onBioChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp),
            label = { Text(stringResource(R.string.bio)) },
            placeholder = { Text(stringResource(R.string.bio_placeholder)) },
            maxLines = 5
        )

        OutlinedTextField(
            value = editForm.institution,
            onValueChange = onInstitutionChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.institution)) },
            placeholder = { Text(stringResource(R.string.institution_placeholder)) },
            singleLine = true
        )

        OutlinedTextField(
            value = editForm.location,
            onValueChange = onLocationChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.location)) },
            placeholder = { Text(stringResource(R.string.location_placeholder)) },
            singleLine = true
        )

        OutlinedTextField(
            value = editForm.website,
            onValueChange = onWebsiteChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.website)) },
            placeholder = { Text(stringResource(R.string.website_placeholder)) },
            singleLine = true
        )

        OutlinedTextField(
            value = editForm.specializations,
            onValueChange = onSpecializationsChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.specializations)) },
            placeholder = { Text(stringResource(R.string.specializations_placeholder)) },
            supportingText = { Text(stringResource(R.string.separate_commas)) },
            singleLine = true
        )

        // Visibility Settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.privacy_settings),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.public_profile),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = stringResource(R.string.public_profile_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = editForm.isPublic,
                        onCheckedChange = onIsPublicChange
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.available_for_hire),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = stringResource(R.string.available_for_hire_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = editForm.isAvailableForHire,
                        onCheckedChange = onIsAvailableForHireChange
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.cancel))
            }
            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}

@Composable
private fun AccountCard(
    isLoggedIn: Boolean,
    userName: String?,
    userEmail: String?,
    onSignIn: () -> Unit,
    onCreateAccount: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (isLoggedIn) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (userName?.firstOrNull()?.uppercase() ?: "U"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userName ?: "User",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = userEmail ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = stringResource(R.string.sign_out),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.guest_mode),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.sign_in_to_build_portfolio),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onSignIn,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.sign_in))
                    }

                    OutlinedButton(
                        onClick = onCreateAccount,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.create_account))
                    }
                }
            }
        }
    }
}
