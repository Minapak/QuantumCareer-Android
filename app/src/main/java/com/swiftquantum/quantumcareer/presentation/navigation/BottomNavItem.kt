package com.swiftquantum.quantumcareer.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Dashboard : BottomNavItem(
        route = Screen.Dashboard.route,
        title = "Dashboard",
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard
    )

    data object Circuits : BottomNavItem(
        route = Screen.Circuits.route,
        title = "Circuits",
        selectedIcon = Icons.Filled.Article,
        unselectedIcon = Icons.Outlined.Article
    )

    data object Reviews : BottomNavItem(
        route = Screen.PeerReview.route,
        title = "Reviews",
        selectedIcon = Icons.Filled.RateReview,
        unselectedIcon = Icons.Outlined.RateReview
    )

    data object Talent : BottomNavItem(
        route = Screen.Talent.route,
        title = "Talent",
        selectedIcon = Icons.Filled.People,
        unselectedIcon = Icons.Outlined.People
    )

    data object Profile : BottomNavItem(
        route = Screen.Profile.route,
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )

    companion object {
        val items = listOf(Dashboard, Circuits, Reviews, Talent, Profile)
    }
}
