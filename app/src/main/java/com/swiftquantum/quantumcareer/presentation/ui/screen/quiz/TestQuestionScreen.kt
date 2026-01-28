package com.swiftquantum.quantumcareer.presentation.ui.screen.quiz

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.swiftquantum.quantumcareer.domain.model.*
import com.swiftquantum.quantumcareer.presentation.ui.component.*
import com.swiftquantum.quantumcareer.presentation.ui.theme.*
import com.swiftquantum.quantumcareer.presentation.viewmodel.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestQuestionScreen(
    viewModel: QuizViewModel = hiltViewModel(),
    onTestFinished: () -> Unit,
    onAbandon: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAbandonDialog by remember { mutableStateOf(false) }

    val session = uiState.session
    val question = uiState.currentQuestion

    LaunchedEffect(uiState.testResult) {
        if (uiState.testResult != null) {
            onTestFinished()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Question ${(session?.currentIndex ?: 0) + 1}")
                        Text(
                            text = "/${session?.totalQuestions ?: 50}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showAbandonDialog = true }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    // Timer
                    TimerDisplay(
                        timeRemainingSeconds = uiState.timeRemainingSeconds,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Progress indicator
            session?.let {
                LinearProgressIndicator(
                    progress = { it.progress },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            when {
                uiState.isLoading -> {
                    LoadingView(message = "Loading question...")
                }
                question == null -> {
                    ErrorView(
                        message = "No question available",
                        onRetry = { viewModel.refresh() }
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Question metadata
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DifficultyChip(difficulty = question.difficulty)
                            CategoryChip(category = question.category)
                        }

                        // Question text
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = question.text,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(20.dp)
                            )
                        }

                        // Points indicator
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "${question.points} ${if (question.points == 1) "point" else "points"}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Answer options
                        question.options.forEachIndexed { index, option ->
                            AnswerOption(
                                index = index,
                                text = option,
                                isSelected = uiState.selectedOption == index,
                                isCorrect = if (uiState.showExplanation) {
                                    index == uiState.lastAnswerResult?.correctAnswer
                                } else null,
                                isUserAnswer = if (uiState.showExplanation) {
                                    index == uiState.selectedOption
                                } else false,
                                enabled = !uiState.showExplanation && !uiState.isSubmitting,
                                onClick = { viewModel.selectOption(index) }
                            )
                        }

                        // Explanation (shown after answer)
                        AnimatedVisibility(
                            visible = uiState.showExplanation,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (uiState.lastAnswerResult?.isCorrect == true) {
                                        StatusPublished.copy(alpha = 0.1f)
                                    } else {
                                        StatusRejected.copy(alpha = 0.1f)
                                    }
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (uiState.lastAnswerResult?.isCorrect == true) {
                                                Icons.Default.CheckCircle
                                            } else {
                                                Icons.Default.Cancel
                                            },
                                            contentDescription = null,
                                            tint = if (uiState.lastAnswerResult?.isCorrect == true) {
                                                StatusPublished
                                            } else {
                                                StatusRejected
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (uiState.lastAnswerResult?.isCorrect == true) {
                                                "Correct!"
                                            } else {
                                                "Incorrect"
                                            },
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = uiState.lastAnswerResult?.explanation ?: question.explanation,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Action button
                        if (uiState.showExplanation) {
                            Button(
                                onClick = { viewModel.nextQuestion() },
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Text(
                                    text = if (uiState.lastAnswerResult?.isLastQuestion == true) {
                                        "Finish Test"
                                    } else {
                                        "Next Question"
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null
                                )
                            }
                        } else {
                            Button(
                                onClick = { viewModel.submitAnswer() },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = uiState.selectedOption != null && !uiState.isSubmitting,
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                if (uiState.isSubmitting) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Text("Submit Answer")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Abandon confirmation dialog
    if (showAbandonDialog) {
        AlertDialog(
            onDismissRequest = { showAbandonDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = StatusRejected
                )
            },
            title = { Text("Abandon Test?") },
            text = {
                Text("Are you sure you want to abandon this test? Your progress will be lost and this will count as an incomplete attempt.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAbandonDialog = false
                        viewModel.abandonTest()
                        onAbandon()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StatusRejected
                    )
                ) {
                    Text("Abandon")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAbandonDialog = false }) {
                    Text("Continue Test")
                }
            }
        )
    }
}

@Composable
private fun TimerDisplay(
    timeRemainingSeconds: Int,
    modifier: Modifier = Modifier
) {
    val minutes = timeRemainingSeconds / 60
    val seconds = timeRemainingSeconds % 60
    val isLowTime = timeRemainingSeconds < 300 // Less than 5 minutes

    val infiniteTransition = rememberInfiniteTransition(label = "timer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isLowTime && timeRemainingSeconds < 60) 0.5f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "timerAlpha"
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (isLowTime) StatusRejected.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (isLowTime) StatusRejected else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = String.format("%02d:%02d", minutes, seconds),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isLowTime) {
                    StatusRejected.copy(alpha = alpha)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Composable
private fun DifficultyChip(difficulty: QuestionDifficulty) {
    val color = Color(difficulty.color)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = difficulty.displayName,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CategoryChip(category: QuestionCategory) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = category.displayName,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun AnswerOption(
    index: Int,
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    isUserAnswer: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val optionLetter = ('A' + index).toString()

    val backgroundColor = when {
        isCorrect == true -> StatusPublished.copy(alpha = 0.2f)
        isCorrect == false && isUserAnswer -> StatusRejected.copy(alpha = 0.2f)
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        isCorrect == true -> StatusPublished
        isCorrect == false && isUserAnswer -> StatusRejected
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(
            width = if (isSelected || isCorrect != null) 2.dp else 1.dp,
            color = borderColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Option letter circle
            Surface(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected || isCorrect == true) {
                    borderColor
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = optionLetter,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected || isCorrect == true) {
                            Color.White
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            // Result icons
            when {
                isCorrect == true -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Correct",
                        tint = StatusPublished
                    )
                }
                isCorrect == false && isUserAnswer -> {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Incorrect",
                        tint = StatusRejected
                    )
                }
                isSelected && enabled -> {
                    Icon(
                        imageVector = Icons.Default.RadioButtonChecked,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
