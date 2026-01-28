package com.swiftquantum.quantumcareer.presentation.ui.screen.certificate

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.swiftquantum.quantumcareer.domain.model.*
import com.swiftquantum.quantumcareer.presentation.ui.component.*
import com.swiftquantum.quantumcareer.presentation.ui.theme.*
import com.swiftquantum.quantumcareer.presentation.viewmodel.CertificateViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificatesScreen(
    viewModel: CertificateViewModel = hiltViewModel(),
    onCertificateClick: (String) -> Unit,
    onVerifyClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Certificates") },
                actions = {
                    IconButton(onClick = onVerifyClick) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Verify Certificate")
                    }
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
            uiState.certificateSummary?.certificates?.isEmpty() == true -> {
                EmptyView(
                    title = "No Certificates Yet",
                    message = "Complete a certification test with a passing score to earn your first certificate.",
                    modifier = Modifier.padding(paddingValues),
                    action = {
                        Button(onClick = { /* Navigate to quiz */ }) {
                            Icon(Icons.Default.Quiz, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Take Certification Test")
                        }
                    }
                )
            }
            else -> {
                val summary = uiState.certificateSummary!!

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Summary Card
                    item {
                        CertificateSummaryCard(summary = summary)
                    }

                    // Active Certificates
                    if (summary.activeCertificates > 0) {
                        item {
                            SectionHeader(title = "Active Certificates")
                        }

                        items(summary.certificates.filter { !it.isExpired }) { certificate ->
                            CertificateCard(
                                certificate = certificate,
                                onClick = { onCertificateClick(certificate.id) }
                            )
                        }
                    }

                    // Expired Certificates
                    if (summary.expiredCertificates > 0) {
                        item {
                            SectionHeader(title = "Expired Certificates")
                        }

                        items(summary.certificates.filter { it.isExpired }) { certificate ->
                            CertificateCard(
                                certificate = certificate,
                                onClick = { onCertificateClick(certificate.id) }
                            )
                        }
                    }

                    // Verify section
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            onClick = onVerifyClick,
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
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Verify a Certificate",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Enter a verification code to validate a certificate",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null
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
private fun CertificateSummaryCard(summary: CertificateSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            summary.highestTier?.let { tier ->
                BadgeIcon(
                    tier = tier,
                    earned = true,
                    size = 80,
                    showGlow = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Highest: ${tier.displayName}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = summary.totalCertificates.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = summary.activeCertificates.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = StatusPublished
                    )
                    Text(
                        text = "Active",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = summary.expiredCertificates.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (summary.expiredCertificates > 0) StatusRejected else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Expired",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun CertificateCard(
    certificate: Certificate,
    onClick: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (certificate.isExpired) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
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
                tier = certificate.tier,
                earned = !certificate.isExpired,
                size = 56
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = certificate.certificateTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (certificate.isExpired) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = StatusRejected.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Expired",
                                style = MaterialTheme.typography.labelSmall,
                                color = StatusRejected,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Score: ${certificate.scorePercentage.toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = if (certificate.isExpired) {
                        "Expired: ${certificate.expiryDate.format(dateFormatter)}"
                    } else {
                        "Valid until: ${certificate.expiryDate.format(dateFormatter)}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (certificate.isExpired) StatusRejected else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
