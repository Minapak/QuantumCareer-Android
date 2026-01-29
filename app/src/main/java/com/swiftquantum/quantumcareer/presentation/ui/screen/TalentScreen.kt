package com.swiftquantum.quantumcareer.presentation.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.swiftquantum.quantumcareer.R
import com.swiftquantum.quantumcareer.domain.model.*
import com.swiftquantum.quantumcareer.presentation.ui.component.*
import com.swiftquantum.quantumcareer.presentation.viewmodel.TalentViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalentScreen(
    viewModel: TalentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchForm by viewModel.searchForm.collectAsState()
    val scoutForm by viewModel.scoutForm.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    val offerSentMessage = stringResource(R.string.offer_sent_success)
    LaunchedEffect(uiState.scoutSuccess) {
        if (uiState.scoutSuccess) {
            snackbarHostState.showSnackbar(offerSentMessage)
            viewModel.clearSuccess()
        }
    }

    Scaffold(
        topBar = {
            if (uiState.selectedProfile != null) {
                TopAppBar(
                    title = { Text(stringResource(R.string.scout_researcher_title)) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelectedProfile() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text(stringResource(R.string.talent_title)) },
                    actions = {
                        IconButton(onClick = { viewModel.loadOffers() }) {
                            Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.refresh))
                        }
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.selectedProfile != null) {
            // Scout Form View
            val profile = uiState.selectedProfile!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Preview
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (profile.avatarUrl != null) {
                            AsyncImage(
                                model = profile.avatarUrl,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = profile.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "@${profile.username}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            CompactIndexDisplay(
                                hIndex = profile.hIndex,
                                i10Index = profile.i10Index
                            )
                        }
                    }
                }

                // Offer Type Selection
                Text(
                    text = stringResource(R.string.offer_type),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OfferType.entries.take(3).forEach { type ->
                        FilterChip(
                            selected = scoutForm.offerType == type,
                            onClick = { viewModel.updateOfferType(type) },
                            label = { Text(type.displayName) }
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OfferType.entries.drop(3).forEach { type ->
                        FilterChip(
                            selected = scoutForm.offerType == type,
                            onClick = { viewModel.updateOfferType(type) },
                            label = { Text(type.displayName) }
                        )
                    }
                }

                // Organization
                OutlinedTextField(
                    value = scoutForm.organization,
                    onValueChange = { viewModel.updateOrganization(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.organization)) },
                    placeholder = { Text(stringResource(R.string.organization_placeholder)) },
                    singleLine = true
                )

                // Position
                OutlinedTextField(
                    value = scoutForm.position,
                    onValueChange = { viewModel.updatePosition(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.position_optional)) },
                    placeholder = { Text(stringResource(R.string.position_placeholder)) },
                    singleLine = true
                )

                // Message
                OutlinedTextField(
                    value = scoutForm.message,
                    onValueChange = { viewModel.updateScoutMessage(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    label = { Text(stringResource(R.string.message)) },
                    placeholder = { Text(stringResource(R.string.message_placeholder)) },
                    maxLines = 5
                )

                // Details
                OutlinedTextField(
                    value = scoutForm.details,
                    onValueChange = { viewModel.updateDetails(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    label = { Text(stringResource(R.string.additional_details_optional)) },
                    placeholder = { Text(stringResource(R.string.additional_details_placeholder)) },
                    maxLines = 4
                )

                // Send Button
                Button(
                    onClick = { viewModel.scoutTalent() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading && scoutForm.organization.isNotBlank() && scoutForm.message.isNotBlank()
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.send_offer))
                    }
                }
            }
        } else {
            // Main Talent View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Tabs
                TabRow(selectedTabIndex = uiState.selectedTab) {
                    Tab(
                        selected = uiState.selectedTab == 0,
                        onClick = { viewModel.selectTab(0) },
                        text = { Text(stringResource(R.string.search)) }
                    )
                    Tab(
                        selected = uiState.selectedTab == 1,
                        onClick = { viewModel.selectTab(1) },
                        text = { Text(stringResource(R.string.received_count, uiState.receivedOffers.size)) }
                    )
                    Tab(
                        selected = uiState.selectedTab == 2,
                        onClick = { viewModel.selectTab(2) },
                        text = { Text(stringResource(R.string.sent_count, uiState.sentOffers.size)) }
                    )
                }

                when (uiState.selectedTab) {
                    0 -> SearchTabContent(
                        searchForm = searchForm,
                        searchResults = uiState.searchResults,
                        isLoading = uiState.isLoading,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        onMinHIndexChange = { viewModel.updateMinHIndex(it) },
                        onMinPublicationsChange = { viewModel.updateMinPublications(it) },
                        onBadgeTierChange = { viewModel.updateBadgeTier(it) },
                        onSearch = { viewModel.searchTalent() },
                        onProfileClick = { viewModel.selectProfile(it) }
                    )
                    1 -> OffersTabContent(
                        offers = uiState.receivedOffers,
                        isReceived = true,
                        onAccept = { viewModel.respondToOffer(it, true) },
                        onDecline = { viewModel.respondToOffer(it, false) },
                        onWithdraw = null
                    )
                    2 -> OffersTabContent(
                        offers = uiState.sentOffers,
                        isReceived = false,
                        onAccept = null,
                        onDecline = null,
                        onWithdraw = { viewModel.withdrawOffer(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchTabContent(
    searchForm: com.swiftquantum.quantumcareer.presentation.viewmodel.TalentSearchFormState,
    searchResults: List<TalentProfile>,
    isLoading: Boolean,
    onQueryChange: (String) -> Unit,
    onMinHIndexChange: (String) -> Unit,
    onMinPublicationsChange: (String) -> Unit,
    onBadgeTierChange: (BadgeTier?) -> Unit,
    onSearch: () -> Unit,
    onProfileClick: (TalentProfile) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search Form
        item {
            OutlinedTextField(
                value = searchForm.query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.search_by_name)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = searchForm.minHIndex,
                    onValueChange = onMinHIndexChange,
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.min_h_index)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = searchForm.minPublications,
                    onValueChange = onMinPublicationsChange,
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.min_publications)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = searchForm.badgeTier == null,
                    onClick = { onBadgeTierChange(null) },
                    label = { Text(stringResource(R.string.all)) }
                )
                BadgeTier.entries.forEach { tier ->
                    FilterChip(
                        selected = searchForm.badgeTier == tier,
                        onClick = { onBadgeTierChange(tier) },
                        label = { Text(tier.displayName) }
                    )
                }
            }
        }

        item {
            Button(
                onClick = onSearch,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.search))
                }
            }
        }

        if (searchResults.isEmpty() && !isLoading) {
            item {
                EmptyView(
                    title = stringResource(R.string.no_results),
                    message = stringResource(R.string.try_adjusting_search),
                    modifier = Modifier.height(200.dp)
                )
            }
        }

        items(searchResults) { profile ->
            TalentProfileCard(
                profile = profile,
                onClick = { onProfileClick(profile) }
            )
        }
    }
}

@Composable
private fun TalentProfileCard(
    profile: TalentProfile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (profile.avatarUrl != null) {
                AsyncImage(
                    model = profile.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = profile.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CurrentBadgeDisplay(tier = profile.badgeTier)
                }

                profile.institution?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                CompactIndexDisplay(
                    hIndex = profile.hIndex,
                    i10Index = profile.i10Index
                )
            }

            if (profile.isAvailable) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = stringResource(R.string.available_for_hire),
                    tint = com.swiftquantum.quantumcareer.presentation.ui.theme.StatusPublished
                )
            }
        }
    }
}

@Composable
private fun OffersTabContent(
    offers: List<TalentOffer>,
    isReceived: Boolean,
    onAccept: ((String) -> Unit)?,
    onDecline: ((String) -> Unit)?,
    onWithdraw: ((String) -> Unit)?
) {
    if (offers.isEmpty()) {
        EmptyView(
            title = stringResource(if (isReceived) R.string.no_offers_received else R.string.no_offers_sent),
            message = stringResource(if (isReceived) R.string.no_offers_received_message else R.string.no_offers_sent_message)
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(offers) { offer ->
                OfferCard(
                    offer = offer,
                    isReceived = isReceived,
                    onAccept = onAccept,
                    onDecline = onDecline,
                    onWithdraw = onWithdraw
                )
            }
        }
    }
}

@Composable
private fun OfferCard(
    offer: TalentOffer,
    isReceived: Boolean,
    onAccept: ((String) -> Unit)?,
    onDecline: ((String) -> Unit)?,
    onWithdraw: ((String) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = offer.offerType.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isReceived) {
                            stringResource(R.string.from_user, offer.fromUserName)
                        } else {
                            stringResource(R.string.to_researcher)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = when (offer.status) {
                        OfferStatus.PENDING -> com.swiftquantum.quantumcareer.presentation.ui.theme.StatusUnderReview.copy(alpha = 0.2f)
                        OfferStatus.ACCEPTED -> com.swiftquantum.quantumcareer.presentation.ui.theme.StatusPublished.copy(alpha = 0.2f)
                        OfferStatus.DECLINED -> com.swiftquantum.quantumcareer.presentation.ui.theme.StatusRejected.copy(alpha = 0.2f)
                        OfferStatus.WITHDRAWN -> com.swiftquantum.quantumcareer.presentation.ui.theme.StatusDraft.copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        text = offer.status.displayName,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = offer.fromOrganization,
                style = MaterialTheme.typography.bodyMedium
            )

            offer.position?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = offer.message,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = offer.createdAt.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (offer.status == OfferStatus.PENDING) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isReceived && onAccept != null && onDecline != null) {
                        OutlinedButton(
                            onClick = { onDecline(offer.id) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.decline))
                        }
                        Button(
                            onClick = { onAccept(offer.id) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.accept))
                        }
                    } else if (!isReceived && onWithdraw != null) {
                        OutlinedButton(
                            onClick = { onWithdraw(offer.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.withdraw_offer))
                        }
                    }
                }
            }
        }
    }
}
