package com.swiftquantum.quantumcareer.presentation.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swiftquantum.quantumcareer.R
import com.swiftquantum.quantumcareer.domain.model.AustralianQuantumCredits
import com.swiftquantum.quantumcareer.domain.model.AustralianStandardsCertification
import com.swiftquantum.quantumcareer.domain.model.FidelityGrade
import com.swiftquantum.quantumcareer.domain.model.SQCFidelityMetrics
import com.swiftquantum.quantumcareer.presentation.ui.theme.*

/**
 * Returns the appropriate color for a fidelity grade
 */
@Composable
fun FidelityGrade.getColor(): Color {
    return when (this) {
        FidelityGrade.PLATINUM -> FidelityPlatinum
        FidelityGrade.GOLD -> FidelityGold
        FidelityGrade.SILVER -> FidelitySilver
        FidelityGrade.BRONZE -> FidelityBronze
        FidelityGrade.STANDARD -> FidelityStandard
        FidelityGrade.DEVELOPING -> FidelityDeveloping
    }
}

/**
 * Returns the appropriate icon for a fidelity grade
 */
fun FidelityGrade.getIcon(): ImageVector {
    return when (this) {
        FidelityGrade.PLATINUM -> Icons.Default.Diamond
        FidelityGrade.GOLD -> Icons.Default.WorkspacePremium
        FidelityGrade.SILVER -> Icons.Default.EmojiEvents
        FidelityGrade.BRONZE -> Icons.Default.Star
        FidelityGrade.STANDARD -> Icons.Default.Verified
        FidelityGrade.DEVELOPING -> Icons.Default.TrendingUp
    }
}

/**
 * SQC Fidelity Badge displaying the current fidelity grade
 */
@Composable
fun SQCFidelityBadge(
    grade: FidelityGrade,
    fidelityPercentage: Double,
    modifier: Modifier = Modifier,
    showGlow: Boolean = true,
    size: Int = 80
) {
    val color = grade.getColor()
    val icon = grade.getIcon()

    val infiniteTransition = rememberInfiniteTransition(label = "fidelity_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glow effect for higher grades
        if (showGlow && grade.ordinal <= FidelityGrade.SILVER.ordinal) {
            Canvas(modifier = Modifier.size((size + 16).dp)) {
                drawCircle(
                    color = color.copy(alpha = glowAlpha * 0.5f),
                    radius = (size / 2 + 8).dp.toPx()
                )
            }
        }

        // Badge background
        Surface(
            modifier = Modifier.size(size.dp),
            shape = CircleShape,
            color = color.copy(alpha = 0.2f),
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 3.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(color, color.copy(alpha = 0.6f))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = grade.displayName,
                        modifier = Modifier.size((size * 0.35f).dp),
                        tint = color
                    )
                    Text(
                        text = "${String.format("%.1f", fidelityPercentage)}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
            }
        }
    }
}

/**
 * Compact SQC Fidelity Badge for inline display
 */
@Composable
fun SQCFidelityBadgeCompact(
    grade: FidelityGrade,
    modifier: Modifier = Modifier
) {
    val color = grade.getColor()

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = grade.getIcon(),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = color
            )
            Text(
                text = grade.displayName,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

/**
 * SQC Fidelity Card with detailed metrics
 */
@Composable
fun SQCFidelityCard(
    metrics: SQCFidelityMetrics,
    certification: AustralianStandardsCertification?,
    modifier: Modifier = Modifier
) {
    val grade = metrics.grade
    val color = grade.getColor()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.sqc_fidelity_grade),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = grade.displayName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                    Text(
                        text = stringResource(R.string.australian_standards_version, AustralianQuantumCredits.STANDARDS_VERSION),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                SQCFidelityBadge(
                    grade = grade,
                    fidelityPercentage = metrics.overallFidelity,
                    size = 72
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Fidelity Metrics
            Text(
                text = stringResource(R.string.fidelity_metrics),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            FidelityMetricRow(
                label = stringResource(R.string.overall_fidelity),
                value = metrics.formattedOverallFidelity,
                progress = (metrics.overallFidelity / 100f).toFloat(),
                color = color
            )

            Spacer(modifier = Modifier.height(8.dp))

            FidelityMetricRow(
                label = stringResource(R.string.single_qubit_fidelity),
                value = metrics.formattedSingleQubitFidelity,
                progress = (metrics.singleQubitGateFidelity / 100f).toFloat(),
                color = FidelityGrade.fromPercentage(metrics.singleQubitGateFidelity).getColor()
            )

            Spacer(modifier = Modifier.height(8.dp))

            FidelityMetricRow(
                label = stringResource(R.string.two_qubit_fidelity),
                value = metrics.formattedTwoQubitFidelity,
                progress = (metrics.twoQubitGateFidelity / 100f).toFloat(),
                color = FidelityGrade.fromPercentage(metrics.twoQubitGateFidelity).getColor()
            )

            Spacer(modifier = Modifier.height(8.dp))

            FidelityMetricRow(
                label = stringResource(R.string.readout_fidelity),
                value = metrics.formattedReadoutFidelity,
                progress = (metrics.readoutFidelity / 100f).toFloat(),
                color = FidelityGrade.fromPercentage(metrics.readoutFidelity).getColor()
            )

            // Next grade progress
            grade.nextGrade?.let { nextGrade ->
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.progress_to_next_grade, nextGrade.displayName),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                val pointsNeeded = nextGrade.minFidelity - metrics.overallFidelity
                Text(
                    text = stringResource(R.string.points_needed_format, String.format("%.2f", pointsNeeded)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { certification?.progressToNextGrade ?: 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = nextGrade.getColor()
                )
            }

            // Certification info
            certification?.let { cert ->
                if (cert.isActive) {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.sqc_certified),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                            Text(
                                text = stringResource(R.string.certificate_id_format, cert.certificateId),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Fidelity metric row with progress bar
 */
@Composable
private fun FidelityMetricRow(
    label: String,
    value: String,
    progress: Float,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}

/**
 * SQC Fidelity Grade Legend
 */
@Composable
fun SQCFidelityGradeLegend(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.sqc_fidelity_grades),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.australian_standards_version, AustralianQuantumCredits.STANDARDS_VERSION),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            FidelityGrade.entries.forEach { grade ->
                FidelityGradeLegendRow(grade = grade)
                if (grade != FidelityGrade.DEVELOPING) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun FidelityGradeLegendRow(grade: FidelityGrade) {
    val color = grade.getColor()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = color.copy(alpha = 0.3f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = grade.getIcon(),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = color
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = grade.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = when (grade) {
                    FidelityGrade.PLATINUM -> ">= 99.9%"
                    FidelityGrade.GOLD -> ">= 99.5%"
                    FidelityGrade.SILVER -> ">= 99.0%"
                    FidelityGrade.BRONZE -> ">= 98.0%"
                    FidelityGrade.STANDARD -> ">= 95.0%"
                    FidelityGrade.DEVELOPING -> "< 95.0%"
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Australian Quantum Credits display
 */
@Composable
fun AustralianQuantumCreditsCard(
    credits: AustralianQuantumCredits = AustralianQuantumCredits.createDefault(),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = null,
                    tint = SQCBlue
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.australian_quantum_credits),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SQC
            CreditOrganizationRow(
                name = credits.sqc.companyName,
                description = credits.sqc.description,
                icon = Icons.Default.Memory
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Q-CTRL
            CreditOrganizationRow(
                name = credits.qCtrl.companyName,
                description = credits.qCtrl.description,
                icon = Icons.Default.Settings
            )

            Spacer(modifier = Modifier.height(12.dp))

            // LabScript
            CreditOrganizationRow(
                name = credits.labScript.projectName,
                description = credits.labScript.description,
                icon = Icons.Default.Code
            )

            Spacer(modifier = Modifier.height(12.dp))

            // MicroQiskit
            CreditOrganizationRow(
                name = credits.microQiskit.projectName,
                description = credits.microQiskit.description,
                icon = Icons.Default.School
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.standards_version_format, credits.version),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CreditOrganizationRow(
    name: String,
    description: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SQCFidelityBadgePreview() {
    QuantumCareerTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SQCFidelityBadge(
                    grade = FidelityGrade.PLATINUM,
                    fidelityPercentage = 99.95
                )
                SQCFidelityBadge(
                    grade = FidelityGrade.GOLD,
                    fidelityPercentage = 99.7
                )
                SQCFidelityBadge(
                    grade = FidelityGrade.SILVER,
                    fidelityPercentage = 99.3
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SQCFidelityBadgeCompact(grade = FidelityGrade.PLATINUM)
                SQCFidelityBadgeCompact(grade = FidelityGrade.GOLD)
                SQCFidelityBadgeCompact(grade = FidelityGrade.SILVER)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SQCFidelityCardPreview() {
    QuantumCareerTheme {
        SQCFidelityCard(
            metrics = SQCFidelityMetrics(
                singleQubitGateFidelity = 99.8,
                twoQubitGateFidelity = 99.5,
                readoutFidelity = 99.7,
                overallFidelity = 99.6,
                coherenceTime = 100.0,
                gateTime = 50.0,
                measurementDate = "2026-01-29"
            ),
            certification = AustralianStandardsCertification(
                fidelityGrade = FidelityGrade.GOLD,
                measuredFidelity = 99.6,
                certificationDate = "2026-01-01",
                expiryDate = "2027-01-01",
                certificateId = "SQC-2026-12345"
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
