package com.swiftquantum.quantumcareer.presentation.ui.component

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.swiftquantum.quantumcareer.presentation.ui.theme.*

/**
 * Unified Navigation Drawer for SwiftQuantum Ecosystem
 * This drawer is shared across all 4 apps in the ecosystem:
 * - SwiftQuantum (Swift-Purple #7B2FFF)
 * - QuantumNative (Native-Blue #0066FF)
 * - Q-Bridge (Bridge-Cyan #00D4FF)
 * - QuantumCareer (Career-Gold #FFB800)
 */

data class EcosystemApp(
    val name: String,
    val description: String,
    val deepLink: String,
    val color: Color,
    val icon: ImageVector,
    val isCurrentApp: Boolean = false
)

data class DrawerMenuItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val badge: String? = null
)

@Composable
fun UnifiedNavigationDrawer(
    currentAppName: String = "QuantumCareer",
    userDisplayName: String = "Quantum User",
    userEmail: String = "user@quantum.dev",
    userAvatarUrl: String? = null,
    currentAppFeatures: List<DrawerMenuItem>,
    onNavigate: (String) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val ecosystemApps = listOf(
        EcosystemApp(
            name = "QuantumNative",
            description = "Native quantum learning",
            deepLink = "quantumnative://home",
            color = NativeBlue,
            icon = Icons.Filled.School,
            isCurrentApp = currentAppName == "QuantumNative"
        ),
        EcosystemApp(
            name = "SwiftQuantum",
            description = "Quantum circuit designer",
            deepLink = "swiftquantum://home",
            color = SwiftPurple,
            icon = Icons.Filled.Memory,
            isCurrentApp = currentAppName == "SwiftQuantum"
        ),
        EcosystemApp(
            name = "Q-Bridge",
            description = "Real quantum hardware",
            deepLink = "qbridge://home",
            color = BridgeCyan,
            icon = Icons.Filled.Hub,
            isCurrentApp = currentAppName == "Q-Bridge"
        ),
        EcosystemApp(
            name = "QuantumCareer",
            description = "Career & certifications",
            deepLink = "quantumcareer://home",
            color = CareerGold,
            icon = Icons.Filled.WorkspacePremium,
            isCurrentApp = currentAppName == "QuantumCareer"
        )
    )

    ModalDrawerSheet(
        modifier = modifier,
        drawerContainerColor = SurfaceDark,
        drawerContentColor = TextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(scrollState)
        ) {
            // Header with gradient and user info
            DrawerHeader(
                appName = currentAppName,
                userDisplayName = userDisplayName,
                userEmail = userEmail,
                appColor = when (currentAppName) {
                    "QuantumNative" -> NativeBlue
                    "SwiftQuantum" -> SwiftPurple
                    "Q-Bridge" -> BridgeCyan
                    "QuantumCareer" -> CareerGold
                    else -> CareerGold
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Current App Features Section
            DrawerSection(title = "Features") {
                currentAppFeatures.forEach { item ->
                    DrawerItem(
                        title = item.title,
                        icon = item.icon,
                        badge = item.badge,
                        onClick = { onNavigate(item.route) }
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = SurfaceDarkVariant
            )

            // Ecosystem Apps Section
            DrawerSection(title = "SwiftQuantum Ecosystem") {
                ecosystemApps.forEach { app ->
                    EcosystemAppItem(
                        app = app,
                        onClick = {
                            if (!app.isCurrentApp) {
                                openDeepLink(context, app.deepLink)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Settings at bottom
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = SurfaceDarkVariant
            )

            DrawerItem(
                title = "Settings",
                icon = Icons.Outlined.Settings,
                onClick = onSettingsClick
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DrawerHeader(
    appName: String,
    userDisplayName: String,
    userEmail: String,
    appColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        appColor.copy(alpha = 0.3f),
                        SurfaceDark
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            // App Logo/Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(appColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = appName.take(2).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = userDisplayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Text(
                text = userEmail,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun DrawerSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
    }
}

@Composable
private fun DrawerItem(
    title: String,
    icon: ImageVector,
    badge: String? = null,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title)
                badge?.let {
                    Badge(
                        containerColor = CareerGold,
                        contentColor = Color.Black
                    ) {
                        Text(text = it)
                    }
                }
            }
        },
        icon = { Icon(imageVector = icon, contentDescription = title) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp),
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            unselectedTextColor = TextPrimary,
            unselectedIconColor = TextSecondary
        )
    )
}

@Composable
private fun EcosystemAppItem(
    app: EcosystemApp,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = {
            Column {
                Text(
                    text = app.name,
                    fontWeight = if (app.isCurrentApp) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = app.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        },
        icon = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(app.color.copy(alpha = if (app.isCurrentApp) 0.3f else 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = app.icon,
                    contentDescription = app.name,
                    tint = app.color
                )
            }
        },
        badge = if (app.isCurrentApp) {
            {
                Text(
                    text = "Current",
                    style = MaterialTheme.typography.labelSmall,
                    color = app.color
                )
            }
        } else null,
        selected = app.isCurrentApp,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = app.color.copy(alpha = 0.1f),
            unselectedContainerColor = Color.Transparent,
            selectedTextColor = TextPrimary,
            unselectedTextColor = TextPrimary,
            selectedIconColor = app.color,
            unselectedIconColor = app.color
        )
    )
}

private fun openDeepLink(context: Context, deepLink: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    } catch (e: Exception) {
        // App not installed, could open Play Store link here
        e.printStackTrace()
    }
}
