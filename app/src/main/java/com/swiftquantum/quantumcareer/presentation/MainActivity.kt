package com.swiftquantum.quantumcareer.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.swiftquantum.quantumcareer.R
import com.swiftquantum.quantumcareer.presentation.navigation.BottomNavItem
import com.swiftquantum.quantumcareer.presentation.navigation.NavGraph
import com.swiftquantum.quantumcareer.presentation.navigation.Screen
import com.swiftquantum.quantumcareer.presentation.ui.component.DrawerMenuItem
import com.swiftquantum.quantumcareer.presentation.ui.component.UnifiedNavigationDrawer
import com.swiftquantum.quantumcareer.presentation.ui.theme.QuantumCareerTheme
import com.swiftquantum.quantumcareer.presentation.viewmodel.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var deepLinkUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle incoming deep link intent
        handleIntent(intent)

        setContent {
            QuantumCareerTheme {
                QuantumCareerApp(deepLinkUri = deepLinkUri)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_VIEW -> {
                intent.data?.let { uri ->
                    if (uri.scheme == "quantumcareer") {
                        deepLinkUri = uri
                    }
                }
            }
        }
    }
}

@Composable
fun QuantumCareerApp(
    deepLinkUri: Uri? = null,
    onboardingViewModel: OnboardingViewModel = hiltViewModel()
) {
    val onboardingState by onboardingViewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Determine start destination based on onboarding status
    val startDestination = if (onboardingState.isLoading) {
        Screen.LanguageSelection.route  // Will be updated after loading
    } else if (onboardingState.onboardingCompleted) {
        Screen.Dashboard.route
    } else {
        Screen.LanguageSelection.route
    }

    // Determine if bottom bar should be shown
    val showBottomBar = currentDestination?.route in listOf(
        Screen.Dashboard.route,
        Screen.Circuits.route,
        Screen.PeerReview.route,
        Screen.Talent.route,
        Screen.Profile.route,
        Screen.Badges.route,
        Screen.Citations.route,
        Screen.Quiz.route,
        Screen.Rankings.route,
        Screen.Certificates.route
    )

    // QuantumCareer app-specific drawer menu items
    val drawerMenuItems = listOf(
        DrawerMenuItem(
            title = stringResource(R.string.drawer_dashboard),
            icon = Icons.Filled.Dashboard,
            route = Screen.Dashboard.route
        ),
        DrawerMenuItem(
            title = stringResource(R.string.drawer_my_circuits),
            icon = Icons.Filled.Memory,
            route = Screen.Circuits.route
        ),
        DrawerMenuItem(
            title = stringResource(R.string.drawer_peer_review),
            icon = Icons.Filled.RateReview,
            route = Screen.PeerReview.route
        ),
        DrawerMenuItem(
            title = stringResource(R.string.drawer_certificates),
            icon = Icons.Filled.Verified,
            route = Screen.Certificates.route
        ),
        DrawerMenuItem(
            title = stringResource(R.string.drawer_talent_pool),
            icon = Icons.Filled.Groups,
            route = Screen.Talent.route
        )
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            UnifiedNavigationDrawer(
                currentAppName = "QuantumCareer",
                userDisplayName = "Quantum Professional",
                userEmail = "pro@quantum.dev",
                currentAppFeatures = drawerMenuItems,
                onNavigate = { route ->
                    scope.launch { drawerState.close() }
                    navController.navigate(route)
                },
                onSettingsClick = {
                    scope.launch { drawerState.close() }
                    // Navigate to settings
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        BottomNavItem.items.forEach { item ->
                            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                            val title = stringResource(item.titleResId)

                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = title
                                    )
                                },
                                label = { Text(title) }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            if (!onboardingState.isLoading) {
                NavGraph(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding),
                    startDestination = startDestination
                )
            }
        }
    }
}
