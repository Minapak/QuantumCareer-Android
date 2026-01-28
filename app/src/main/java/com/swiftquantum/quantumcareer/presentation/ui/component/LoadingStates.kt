package com.swiftquantum.quantumcareer.presentation.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.swiftquantum.quantumcareer.presentation.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Loading State Components for SwiftQuantum Ecosystem
 */

@Composable
fun QuantumLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    primaryColor: Color = CareerGold,
    secondaryColor: Color = SwiftPurple,
    tertiaryColor: Color = BridgeCyan
) {
    val infiniteTransition = rememberInfiniteTransition(label = "quantum_loader")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "main_rotation"
    )

    val counterRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "counter_rotation"
    )

    val centerPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "center_pulse"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotation)
        ) {
            listOf(0f, 120f, 240f).forEachIndexed { index, angle ->
                val colors = listOf(primaryColor, secondaryColor, tertiaryColor)
                Box(
                    modifier = Modifier
                        .size(size * 0.15f)
                        .align(Alignment.Center)
                        .graphicsLayer {
                            val radians = (angle * PI / 180).toFloat()
                            translationX = cos(radians) * (size.toPx() * 0.4f)
                            translationY = sin(radians) * (size.toPx() * 0.4f)
                        }
                        .clip(CircleShape)
                        .background(colors[index])
                )
            }
        }

        Box(
            modifier = Modifier
                .size(size * 0.6f)
                .rotate(counterRotation)
        ) {
            listOf(0f, 180f).forEachIndexed { index, angle ->
                val colors = listOf(secondaryColor.copy(alpha = 0.7f), tertiaryColor.copy(alpha = 0.7f))
                Box(
                    modifier = Modifier
                        .size(size * 0.1f)
                        .align(Alignment.Center)
                        .graphicsLayer {
                            val radians = (angle * PI / 180).toFloat()
                            translationX = cos(radians) * (size.toPx() * 0.25f)
                            translationY = sin(radians) * (size.toPx() * 0.25f)
                        }
                        .clip(CircleShape)
                        .background(colors[index])
                )
            }
        }

        Box(
            modifier = Modifier
                .size(size * 0.2f)
                .scale(centerPulse)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryColor,
                            primaryColor.copy(alpha = 0.5f)
                        )
                    )
                )
        )
    }
}

@Composable
fun PulsingDot(
    modifier: Modifier = Modifier,
    color: Color = CareerGold,
    size: Dp = 12.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsing_dot")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .alpha(alpha)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun PulsingDotsLoader(
    modifier: Modifier = Modifier,
    color: Color = CareerGold,
    dotCount: Int = 3,
    dotSize: Dp = 8.dp,
    spacing: Dp = 8.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsing_dots")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(dotCount) { index ->
            val delay = index * 150
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 600,
                        delayMillis = delay,
                        easing = EaseInOutCubic
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_$index"
            )

            Box(
                modifier = Modifier
                    .size(dotSize)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
fun WaveformLoader(
    modifier: Modifier = Modifier,
    color: Color = CareerGold,
    barCount: Int = 5,
    barWidth: Dp = 4.dp,
    maxHeight: Dp = 32.dp,
    minHeight: Dp = 8.dp,
    spacing: Dp = 4.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(barCount) { index ->
            val delay = index * 100
            val heightFraction by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 500,
                        delayMillis = delay,
                        easing = EaseInOutCubic
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$index"
            )

            val height = minHeight + (maxHeight - minHeight) * heightFraction

            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(height)
                    .clip(RoundedCornerShape(barWidth / 2))
                    .background(color)
            )
        }
    }
}

@Composable
fun SkeletonLoader(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp)
) {
    val shimmerColors = listOf(
        SurfaceDarkVariant,
        SurfaceDarkVariant.copy(alpha = 0.5f),
        SurfaceDarkVariant
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200f, translateAnim - 200f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

@Composable
fun CardSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SkeletonLoader(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(20.dp)
        )
        SkeletonLoader(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(14.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        repeat(3) {
            SkeletonLoader(
                modifier = Modifier
                    .fillMaxWidth(if (it == 2) 0.8f else 1f)
                    .height(12.dp)
            )
        }
    }
}

@Composable
fun ListItemSkeleton(
    modifier: Modifier = Modifier,
    hasAvatar: Boolean = true,
    hasTrailingContent: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hasAvatar) {
            SkeletonLoader(
                modifier = Modifier.size(48.dp),
                shape = CircleShape
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SkeletonLoader(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(16.dp)
            )
            SkeletonLoader(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(12.dp)
            )
        }

        if (hasTrailingContent) {
            SkeletonLoader(
                modifier = Modifier
                    .width(60.dp)
                    .height(32.dp),
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
fun FullScreenLoading(
    modifier: Modifier = Modifier,
    message: String? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuantumLoadingIndicator(size = 80.dp)

            message?.let {
                androidx.compose.material3.Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}
