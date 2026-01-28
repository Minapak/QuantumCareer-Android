package com.swiftquantum.quantumcareer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.swiftquantum.quantumcareer.presentation.ui.screen.*

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object Publish : Screen("publish")
    data object Circuits : Screen("circuits")
    data object CircuitDetail : Screen("circuits/{circuitId}") {
        fun createRoute(circuitId: String) = "circuits/$circuitId"
    }
    data object PeerReview : Screen("peer-review")
    data object Badges : Screen("badges")
    data object Citations : Screen("citations")
    data object Talent : Screen("talent")
    data object Profile : Screen("profile")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToCircuits = { navController.navigate(Screen.Circuits.route) },
                onNavigateToReviews = { navController.navigate(Screen.PeerReview.route) },
                onNavigateToBadges = { navController.navigate(Screen.Badges.route) },
                onNavigateToCitations = { navController.navigate(Screen.Citations.route) }
            )
        }

        composable(Screen.Publish.route) {
            PublishScreen(
                onNavigateBack = { navController.popBackStack() },
                onPublishSuccess = {
                    navController.popBackStack()
                    navController.navigate(Screen.Circuits.route) {
                        popUpTo(Screen.Dashboard.route)
                    }
                }
            )
        }

        composable(Screen.Circuits.route) {
            CircuitsScreen(
                onNavigateToPublish = { navController.navigate(Screen.Publish.route) },
                onCircuitClick = { circuitId ->
                    navController.navigate(Screen.CircuitDetail.createRoute(circuitId))
                }
            )
        }

        composable(Screen.CircuitDetail.route) { backStackEntry ->
            val circuitId = backStackEntry.arguments?.getString("circuitId") ?: return@composable
            // Circuit detail screen would go here
            // For now, we'll show the circuits screen
            CircuitsScreen(
                onNavigateToPublish = { navController.navigate(Screen.Publish.route) },
                onCircuitClick = {}
            )
        }

        composable(Screen.PeerReview.route) {
            PeerReviewScreen()
        }

        composable(Screen.Badges.route) {
            BadgesScreen()
        }

        composable(Screen.Citations.route) {
            CitationsScreen(
                onCircuitClick = { circuitId ->
                    navController.navigate(Screen.CircuitDetail.createRoute(circuitId))
                }
            )
        }

        composable(Screen.Talent.route) {
            TalentScreen()
        }

        composable(Screen.Profile.route) {
            ProfileScreen()
        }
    }
}
