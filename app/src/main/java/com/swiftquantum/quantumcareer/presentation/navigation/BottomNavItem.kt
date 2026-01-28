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
        title = "Home",
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard
    )

    data object Quiz : BottomNavItem(
        route = Screen.Quiz.route,
        title = "Quiz",
        selectedIcon = Icons.Filled.Quiz,
        unselectedIcon = Icons.Outlined.Quiz
    )

    data object Rankings : BottomNavItem(
        route = Screen.Rankings.route,
        title = "Rankings",
        selectedIcon = Icons.Filled.Leaderboard,
        unselectedIcon = Icons.Outlined.Leaderboard
    )

    data object Jobs : BottomNavItem(
        route = Screen.Jobs.route,
        title = "Jobs",
        selectedIcon = Icons.Filled.Work,
        unselectedIcon = Icons.Outlined.Work
    )

    data object Certificates : BottomNavItem(
        route = Screen.Certificates.route,
        title = "Certs",
        selectedIcon = Icons.Filled.Verified,
        unselectedIcon = Icons.Outlined.Verified
    )

    data object Profile : BottomNavItem(
        route = Screen.Profile.route,
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )

    companion object {
        val items = listOf(Dashboard, Jobs, Quiz, Certificates, Profile)
    }
}
