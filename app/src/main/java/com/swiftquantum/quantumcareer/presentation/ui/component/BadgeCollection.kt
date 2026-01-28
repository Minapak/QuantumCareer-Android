package com.swiftquantum.quantumcareer.presentation.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swiftquantum.quantumcareer.domain.model.BadgeProgress
import com.swiftquantum.quantumcareer.domain.model.BadgeTier
import com.swiftquantum.quantumcareer.domain.model.CareerBadge
import com.swiftquantum.quantumcareer.presentation.ui.theme.*

@Composable
fun BadgeCollectionView(
    badges: List<CareerBadge>,
    currentTier: BadgeTier,
    onBadgeClick: (CareerBadge) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Badge Collection",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(badges) { badge ->
                BadgeItem(
                    badge = badge,
                    isCurrent = badge.tier == currentTier,
                    onClick = { onBadgeClick(badge) }
                )
            }
        }
    }
}

@Composable
fun BadgeItem(
    badge: CareerBadge,
    isCurrent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isCurrent) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Column(
        modifier = modifier
            .scale(scale)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BadgeIcon(
            tier = badge.tier,
            earned = badge.earned,
            size = 72,
            showGlow = isCurrent && badge.earned
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = badge.tier.displayName,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
            color = if (badge.earned) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )

        if (badge.earned) {
            Text(
                text = "Earned",
                style = MaterialTheme.typography.labelSmall,
                color = StatusPublished
            )
        } else {
            badge.progress?.let { progress ->
                Text(
                    text = "${(progress.percentage * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun BadgeIcon(
    tier: BadgeTier,
    earned: Boolean,
    size: Int,
    showGlow: Boolean = false,
    modifier: Modifier = Modifier
) {
    val color = when (tier) {
        BadgeTier.BRONZE -> BadgeBronze
        BadgeTier.SILVER -> BadgeSilver
        BadgeTier.GOLD -> BadgeGold
        BadgeTier.PLATINUM -> BadgePlatinum
    }

    val icon = when (tier) {
        BadgeTier.BRONZE -> Icons.Default.Star
        BadgeTier.SILVER -> Icons.Default.EmojiEvents
        BadgeTier.GOLD -> Icons.Default.WorkspacePremium
        BadgeTier.PLATINUM -> Icons.Default.WorkspacePremium
    }

    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        if (showGlow) {
            Canvas(modifier = Modifier.size((size + 16).dp)) {
                drawCircle(
                    color = color.copy(alpha = glowAlpha),
                    radius = (size / 2 + 8).dp.toPx()
                )
            }
        }

        Surface(
            modifier = Modifier.size(size.dp),
            shape = CircleShape,
            color = if (earned) color else color.copy(alpha = 0.3f),
            shadowElevation = if (earned) 4.dp else 0.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (earned) 1f else 0.5f),
                contentAlignment = Alignment.Center
            ) {
                if (earned) {
                    Icon(
                        imageVector = icon,
                        contentDescription = tier.displayName,
                        modifier = Modifier.size((size * 0.5f).dp),
                        tint = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        modifier = Modifier.size((size * 0.4f).dp),
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun BadgeProgressCard(
    badge: CareerBadge,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                BadgeIcon(
                    tier = badge.tier,
                    earned = badge.earned,
                    size = 48
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
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
                }
            }

            badge.progress?.let { progress ->
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Progress to ${badge.tier.displayName}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Publications progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Publications",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "${progress.publicationsCurrent}/${progress.publicationsRequired}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { progress.publicationsProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = when (badge.tier) {
                        BadgeTier.BRONZE -> BadgeBronze
                        BadgeTier.SILVER -> BadgeSilver
                        BadgeTier.GOLD -> BadgeGold
                        BadgeTier.PLATINUM -> BadgePlatinum
                    },
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Citations progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Citations",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "${progress.citationsCurrent}/${progress.citationsRequired}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { progress.citationsProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = when (badge.tier) {
                        BadgeTier.BRONZE -> BadgeBronze
                        BadgeTier.SILVER -> BadgeSilver
                        BadgeTier.GOLD -> BadgeGold
                        BadgeTier.PLATINUM -> BadgePlatinum
                    },
                )
            }
        }
    }
}

@Composable
fun CurrentBadgeDisplay(
    tier: BadgeTier,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BadgeIcon(
            tier = tier,
            earned = true,
            size = 32
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = tier.displayName,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BadgeCollectionPreview() {
    QuantumCareerTheme {
        val badges = listOf(
            CareerBadge(
                id = "1",
                tier = BadgeTier.BRONZE,
                name = "Bronze Researcher",
                description = "Published your first circuit",
                earned = true,
                earnedAt = null,
                progress = null
            ),
            CareerBadge(
                id = "2",
                tier = BadgeTier.SILVER,
                name = "Silver Researcher",
                description = "5 publications + 10 citations",
                earned = true,
                earnedAt = null,
                progress = null
            ),
            CareerBadge(
                id = "3",
                tier = BadgeTier.GOLD,
                name = "Gold Researcher",
                description = "20 publications + 50 citations",
                earned = false,
                earnedAt = null,
                progress = BadgeProgress(
                    publicationsRequired = 20,
                    publicationsCurrent = 12,
                    citationsRequired = 50,
                    citationsCurrent = 35,
                    percentage = 0.6f
                )
            ),
            CareerBadge(
                id = "4",
                tier = BadgeTier.PLATINUM,
                name = "Platinum Researcher",
                description = "50 publications + 200 citations",
                earned = false,
                earnedAt = null,
                progress = BadgeProgress(
                    publicationsRequired = 50,
                    publicationsCurrent = 12,
                    citationsRequired = 200,
                    citationsCurrent = 35,
                    percentage = 0.2f
                )
            )
        )

        Column(modifier = Modifier.padding(16.dp)) {
            BadgeCollectionView(
                badges = badges,
                currentTier = BadgeTier.SILVER,
                onBadgeClick = {}
            )
        }
    }
}
