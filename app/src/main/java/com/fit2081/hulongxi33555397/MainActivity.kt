package com.fit2081.hulongxi33555397

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fit2081.hulongxi33555397.ui.theme.NutriTrackTheme
import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.fit2081.hulongxi33555397.db.NutritrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create the repository and initialize the database
        val repository = NutritrackRepository(this)

        // Importing data into a coroutine
        lifecycleScope.launch(Dispatchers.IO) {
            val count = repository.patientCount()
            if (count == 0) {
                val importedCount = repository.importPatientsFromCsv()
                Log.d("MainActivity", "Imported $importedCount individual patient data from CSV")
            } else {
                Log.d("MainActivity", "There are already $count patient data in the database")
            }
        }
        setContent {
            NutriTrackTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)
                    val isLoggedIn = prefs.getBoolean("is_logged_in", false)

                    // Initial route - home if logged in, welcome otherwise
                    val startRoute = if (isLoggedIn) "home" else "welcome"

                    MainContent(startDestination = startRoute)
                }
            }
        }
    }
}

data class BottomNavItem(
    val title: String,
    val selectedIcon: @Composable () -> Unit,
    val unselectedIcon: @Composable () -> Unit,
    val route: String
)

@Composable
fun MainContent(startDestination: String) {
    val navController = rememberNavController()
    val bottomNavItems = listOf(
        BottomNavItem(
            title = "Home",
            selectedIcon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            unselectedIcon = { Icon(Icons.Outlined.Home, contentDescription = "Home") },
            route = "home"
        ),
        BottomNavItem(
            title = "Insights",
            selectedIcon = { Icon(Icons.Filled.Info, contentDescription = "Insights") },
            unselectedIcon = { Icon(Icons.Outlined.Info, contentDescription = "Insights") },
            route = "insights"
        ),
        BottomNavItem(
            title = "Nutricoach",
            selectedIcon = { Icon(Icons.Filled.Spa, contentDescription = "Nutricoach") },
            unselectedIcon = { Icon(Icons.Outlined.Spa, contentDescription = "Nutricoach") },
            route = "nutricoach"
        ),
        BottomNavItem(
            title = "Settings",
            selectedIcon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
            unselectedIcon = { Icon(Icons.Outlined.Settings, contentDescription = "Settings") },
            route = "settings"
        )
    )

    // Keep track of whether the bottom navigation bar should be displayed
    var shouldShowBottomBar by remember { mutableStateOf(false) }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    shouldShowBottomBar = when (currentRoute) {
        "home", "insights", "nutricoach", "settings", "admin_view" -> true
        else -> false
    }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                BottomNavBar(navController = navController, items = bottomNavItems)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Welcome screen
            composable("welcome") {
                WelcomeScreen(navController = navController)
            }

            // Login screen
            composable("login") {
                LoginScreen(navController = navController)
            }

            // Questionnaire screen
            composable(
                route = "questionnaire?isEdit={isEdit}",
                arguments = listOf(
                    navArgument("isEdit") {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) { backStackEntry ->
                val isEdit = backStackEntry.arguments?.getBoolean("isEdit") ?: false
                QuestionnaireScreen(navController = navController, isEdit = isEdit)
            }

            // home
            composable("home") {
                HomeScreen(navController = navController)
            }

            // InsightsScreen
            composable("insights") {
                InsightsScreen(navController = navController)
            }

            // SettingScreen
            composable("settings") {
                SettingScreen(navController = navController)
            }

            // NutriCoachScreen
            composable("nutricoach") {
                NutriCoachScreen(navController = navController)
            }

            // AdminViewScreen
            composable("admin_view") {
                AdminViewScreen(navController = navController)
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController, items: List<BottomNavItem>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = { if (isSelected) item.selectedIcon() else item.unselectedIcon() },
                label = { Text(item.title) },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple pushes to the same destination
                        launchSingleTop = true
                        // Restore status
                        restoreState = true
                    }
                }
            )
        }
    }
}