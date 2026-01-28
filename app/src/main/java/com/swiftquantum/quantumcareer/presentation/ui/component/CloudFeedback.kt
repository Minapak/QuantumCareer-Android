package com.swiftquantum.quantumcareer.presentation.ui.component

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.swiftquantum.quantumcareer.presentation.ui.theme.*

/**
 * Cloud Processing Feedback Components for SwiftQuantum Ecosystem
 *
 * Provides visual feedback for cloud-based quantum computations:
 * - Connecting to quantum cloud
 * - Processing quantum circuits
 * - Success/Error states
 */

enum class CloudProcessingState {
    Connecting,
    Processing,
    Success,
    Error
}

/**
 * Main Cloud Processing Animation Composable
 * Uses Lottie animations for smooth, professional cloud feedback
 */
@Composable
fun CloudProcessingAnimation(
    state: CloudProcessingState,
    modifier: Modifier = Modifier,
    message: String? = null,
    onRetry: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null
) {
    val defaultMessage = when (state) {
        CloudProcessingState.Connecting -> "Connecting to quantum cloud..."
        CloudProcessingState.Processing -> "Quantum computation in progress..."
        CloudProcessingState.Success -> "Computation completed successfully!"
        CloudProcessingState.Error -> "An error occurred. Please try again."
    }

    val displayMessage = message ?: defaultMessage

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceDark
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animation based on state
            when (state) {
                CloudProcessingState.Connecting -> ConnectingAnimation()
                CloudProcessingState.Processing -> ProcessingAnimation()
                CloudProcessingState.Success -> SuccessAnimation()
                CloudProcessingState.Error -> ErrorAnimation()
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated text
            AnimatedContent(
                targetState = displayMessage,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                },
                label = "message_animation"
            ) { targetMessage ->
                Text(
                    text = targetMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            }

            // Progress indicator for processing states
            if (state == CloudProcessingState.Connecting || state == CloudProcessingState.Processing) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = CareerGold,
                    trackColor = CareerGold.copy(alpha = 0.2f)
                )
            }

            // Action buttons for error state
            if (state == CloudProcessingState.Error && onRetry != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    onDismiss?.let {
                        OutlinedButton(onClick = it) {
                            Text("Dismiss")
                        }
                    }
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CareerGold
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Retry", color = Color.Black)
                    }
                }
            }

            // Dismiss button for success state
            if (state == CloudProcessingState.Success && onDismiss != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SuccessGreen
                    )
                ) {
                    Text("Continue")
                }
            }
        }
    }
}

@Composable
private fun ConnectingAnimation() {
    // Placeholder Lottie URL - replace with actual asset
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Url("https://assets.lottiefiles.com/packages/lf20_UJNc2t.json")
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    if (composition == null) {
        PulsingCloudIcon(color = CareerGold)
    } else {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(120.dp)
        )
    }
}

@Composable
private fun ProcessingAnimation() {
    // Placeholder Lottie URL - replace with actual asset
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Url("https://assets.lottiefiles.com/packages/lf20_qjosmr4w.json")
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    if (composition == null) {
        QuantumProcessingIndicator()
    } else {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(120.dp)
        )
    }
}

@Composable
private fun SuccessAnimation() {
    // Placeholder Lottie URL - replace with actual asset
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Url("https://assets.lottiefiles.com/packages/lf20_jbrw3hcz.json")
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1
    )

    if (composition == null) {
        SuccessIcon()
    } else {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(120.dp)
        )
    }
}

@Composable
private fun ErrorAnimation() {
    // Placeholder Lottie URL - replace with actual asset
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Url("https://assets.lottiefiles.com/packages/lf20_yw3nyrsv.json")
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1
    )

    if (composition == null) {
        ErrorIcon()
    } else {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(120.dp)
        )
    }
}

// Fallback animations

@Composable
private fun PulsingCloudIcon(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Icon(
        imageVector = Icons.Default.Cloud,
        contentDescription = "Connecting",
        modifier = Modifier
            .size(80.dp)
            .scale(scale)
            .alpha(alpha),
        tint = color
    )
}

@Composable
private fun QuantumProcessingIndicator() {
    Box(
        modifier = Modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(80.dp),
            color = CareerGold,
            strokeWidth = 4.dp
        )
        Icon(
            imageVector = Icons.Default.Memory,
            contentDescription = "Processing",
            modifier = Modifier.size(40.dp),
            tint = CareerGold
        )
    }
}

@Composable
private fun SuccessIcon() {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = "Success",
        modifier = Modifier
            .size(80.dp)
            .scale(scale.value),
        tint = SuccessGreen
    )
}

@Composable
private fun ErrorIcon() {
    val shake = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        repeat(3) {
            shake.animateTo(10f, animationSpec = tween(50))
            shake.animateTo(-10f, animationSpec = tween(50))
        }
        shake.animateTo(0f, animationSpec = tween(50))
    }

    Icon(
        imageVector = Icons.Default.Error,
        contentDescription = "Error",
        modifier = Modifier
            .size(80.dp)
            .offset(x = shake.value.dp),
        tint = ErrorRed
    )
}

/**
 * Compact cloud processing indicator for inline usage
 */
@Composable
fun CloudProcessingChip(
    state: CloudProcessingState,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (state) {
        CloudProcessingState.Connecting -> CareerGold.copy(alpha = 0.1f)
        CloudProcessingState.Processing -> CareerGold.copy(alpha = 0.2f)
        CloudProcessingState.Success -> SuccessGreen.copy(alpha = 0.1f)
        CloudProcessingState.Error -> ErrorRed.copy(alpha = 0.1f)
    }

    val contentColor = when (state) {
        CloudProcessingState.Connecting -> CareerGold
        CloudProcessingState.Processing -> CareerGold
        CloudProcessingState.Success -> SuccessGreen
        CloudProcessingState.Error -> ErrorRed
    }

    val icon = when (state) {
        CloudProcessingState.Connecting -> Icons.Default.CloudSync
        CloudProcessingState.Processing -> Icons.Default.Memory
        CloudProcessingState.Success -> Icons.Default.CloudDone
        CloudProcessingState.Error -> Icons.Default.CloudOff
    }

    val label = when (state) {
        CloudProcessingState.Connecting -> "Connecting..."
        CloudProcessingState.Processing -> "Processing..."
        CloudProcessingState.Success -> "Complete"
        CloudProcessingState.Error -> "Error"
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state == CloudProcessingState.Connecting || state == CloudProcessingState.Processing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = contentColor,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = contentColor
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor
            )
        }
    }
}
