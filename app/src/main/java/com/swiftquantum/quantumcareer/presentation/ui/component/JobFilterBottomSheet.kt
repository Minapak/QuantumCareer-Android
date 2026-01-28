package com.swiftquantum.quantumcareer.presentation.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.swiftquantum.quantumcareer.R
import com.swiftquantum.quantumcareer.domain.model.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun JobFilterBottomSheet(
    currentFilter: JobFilter,
    onDismiss: () -> Unit,
    onApplyFilter: (JobFilter) -> Unit,
    onClearFilters: () -> Unit
) {
    var selectedLocationTypes by remember { mutableStateOf(currentFilter.locationTypes) }
    var selectedEmploymentTypes by remember { mutableStateOf(currentFilter.employmentTypes) }
    var selectedExperienceLevels by remember { mutableStateOf(currentFilter.experienceLevels) }
    var selectedSkills by remember { mutableStateOf(currentFilter.skills) }
    var skillInput by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.filter_jobs),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Location Type Section
            FilterSection(
                title = stringResource(R.string.location_type)
            ) {
                LocationType.entries.forEach { type ->
                    FilterChip(
                        selected = type in selectedLocationTypes,
                        onClick = {
                            selectedLocationTypes = if (type in selectedLocationTypes) {
                                selectedLocationTypes - type
                            } else {
                                selectedLocationTypes + type
                            }
                        },
                        label = { Text(type.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Employment Type Section
            FilterSection(
                title = stringResource(R.string.employment_type)
            ) {
                EmploymentType.entries.forEach { type ->
                    FilterChip(
                        selected = type in selectedEmploymentTypes,
                        onClick = {
                            selectedEmploymentTypes = if (type in selectedEmploymentTypes) {
                                selectedEmploymentTypes - type
                            } else {
                                selectedEmploymentTypes + type
                            }
                        },
                        label = { Text(type.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Experience Level Section
            FilterSection(
                title = stringResource(R.string.experience_level)
            ) {
                ExperienceLevel.entries.forEach { level ->
                    FilterChip(
                        selected = level in selectedExperienceLevels,
                        onClick = {
                            selectedExperienceLevels = if (level in selectedExperienceLevels) {
                                selectedExperienceLevels - level
                            } else {
                                selectedExperienceLevels + level
                            }
                        },
                        label = { Text(level.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Skills Filter Section
            Text(
                text = stringResource(R.string.skills),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = skillInput,
                onValueChange = { skillInput = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.add_skill_placeholder)) },
                singleLine = true,
                trailingIcon = {
                    if (skillInput.isNotBlank()) {
                        TextButton(
                            onClick = {
                                if (skillInput.isNotBlank() && skillInput !in selectedSkills) {
                                    selectedSkills = selectedSkills + skillInput.trim()
                                    skillInput = ""
                                }
                            }
                        ) {
                            Text(stringResource(R.string.add))
                        }
                    }
                }
            )

            if (selectedSkills.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedSkills.forEach { skill ->
                        InputChip(
                            selected = true,
                            onClick = {
                                selectedSkills = selectedSkills - skill
                            },
                            label = { Text(skill) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = stringResource(R.string.remove),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        selectedLocationTypes = emptyList()
                        selectedEmploymentTypes = emptyList()
                        selectedExperienceLevels = emptyList()
                        selectedSkills = emptyList()
                        onClearFilters()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.clear_filters))
                }

                Button(
                    onClick = {
                        val newFilter = currentFilter.copy(
                            locationTypes = selectedLocationTypes,
                            employmentTypes = selectedEmploymentTypes,
                            experienceLevels = selectedExperienceLevels,
                            skills = selectedSkills
                        )
                        onApplyFilter(newFilter)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.apply_filters))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterSection(
    title: String,
    content: @Composable FlowRowScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable FlowRowScope.() -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        content = content
    )
}
