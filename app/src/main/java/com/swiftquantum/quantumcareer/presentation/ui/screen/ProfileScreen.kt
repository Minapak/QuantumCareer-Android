package com.swiftquantum.quantumcareer.presentation.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.swiftquantum.quantumcareer.presentation.ui.component.*
import com.swiftquantum.quantumcareer.presentation.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val editForm by viewModel.editForm.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar("Profile updated successfully!")
            viewModel.clearSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "Edit Profile" else "Profile") },
                actions = {
                    if (!uiState.isEditing) {
                        IconButton(onClick = { viewModel.startEditing() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { viewModel.refresh() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            uiState.isLoading && uiState.profile == null -> {
                LoadingView(modifier = Modifier.padding(paddingValues))
            }
            uiState.error != null && uiState.profile == null -> {
                ErrorView(
                    message = uiState.error ?: "Unknown error",
                    onRetry = { viewModel.refresh() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.profile != null -> {
                val profile = uiState.profile!!

                if (uiState.isEditing) {
                    // Edit Mode
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = editForm.displayName,
                            onValueChange = { viewModel.updateDisplayName(it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Display Name") },
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = editForm.bio,
                            onValueChange = { viewModel.updateBio(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp),
                            label = { Text("Bio") },
                            placeholder = { Text("Tell us about yourself...") },
                            maxLines = 5
                        )

                        OutlinedTextField(
                            value = editForm.institution,
                            onValueChange = { viewModel.updateInstitution(it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Institution") },
                            placeholder = { Text("University, company, or research lab") },
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = editForm.location,
                            onValueChange = { viewModel.updateLocation(it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Location") },
                            placeholder = { Text("City, Country") },
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = editForm.website,
                            onValueChange = { viewModel.updateWebsite(it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Website") },
                            placeholder = { Text("https://...") },
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = editForm.specializations,
                            onValueChange = { viewModel.updateSpecializations(it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Specializations") },
                            placeholder = { Text("Quantum algorithms, error correction...") },
                            supportingText = { Text("Separate with commas") },
                            singleLine = true
                        )

                        // Visibility Settings
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Privacy Settings",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Public Profile",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "Make your profile visible to everyone",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Switch(
                                        checked = editForm.isPublic,
                                        onCheckedChange = { viewModel.updateIsPublic(it) }
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Available for Hire",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "Allow recruiters to send you offers",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Switch(
                                        checked = editForm.isAvailableForHire,
                                        onCheckedChange = { viewModel.updateIsAvailableForHire(it) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.cancelEditing() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }
                            Button(
                                onClick = { viewModel.saveProfile() },
                                modifier = Modifier.weight(1f),
                                enabled = !uiState.isLoading
                            ) {
                                if (uiState.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Text("Save")
                                }
                            }
                        }
                    }
                } else {
                    // View Mode
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Profile Header
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (profile.avatarUrl != null) {
                                    AsyncImage(
                                        model = profile.avatarUrl,
                                        contentDescription = null,
                                        modifier = Modifier.size(80.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(80.dp),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = profile.displayName,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "@${profile.username}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                CurrentBadgeDisplay(tier = profile.badgeTier)

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = profile.totalPublications.toString(),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Publications",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = profile.totalCitations.toString(),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Citations",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = profile.hIndex.toString(),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "h-Index",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }

                        // Public Profile URL
                        if (profile.isPublic) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Public Profile",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = profile.publicProfileUrl,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(profile.publicProfileUrl))
                                            }
                                        ) {
                                            Icon(
                                                Icons.Default.ContentCopy,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Copy")
                                        }

                                        Button(
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(profile.publicProfileUrl))
                                                context.startActivity(intent)
                                            }
                                        ) {
                                            Icon(
                                                Icons.Default.OpenInNew,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Open")
                                        }

                                        IconButton(
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(Intent.EXTRA_TEXT, "Check out my quantum computing portfolio: ${profile.publicProfileUrl}")
                                                }
                                                context.startActivity(Intent.createChooser(intent, "Share Profile"))
                                            }
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = "Share")
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Bio
                        profile.bio?.let { bio ->
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                SectionHeader(title = "About")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = bio,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Details
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            SectionHeader(title = "Details")
                            Spacer(modifier = Modifier.height(8.dp))

                            profile.institution?.let {
                                ProfileDetailRow(
                                    icon = Icons.Default.Business,
                                    label = "Institution",
                                    value = it
                                )
                            }

                            profile.location?.let {
                                ProfileDetailRow(
                                    icon = Icons.Default.LocationOn,
                                    label = "Location",
                                    value = it
                                )
                            }

                            profile.website?.let {
                                ProfileDetailRow(
                                    icon = Icons.Default.Language,
                                    label = "Website",
                                    value = it
                                )
                            }
                        }

                        // Specializations
                        if (profile.specializations.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                SectionHeader(title = "Specializations")
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    profile.specializations.forEach { spec ->
                                        SuggestionChip(
                                            onClick = {},
                                            label = { Text(spec) }
                                        )
                                    }
                                }
                            }
                        }

                        // Badges
                        if (profile.badges.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                BadgeCollectionView(
                                    badges = profile.badges,
                                    currentTier = profile.badgeTier,
                                    onBadgeClick = {}
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
