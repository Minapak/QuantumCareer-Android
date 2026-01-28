package com.swiftquantum.quantumcareer.presentation.ui.screen.certificate

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.swiftquantum.quantumcareer.domain.model.*
import com.swiftquantum.quantumcareer.presentation.ui.component.*
import com.swiftquantum.quantumcareer.presentation.ui.theme.*
import com.swiftquantum.quantumcareer.presentation.viewmodel.CertificateViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificateDetailScreen(
    certificateId: String,
    viewModel: CertificateViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showShareSheet by remember { mutableStateOf(false) }

    LaunchedEffect(certificateId) {
        viewModel.selectCertificate(certificateId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Certificate") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearSelectedCertificate()
                        onNavigateBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showShareSheet = true }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
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
                    onRetry = { viewModel.selectCertificate(certificateId) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.selectedCertificate != null -> {
                val certificate = uiState.selectedCertificate!!
                val dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Certificate Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = when (certificate.tier) {
                                BadgeTier.BRONZE -> BadgeBronze.copy(alpha = 0.1f)
                                BadgeTier.SILVER -> BadgeSilver.copy(alpha = 0.15f)
                                BadgeTier.GOLD -> BadgeGold.copy(alpha = 0.1f)
                                BadgeTier.PLATINUM -> BadgePlatinum.copy(alpha = 0.15f)
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Certificate header
                            Text(
                                text = "QUANTUM COMPUTING",
                                style = MaterialTheme.typography.labelLarge,
                                letterSpacing = 4.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "CERTIFICATION",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Badge
                            BadgeIcon(
                                tier = certificate.tier,
                                earned = !certificate.isExpired,
                                size = 100,
                                showGlow = !certificate.isExpired
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = certificate.tier.displayName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Certificate holder
                            Text(
                                text = "This is to certify that",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = certificate.userName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "has successfully completed the Quantum Computing Certification with a score of ${certificate.scorePercentage.toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            HorizontalDivider()

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Issue Date",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = certificate.issueDate.format(dateFormatter),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Expiry Date",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = certificate.expiryDate.format(dateFormatter),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = if (certificate.isExpired) StatusRejected else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Status Card
                    if (certificate.isExpired) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = StatusRejected.copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = StatusRejected
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Certificate Expired",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusRejected
                                    )
                                    Text(
                                        text = "Retake the certification test to renew",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // QR Code for verification
                    SectionHeader(title = "Verification")

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // QR Code
                            QRCodeImage(
                                content = "https://swiftquantum.tech/verify/${certificate.verificationCode}",
                                size = 180
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Scan to verify",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            HorizontalDivider()

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Verification Code",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = certificate.formattedVerificationCode,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                IconButton(onClick = { /* Copy to clipboard */ }) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy"
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "DOI",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = certificate.doiDisplay,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                IconButton(onClick = { /* Open DOI URL */ }) {
                                    Icon(
                                        imageVector = Icons.Default.OpenInNew,
                                        contentDescription = "Open"
                                    )
                                }
                            }
                        }
                    }

                    // Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.loadCertificatePdf() },
                            modifier = Modifier.weight(1f)
                        ) {
                            if (uiState.isLoadingPdf) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Download, contentDescription = null)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Download PDF")
                        }

                        Button(
                            onClick = { showShareSheet = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Share")
                        }
                    }
                }
            }
        }
    }

    // Share bottom sheet
    if (showShareSheet && uiState.selectedCertificate != null) {
        ShareCertificateSheet(
            onShare = { shareType ->
                viewModel.shareCertificate(shareType)
                showShareSheet = false
            },
            onDismiss = { showShareSheet = false }
        )
    }
}

@Composable
private fun QRCodeImage(
    content: String,
    size: Int,
    modifier: Modifier = Modifier
) {
    val qrBitmap = remember(content) {
        generateQRCode(content, size)
    }

    qrBitmap?.let { bitmap ->
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "QR Code",
            modifier = modifier
                .size(size.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(8.dp)
        )
    }
}

private fun generateQRCode(content: String, size: Int): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size * 3, size * 3)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(
                    x, y,
                    if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                )
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShareCertificateSheet(
    onShare: (CertificateShareType) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Share Certificate",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            CertificateShareType.entries.forEach { shareType ->
                ListItem(
                    headlineContent = { Text(shareType.displayName) },
                    leadingContent = {
                        Icon(
                            imageVector = when (shareType) {
                                CertificateShareType.LINKEDIN -> Icons.Default.Person
                                CertificateShareType.TWITTER -> Icons.Default.Share
                                CertificateShareType.EMAIL -> Icons.Default.Email
                                CertificateShareType.DOWNLOAD_PDF -> Icons.Default.Download
                                CertificateShareType.COPY_LINK -> Icons.Default.ContentCopy
                            },
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable { onShare(shareType) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
