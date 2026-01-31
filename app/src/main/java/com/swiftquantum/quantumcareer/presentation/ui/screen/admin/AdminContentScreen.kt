package com.swiftquantum.quantumcareer.presentation.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.swiftquantum.quantumcareer.R
import com.swiftquantum.quantumcareer.data.dto.*
import com.swiftquantum.quantumcareer.presentation.ui.theme.QuantumColors

enum class ContentSection {
    QUESTIONS, JOBS, BADGES
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminContentScreen(
    navController: NavController,
    viewModel: AdminContentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedSection by remember { mutableStateOf(ContentSection.QUESTIONS) }
    var showAddQuestionSheet by remember { mutableStateOf(false) }
    var showAddJobSheet by remember { mutableStateOf(false) }

    LaunchedEffect(selectedSection) {
        when (selectedSection) {
            ContentSection.QUESTIONS -> viewModel.loadQuestions()
            ContentSection.JOBS -> viewModel.loadJobs()
            ContentSection.BADGES -> viewModel.loadBadges()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.admin_content_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    when (selectedSection) {
                        ContentSection.QUESTIONS -> {
                            IconButton(onClick = { showAddQuestionSheet = true }) {
                                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
                            }
                        }
                        ContentSection.JOBS -> {
                            IconButton(onClick = { showAddJobSheet = true }) {
                                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
                            }
                        }
                        ContentSection.BADGES -> { }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = QuantumColors.Surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(QuantumColors.Background)
        ) {
            // Section Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ContentSection.entries.forEach { section ->
                    FilterChip(
                        selected = selectedSection == section,
                        onClick = { selectedSection = section },
                        label = {
                            Text(
                                when (section) {
                                    ContentSection.QUESTIONS -> stringResource(R.string.admin_content_questions)
                                    ContentSection.JOBS -> stringResource(R.string.admin_content_jobs)
                                    ContentSection.BADGES -> stringResource(R.string.admin_content_badges)
                                }
                            )
                        }
                    )
                }
            }

            // Content
            when (selectedSection) {
                ContentSection.QUESTIONS -> QuestionsContent(
                    questions = uiState.questions,
                    isLoading = uiState.isLoading,
                    onDelete = { viewModel.deleteQuestion(it) }
                )
                ContentSection.JOBS -> JobsContent(
                    jobs = uiState.jobs,
                    isLoading = uiState.isLoading,
                    onDelete = { viewModel.deleteJob(it) },
                    onToggleActive = { viewModel.toggleJobActive(it) }
                )
                ContentSection.BADGES -> BadgesContent(
                    badges = uiState.badges,
                    isLoading = uiState.isLoading
                )
            }
        }
    }

    // Add Question Sheet
    if (showAddQuestionSheet) {
        AddQuestionSheet(
            onDismiss = { showAddQuestionSheet = false },
            onSave = { request ->
                viewModel.createQuestion(request)
                showAddQuestionSheet = false
            }
        )
    }

    // Add Job Sheet
    if (showAddJobSheet) {
        AddJobSheet(
            onDismiss = { showAddJobSheet = false },
            onSave = { request ->
                viewModel.createJob(request)
                showAddJobSheet = false
            }
        )
    }
}

@Composable
private fun QuestionsContent(
    questions: List<AdminQuestionItemDto>,
    isLoading: Boolean,
    onDelete: (Int) -> Unit
) {
    if (isLoading && questions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = QuantumColors.Primary)
        }
    } else if (questions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.QuestionMark,
                    contentDescription = null,
                    tint = QuantumColors.TextTertiary,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = stringResource(R.string.admin_questions_empty),
                    color = QuantumColors.TextSecondary
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(questions) { question ->
                QuestionCard(
                    question = question,
                    onDelete = { onDelete(question.id) }
                )
            }
        }
    }
}

@Composable
private fun QuestionCard(
    question: AdminQuestionItemDto,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = QuantumColors.Surface),
        shape = RoundedCornerShape(12.dp)
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
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = QuantumColors.BackgroundSecondary,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = question.category.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelSmall,
                            color = QuantumColors.TextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    val difficultyColor = when (question.difficulty.lowercase()) {
                        "easy" -> QuantumColors.Success
                        "medium" -> QuantumColors.Warning
                        "hard" -> QuantumColors.Error
                        else -> QuantumColors.TextSecondary
                    }
                    Text(
                        text = question.difficulty.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall,
                        color = difficultyColor
                    )
                }

                Text(
                    text = "${question.points} pts",
                    style = MaterialTheme.typography.labelSmall,
                    color = QuantumColors.Accent
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = question.text,
                style = MaterialTheme.typography.bodyMedium,
                color = QuantumColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${stringResource(R.string.admin_answered)}: ${question.timesAnswered} (${String.format("%.1f", question.correctRate * 100)}%)",
                    style = MaterialTheme.typography.labelSmall,
                    color = QuantumColors.TextTertiary
                )

                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = QuantumColors.Error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.admin_delete_question)) },
            text = { Text(stringResource(R.string.admin_delete_question_confirm)) },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = QuantumColors.Error)
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun JobsContent(
    jobs: List<AdminJobItemDto>,
    isLoading: Boolean,
    onDelete: (Int) -> Unit,
    onToggleActive: (Int) -> Unit
) {
    if (isLoading && jobs.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = QuantumColors.Primary)
        }
    } else if (jobs.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Work,
                    contentDescription = null,
                    tint = QuantumColors.TextTertiary,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = stringResource(R.string.admin_jobs_empty),
                    color = QuantumColors.TextSecondary
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(jobs) { job ->
                JobCard(
                    job = job,
                    onDelete = { onDelete(job.id) },
                    onToggleActive = { onToggleActive(job.id) }
                )
            }
        }
    }
}

@Composable
private fun JobCard(
    job: AdminJobItemDto,
    onDelete: () -> Unit,
    onToggleActive: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = QuantumColors.Surface),
        shape = RoundedCornerShape(12.dp)
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
                    text = job.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = QuantumColors.TextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (job.isActive) QuantumColors.Success
                            else QuantumColors.TextTertiary
                        )
                )
            }

            Text(
                text = job.companyName,
                style = MaterialTheme.typography.bodySmall,
                color = QuantumColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${job.location} • ${job.applicationCount} ${stringResource(R.string.admin_applications)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = QuantumColors.TextTertiary
                )

                Row {
                    IconButton(
                        onClick = onToggleActive,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (job.isActive) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = QuantumColors.TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete),
                            tint = QuantumColors.Error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgesContent(
    badges: List<AdminBadgeItemDto>,
    isLoading: Boolean
) {
    if (isLoading && badges.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = QuantumColors.Primary)
        }
    } else if (badges.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = QuantumColors.TextTertiary,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = stringResource(R.string.admin_badges_empty),
                    color = QuantumColors.TextSecondary
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(badges) { badge ->
                BadgeCard(badge = badge)
            }
        }
    }
}

@Composable
private fun BadgeCard(badge: AdminBadgeItemDto) {
    val tierColor = when (badge.tier.lowercase()) {
        "bronze" -> Color(0xFFCD7F32)
        "silver" -> Color(0xFFC0C0C0)
        "gold" -> QuantumColors.Gold
        "platinum" -> Color(0xFFE5E4E2)
        else -> QuantumColors.Primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = QuantumColors.Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(tierColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = tierColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = badge.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = QuantumColors.TextPrimary
                    )
                    Text(
                        text = badge.tier.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall,
                        color = tierColor
                    )
                }

                Text(
                    text = badge.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = QuantumColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${badge.earnedCount}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = QuantumColors.TextPrimary
                )
                Text(
                    text = stringResource(R.string.admin_earned),
                    style = MaterialTheme.typography.labelSmall,
                    color = QuantumColors.TextTertiary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddQuestionSheet(
    onDismiss: () -> Unit,
    onSave: (CreateQuestionRequest) -> Unit
) {
    var questionText by remember { mutableStateOf("") }
    var questionTextKo by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("fundamentals") }
    var difficulty by remember { mutableStateOf("medium") }
    var options by remember { mutableStateOf(listOf("", "", "", "")) }
    var correctOptionIndex by remember { mutableIntStateOf(0) }
    var explanation by remember { mutableStateOf("") }
    var points by remember { mutableIntStateOf(10) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = QuantumColors.Surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.admin_add_question),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = questionText,
                onValueChange = { questionText = it },
                label = { Text(stringResource(R.string.admin_question_text_en)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            OutlinedTextField(
                value = questionTextKo,
                onValueChange = { questionTextKo = it },
                label = { Text(stringResource(R.string.admin_question_text_ko)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = {},
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = category.replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.category)) },
                        modifier = Modifier.menuAnchor()
                    )
                }

                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = {},
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = difficulty.replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.difficulty)) },
                        modifier = Modifier.menuAnchor()
                    )
                }
            }

            Text(
                text = stringResource(R.string.admin_options),
                style = MaterialTheme.typography.titleSmall
            )

            options.forEachIndexed { index, option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(
                        selected = correctOptionIndex == index,
                        onClick = { correctOptionIndex = index }
                    )
                    OutlinedTextField(
                        value = option,
                        onValueChange = { newValue ->
                            options = options.toMutableList().apply { set(index, newValue) }
                        },
                        label = { Text("${stringResource(R.string.option)} ${index + 1}") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }

            OutlinedTextField(
                value = explanation,
                onValueChange = { explanation = it },
                label = { Text(stringResource(R.string.admin_explanation)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.cancel))
                }

                Button(
                    onClick = {
                        onSave(
                            CreateQuestionRequest(
                                text = questionText,
                                textKo = questionTextKo.takeIf { it.isNotBlank() },
                                category = category,
                                difficulty = difficulty,
                                options = options.map { CreateOptionRequest(text = it) },
                                correctOptionIndex = correctOptionIndex,
                                explanation = explanation.takeIf { it.isNotBlank() },
                                points = points
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = questionText.isNotBlank() && options.all { it.isNotBlank() }
                ) {
                    Text(stringResource(R.string.save))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddJobSheet(
    onDismiss: () -> Unit,
    onSave: (CreateJobRequest) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var requirements by remember { mutableStateOf("") }
    var salaryMin by remember { mutableStateOf("") }
    var salaryMax by remember { mutableStateOf("") }
    var jobType by remember { mutableStateOf("full_time") }
    var isRemote by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = QuantumColors.Surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.admin_add_job),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.admin_job_title)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = companyName,
                onValueChange = { companyName = it },
                label = { Text(stringResource(R.string.admin_company_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text(stringResource(R.string.location)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = isRemote,
                    onCheckedChange = { isRemote = it }
                )
                Text(stringResource(R.string.admin_remote_available))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = salaryMin,
                    onValueChange = { salaryMin = it },
                    label = { Text(stringResource(R.string.admin_salary_min)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = salaryMax,
                    onValueChange = { salaryMax = it },
                    label = { Text(stringResource(R.string.admin_salary_max)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = requirements,
                onValueChange = { requirements = it },
                label = { Text(stringResource(R.string.admin_requirements)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                placeholder = { Text(stringResource(R.string.admin_requirements_hint)) }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.cancel))
                }

                Button(
                    onClick = {
                        onSave(
                            CreateJobRequest(
                                title = title,
                                companyName = companyName,
                                location = location,
                                description = description,
                                requirements = requirements.lines().filter { it.isNotBlank() },
                                salaryMin = salaryMin.toIntOrNull(),
                                salaryMax = salaryMax.toIntOrNull(),
                                jobType = jobType,
                                isRemote = isRemote
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = title.isNotBlank() && companyName.isNotBlank()
                ) {
                    Text(stringResource(R.string.admin_post_job))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
