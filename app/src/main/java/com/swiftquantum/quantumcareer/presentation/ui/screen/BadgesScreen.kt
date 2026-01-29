package com.swiftquantum.quantumcareer.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import com.swiftquantum.quantumcareer.domain.model.AustralianQuantumCredits
import com.swiftquantum.quantumcareer.domain.model.AustralianStandardsCertification
import com.swiftquantum.quantumcareer.domain.model.BadgeTier
import com.swiftquantum.quantumcareer.domain.model.FidelityGrade
import com.swiftquantum.quantumcareer.domain.model.SQCFidelityMetrics
import com.swiftquantum.quantumcareer.presentation.ui.component.*
import com.swiftquantum.quantumcareer.presentation.ui.theme.*
import com.swiftquantum.quantumcareer.presentation.viewmodel.BadgesViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgesScreen(
    viewModel: BadgesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Badges") },
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
            uiState.badgeCollection != null -> {
                val collection = uiState.badgeCollection!!

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Current Tier Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = when (collection.currentTier) {
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
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BadgeIcon(
                                    tier = collection.currentTier,
                                    earned = true,
                                    size = 80,
                                    showGlow = true
                                )

                                Spacer(modifier = Modifier.width(24.dp))

                                Column {
                                    Text(
                                        text = "Current Tier",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = collection.currentTier.displayName,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Researcher",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Next Badge Progress
                    collection.nextBadge?.let { nextBadge ->
                        item {
                            SectionHeader(title = "Next Achievement")
                        }

                        item {
                            BadgeProgressCard(badge = nextBadge)
                        }
                    }

                    // Badge Requirements Info
                    item {
                        SectionHeader(title = "Badge Requirements")
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                BadgeTier.entries.forEach { tier ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BadgeIcon(
                                            tier = tier,
                                            earned = tier.ordinal <= collection.currentTier.ordinal,
                                            size = 40
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = tier.displayName,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            val req = tier.requirements
                                            Text(
                                                text = when (tier) {
                                                    BadgeTier.BRONZE -> "First circuit published"
                                                    else -> "${req.publications} publications + ${req.citations} citations"
                                                },
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        if (tier.ordinal <= collection.currentTier.ordinal) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Earned",
                                                tint = StatusPublished
                                            )
                                        }
                                    }

                                    if (tier != BadgeTier.PLATINUM) {
                                        HorizontalDivider()
                                    }
                                }
                            }
                        }
                    }

                    // All Badges Collection
                    item {
                        SectionHeader(title = "All Badges")
                    }

                    items(collection.badges) { badge ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (badge.earned) {
                                    MaterialTheme.colorScheme.surface
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BadgeIcon(
                                    tier = badge.tier,
                                    earned = badge.earned,
                                    size = 56
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = badge.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = badge.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (badge.earned && badge.earnedAt != null) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Earned on ${badge.earnedAt.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = StatusPublished
                                        )
                                    }

                                    if (!badge.earned && badge.progress != null) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        LinearProgressIndicator(
                                            progress = { badge.progress.percentage },
                                            modifier = Modifier.fillMaxWidth(),
                                        )
                                        Text(
                                            text = "${(badge.progress.percentage * 100).toInt()}% complete",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (badge.earned) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Earned",
                                        tint = when (badge.tier) {
                                            BadgeTier.BRONZE -> BadgeBronze
                                            BadgeTier.SILVER -> BadgeSilver
                                            BadgeTier.GOLD -> BadgeGold
                                            BadgeTier.PLATINUM -> BadgePlatinum
                                        }
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // SQC Fidelity Grading Section (Australian Standards v5.2.0)
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        SectionHeader(title = stringResource(R.string.sqc_fidelity_grading))
                    }

                    // Sample SQC Fidelity Card - In a real app, this would come from ViewModel
                    item {
                        val sampleMetrics = SQCFidelityMetrics(
                            singleQubitGateFidelity = 99.8,
                            twoQubitGateFidelity = 99.5,
                            readoutFidelity = 99.7,
                            overallFidelity = 99.6,
                            coherenceTime = 100.0,
                            gateTime = 50.0,
                            measurementDate = "2026-01-29"
                        )

                        val sampleCertification = AustralianStandardsCertification(
                            fidelityGrade = FidelityGrade.GOLD,
                            measuredFidelity = 99.6,
                            certificationDate = "2026-01-01",
                            expiryDate = "2027-01-01",
                            certificateId = "SQC-2026-12345"
                        )

                        SQCFidelityCard(
                            metrics = sampleMetrics,
                            certification = sampleCertification
                        )
                    }

                    // SQC Fidelity Grade Legend
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        SQCFidelityGradeLegend()
                    }

                    // Australian Quantum Credits
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        SectionHeader(title = stringResource(R.string.australian_quantum_credits))
                    }

                    item {
                        AustralianQuantumCreditsCard(
                            credits = AustralianQuantumCredits.createDefault()
                        )
                    }

                    // Bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}
