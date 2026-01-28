package com.swiftquantum.quantumcareer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.swiftquantum.quantumcareer.presentation.ui.screen.*
import com.swiftquantum.quantumcareer.presentation.ui.screen.auth.AuthScreen
import com.swiftquantum.quantumcareer.presentation.ui.screen.jobs.JobDetailScreen
import com.swiftquantum.quantumcareer.presentation.ui.screen.jobs.JobsScreen
import com.swiftquantum.quantumcareer.presentation.ui.screen.quiz.*
import com.swiftquantum.quantumcareer.presentation.ui.screen.rankings.*
import com.swiftquantum.quantumcareer.presentation.ui.screen.certificate.*

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
    data object Auth : Screen("auth")
    data object Settings : Screen("settings")

    // Quiz screens
    data object Quiz : Screen("quiz")
    data object QuizTest : Screen("quiz/test")
    data object QuizResult : Screen("quiz/result/{sessionId}") {
        fun createRoute(sessionId: String) = "quiz/result/$sessionId"
    }

    // Rankings screens
    data object Rankings : Screen("rankings")
    data object MyRank : Screen("rankings/me")

    // Certificate screens
    data object Certificates : Screen("certificates")
    data object CertificateDetail : Screen("certificates/{certificateId}") {
        fun createRoute(certificateId: String) = "certificates/$certificateId"
    }
    data object VerifyCertificate : Screen("certificates/verify")

    // Jobs screens
    data object Jobs : Screen("jobs")
    data object JobDetail : Screen("jobs/{jobId}") {
        fun createRoute(jobId: String) = "jobs/$jobId"
    }
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
                onNavigateToCitations = { navController.navigate(Screen.Citations.route) },
                onNavigateToJobs = { navController.navigate(Screen.Jobs.route) },
                onNavigateToJobDetail = { jobId ->
                    navController.navigate(Screen.JobDetail.createRoute(jobId))
                }
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
            ProfileScreen(
                onNavigateToAuth = { navController.navigate(Screen.Auth.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAuth = { navController.navigate(Screen.Auth.route) },
                onNavigateToEditProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.Auth.route) {
            AuthScreen(
                onNavigateToMain = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
                onContinueAsGuest = { navController.popBackStack() }
            )
        }

        // Quiz screens
        composable(Screen.Quiz.route) {
            TestStartScreen(
                onStartTest = { navController.navigate(Screen.QuizTest.route) },
                onResumeTest = { navController.navigate(Screen.QuizTest.route) },
                onViewResult = { sessionId ->
                    navController.navigate(Screen.QuizResult.createRoute(sessionId))
                }
            )
        }

        composable(Screen.QuizTest.route) {
            TestQuestionScreen(
                onTestFinished = {
                    navController.popBackStack(Screen.Quiz.route, false)
                },
                onAbandon = {
                    navController.popBackStack(Screen.Quiz.route, false)
                }
            )
        }

        composable(Screen.QuizResult.route) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable
            TestResultScreen(
                onNavigateBack = { navController.popBackStack() },
                onViewCertificate = { certificateId ->
                    navController.navigate(Screen.CertificateDetail.createRoute(certificateId))
                },
                onRetakeTest = { navController.navigate(Screen.Quiz.route) }
            )
        }

        // Rankings screens
        composable(Screen.Rankings.route) {
            LeaderboardScreen(
                onNavigateToMyRank = { navController.navigate(Screen.MyRank.route) }
            )
        }

        composable(Screen.MyRank.route) {
            MyRankScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Certificate screens
        composable(Screen.Certificates.route) {
            CertificatesScreen(
                onCertificateClick = { certificateId ->
                    navController.navigate(Screen.CertificateDetail.createRoute(certificateId))
                },
                onVerifyClick = { navController.navigate(Screen.VerifyCertificate.route) }
            )
        }

        composable(Screen.CertificateDetail.route) { backStackEntry ->
            val certificateId = backStackEntry.arguments?.getString("certificateId") ?: return@composable
            CertificateDetailScreen(
                certificateId = certificateId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.VerifyCertificate.route) {
            // Verification screen - simplified for now
            CertificatesScreen(
                onCertificateClick = { certificateId ->
                    navController.navigate(Screen.CertificateDetail.createRoute(certificateId))
                },
                onVerifyClick = { }
            )
        }

        // Jobs screens
        composable(Screen.Jobs.route) {
            JobsScreen(
                onNavigateToJobDetail = { jobId ->
                    navController.navigate(Screen.JobDetail.createRoute(jobId))
                }
            )
        }

        composable(
            route = Screen.JobDetail.route,
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) { backStackEntry ->
            JobDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
