package com.swiftquantum.quantumcareer.presentation.ui.util

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Responsive Layout Utilities for SwiftQuantum Ecosystem
 */

enum class DeviceSize {
    Compact,
    Medium,
    Expanded
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val activity = LocalContext.current as Activity
    return calculateWindowSizeClass(activity)
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberDeviceSize(): DeviceSize {
    val windowSizeClass = rememberWindowSizeClass()
    return when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> DeviceSize.Compact
        WindowWidthSizeClass.Medium -> DeviceSize.Medium
        WindowWidthSizeClass.Expanded -> DeviceSize.Expanded
        else -> DeviceSize.Compact
    }
}

@Composable
fun isTablet(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp >= 600
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun isFoldable(): Boolean {
    val windowSizeClass = rememberWindowSizeClass()
    return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium
}

@Composable
fun isLandscape(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

@Composable
fun AdaptiveLayout(
    compactContent: @Composable () -> Unit,
    mediumContent: @Composable (() -> Unit)? = null,
    expandedContent: @Composable (() -> Unit)? = null
) {
    val deviceSize = rememberDeviceSize()

    when (deviceSize) {
        DeviceSize.Compact -> compactContent()
        DeviceSize.Medium -> (mediumContent ?: compactContent)()
        DeviceSize.Expanded -> (expandedContent ?: mediumContent ?: compactContent)()
    }
}

@Composable
fun rememberAdaptiveColumnCount(
    compactColumns: Int = 1,
    mediumColumns: Int = 2,
    expandedColumns: Int = 3
): Int {
    val deviceSize = rememberDeviceSize()
    return when (deviceSize) {
        DeviceSize.Compact -> compactColumns
        DeviceSize.Medium -> mediumColumns
        DeviceSize.Expanded -> expandedColumns
    }
}

@Composable
fun rememberAdaptivePadding(): PaddingValues {
    val deviceSize = rememberDeviceSize()
    return when (deviceSize) {
        DeviceSize.Compact -> PaddingValues(16.dp)
        DeviceSize.Medium -> PaddingValues(24.dp)
        DeviceSize.Expanded -> PaddingValues(32.dp)
    }
}

@Composable
fun AdaptiveNavigationScaffold(
    navigationItems: List<NavigationItem>,
    selectedIndex: Int,
    onNavigate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val deviceSize = rememberDeviceSize()

    when (deviceSize) {
        DeviceSize.Compact -> {
            Scaffold(
                modifier = modifier,
                bottomBar = {
                    NavigationBar {
                        navigationItems.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = index == selectedIndex,
                                onClick = { onNavigate(index) },
                                icon = {
                                    Icon(
                                        imageVector = if (index == selectedIndex) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                },
                                label = { Text(item.label) }
                            )
                        }
                    }
                },
                floatingActionButton = floatingActionButton,
                content = content
            )
        }
        DeviceSize.Medium, DeviceSize.Expanded -> {
            Row(modifier = modifier.fillMaxSize()) {
                NavigationRail {
                    Spacer(modifier = Modifier.height(8.dp))
                    navigationItems.forEachIndexed { index, item ->
                        NavigationRailItem(
                            selected = index == selectedIndex,
                            onClick = { onNavigate(index) },
                            icon = {
                                Icon(
                                    imageVector = if (index == selectedIndex) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) }
                        )
                    }
                }

                Scaffold(
                    modifier = Modifier.weight(1f),
                    floatingActionButton = floatingActionButton,
                    content = content
                )
            }
        }
    }
}

data class NavigationItem(
    val label: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)

@Composable
fun TwoPaneLayout(
    isDetailVisible: Boolean,
    listPane: @Composable () -> Unit,
    detailPane: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val deviceSize = rememberDeviceSize()

    when (deviceSize) {
        DeviceSize.Compact -> {
            if (isDetailVisible) {
                detailPane()
            } else {
                listPane()
            }
        }
        DeviceSize.Medium -> {
            Row(modifier = modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(0.4f)) {
                    listPane()
                }
                Box(modifier = Modifier.weight(0.6f)) {
                    detailPane()
                }
            }
        }
        DeviceSize.Expanded -> {
            Row(modifier = modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(0.35f)) {
                    listPane()
                }
                Box(modifier = Modifier.weight(0.65f)) {
                    detailPane()
                }
            }
        }
    }
}

@Composable
fun ResponsiveGrid(
    items: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier
) {
    val columnCount = rememberAdaptiveColumnCount()

    Column(modifier = modifier) {
        items.chunked(columnCount).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowItems.forEach { item ->
                    Box(modifier = Modifier.weight(1f)) {
                        item()
                    }
                }
                repeat(columnCount - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
