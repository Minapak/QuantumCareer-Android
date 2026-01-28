package com.swiftquantum.quantumcareer.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swiftquantum.quantumcareer.domain.model.PeerReview
import com.swiftquantum.quantumcareer.domain.model.ReviewDecision
import com.swiftquantum.quantumcareer.domain.model.ReviewStatus
import com.swiftquantum.quantumcareer.domain.model.ReviewerLevel
import com.swiftquantum.quantumcareer.presentation.ui.theme.QuantumCareerTheme
import com.swiftquantum.quantumcareer.presentation.ui.theme.StatusPublished
import com.swiftquantum.quantumcareer.presentation.ui.theme.StatusRejected
import com.swiftquantum.quantumcareer.presentation.ui.theme.StatusUnderReview
import java.time.LocalDateTime

@Composable
fun ReviewCard(
    review: PeerReview,
    onClaim: (() -> Unit)? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
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
                Text(
                    text = review.circuitTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                ReviewStatusChip(status = review.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "by ${review.circuitAuthor}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (review.status == ReviewStatus.PENDING && onClaim != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onClaim,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AssignmentInd,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Claim Review")
                }
            }

            review.decision?.let { decision ->
                Spacer(modifier = Modifier.height(8.dp))
                ReviewDecisionChip(decision = decision)
            }
        }
    }
}

@Composable
fun ReviewStatusChip(
    status: ReviewStatus,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (status) {
        ReviewStatus.PENDING -> StatusUnderReview to "Pending"
        ReviewStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary to "In Progress"
        ReviewStatus.COMPLETED -> StatusPublished to "Completed"
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
fun ReviewDecisionChip(
    decision: ReviewDecision,
    modifier: Modifier = Modifier
) {
    val (color, icon) = when (decision) {
        ReviewDecision.APPROVED -> StatusPublished to Icons.Default.CheckCircle
        ReviewDecision.REQUEST_CHANGES -> StatusUnderReview to Icons.Default.Edit
        ReviewDecision.REJECTED -> StatusRejected to Icons.Default.Cancel
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = color
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = decision.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

@Composable
fun ReviewSubmitForm(
    qasmCode: String?,
    selectedDecision: ReviewDecision?,
    comment: String,
    technicalScore: Int,
    innovationScore: Int,
    documentationScore: Int,
    onDecisionChange: (ReviewDecision) -> Unit,
    onCommentChange: (String) -> Unit,
    onTechnicalScoreChange: (Int) -> Unit,
    onInnovationScoreChange: (Int) -> Unit,
    onDocumentationScoreChange: (Int) -> Unit,
    onSubmit: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        // QASM Code Preview
        if (!qasmCode.isNullOrBlank()) {
            Text(
                text = "Circuit Code",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = qasmCode,
                    modifier = Modifier
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState()),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Decision Selection
        Text(
            text = "Your Decision",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ReviewDecision.entries.forEach { decision ->
                DecisionButton(
                    decision = decision,
                    isSelected = selectedDecision == decision,
                    onClick = { onDecisionChange(decision) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Scoring
        Text(
            text = "Scores",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        ScoreSlider(
            label = "Technical Quality",
            value = technicalScore,
            onValueChange = onTechnicalScoreChange
        )

        ScoreSlider(
            label = "Innovation",
            value = innovationScore,
            onValueChange = onInnovationScoreChange
        )

        ScoreSlider(
            label = "Documentation",
            value = documentationScore,
            onValueChange = onDocumentationScoreChange
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Comment
        Text(
            text = "Review Comment",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = comment,
            onValueChange = onCommentChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            placeholder = { Text("Provide detailed feedback...") },
            maxLines = 6
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedDecision != null && comment.isNotBlank() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit Review")
            }
        }
    }
}

@Composable
private fun DecisionButton(
    decision: ReviewDecision,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (color, icon) = when (decision) {
        ReviewDecision.APPROVED -> StatusPublished to Icons.Default.ThumbUp
        ReviewDecision.REQUEST_CHANGES -> StatusUnderReview to Icons.Default.Edit
        ReviewDecision.REJECTED -> StatusRejected to Icons.Default.ThumbDown
    }

    val backgroundColor = if (isSelected) color.copy(alpha = 0.2f) else Color.Transparent
    val borderColor = if (isSelected) color else MaterialTheme.colorScheme.outline

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when (decision) {
                    ReviewDecision.APPROVED -> "Approve"
                    ReviewDecision.REQUEST_CHANGES -> "Changes"
                    ReviewDecision.REJECTED -> "Reject"
                },
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ScoreSlider(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "$value/5",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 1f..5f,
            steps = 3
        )
    }
}

@Composable
fun ReviewerLevelBadge(
    level: ReviewerLevel,
    modifier: Modifier = Modifier
) {
    val color = when (level) {
        ReviewerLevel.JUNIOR -> MaterialTheme.colorScheme.tertiary
        ReviewerLevel.SENIOR -> MaterialTheme.colorScheme.secondary
        ReviewerLevel.EXPERT -> MaterialTheme.colorScheme.primary
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (level) {
                    ReviewerLevel.JUNIOR -> Icons.Default.School
                    ReviewerLevel.SENIOR -> Icons.Default.Verified
                    ReviewerLevel.EXPERT -> Icons.Default.Stars
                },
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = color
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = level.displayName,
                style = MaterialTheme.typography.labelMedium,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewCardPreview() {
    QuantumCareerTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ReviewCard(
                review = PeerReview(
                    id = "1",
                    circuitId = "c1",
                    circuitTitle = "Quantum Fourier Transform",
                    circuitAuthor = "Dr. Alice Smith",
                    reviewerId = null,
                    reviewerName = null,
                    reviewerLevel = null,
                    status = ReviewStatus.PENDING,
                    decision = null,
                    comment = null,
                    submittedAt = null,
                    createdAt = LocalDateTime.now(),
                    qasmCode = null
                ),
                onClaim = {},
                onClick = {}
            )

            ReviewerLevelBadge(level = ReviewerLevel.SENIOR)
        }
    }
}
