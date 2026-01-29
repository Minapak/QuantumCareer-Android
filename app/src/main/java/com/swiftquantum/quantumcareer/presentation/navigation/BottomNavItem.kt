package com.swiftquantum.quantumcareer.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.swiftquantum.quantumcareer.R

sealed class BottomNavItem(
    val route: String,
    @StringRes val titleResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Dashboard : BottomNavItem(
        route = Screen.Dashboard.route,
        titleResId = R.string.nav_home,
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard
    )

    data object Quiz : BottomNavItem(
        route = Screen.Quiz.route,
        titleResId = R.string.nav_quiz,
        selectedIcon = Icons.Filled.Quiz,
        unselectedIcon = Icons.Outlined.Quiz
    )

    data object Rankings : BottomNavItem(
        route = Screen.Rankings.route,
        titleResId = R.string.nav_rankings,
        selectedIcon = Icons.Filled.Leaderboard,
        unselectedIcon = Icons.Outlined.Leaderboard
    )

    data object Jobs : BottomNavItem(
        route = Screen.Jobs.route,
        titleResId = R.string.nav_jobs,
        selectedIcon = Icons.Filled.Work,
        unselectedIcon = Icons.Outlined.Work
    )

    data object Certificates : BottomNavItem(
        route = Screen.Certificates.route,
        titleResId = R.string.nav_certs,
        selectedIcon = Icons.Filled.Verified,
        unselectedIcon = Icons.Outlined.Verified
    )

    data object Profile : BottomNavItem(
        route = Screen.Profile.route,
        titleResId = R.string.nav_profile,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )

    companion object {
        val items = listOf(Dashboard, Jobs, Quiz, Certificates, Profile)
    }
}
