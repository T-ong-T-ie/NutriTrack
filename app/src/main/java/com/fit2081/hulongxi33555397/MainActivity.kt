package com.fit2081.hulongxi33555397

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// Main activity class responsible for setting up the app's UI and navigation structure
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Create a navigation controller to manage page transitions
            val navController = rememberNavController()
            // Get the current context for accessing SharedPreferences
            val context = LocalContext.current
            // Use SharedPreferences to store user data and login status
            val prefs = context.getSharedPreferences("NutriTrackPrefs", MODE_PRIVATE)
            // Check if the user is logged in, default is false
            val isLoggedIn = prefs.getBoolean("is_logged_in", false)

            // Dynamically determine the app's start page
            val startDestination = if (isLoggedIn) {
                // Get the user ID, default is "Unknown"
                val userId = prefs.getString("user_id", "Unknown") ?: "Unknown"
                // Check if the user has filled out the questionnaire data (check the user-specific categories key)
                if (prefs.getString("${userId}_categories", null) != null) {
                    "home" // If questionnaire data exists, start at HomeScreen
                } else {
                    "questionnaire" // If no questionnaire data, go to QuestionnaireScreen
                }
            } else {
                "welcome" // If not logged in, go to WelcomeScreen
            }

            // Use MaterialTheme to provide consistent theme styling
            MaterialTheme {
                // Surface fills the entire screen and provides a background
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Get the current navigation route to dynamically display the navigation bar
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route ?: ""

                    // Determine whether to show the bottom navigation bar
                    // Only show on pages other than welcome, login, and questionnaire
                    val shouldShowBottomBar = !currentRoute.startsWith("welcome") &&
                            !currentRoute.startsWith("login") &&
                            !currentRoute.startsWith("questionnaire")

                    // Use Scaffold to manage the app's layout structure, including the bottom navigation bar
                    Scaffold(
                        bottomBar = {
                            if (shouldShowBottomBar) {
                                BottomNavigationBar(navController) // Show the navigation bar
                            }
                            // If not logged in or on specific pages, bottomBar is empty and the navigation bar is not displayed
                        }
                    ) { paddingValues ->
                        // NavHost defines the navigation graph and manages all page routes
                        NavHost(
                            navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier.padding(paddingValues) // Apply Scaffold's padding to avoid content being obscured by the navigation bar
                        ) {
                            // Define the welcome page route
                            composable("welcome") { WelcomeScreen(navController) }
                            // Define the login page route
                            composable("login") { LoginScreen(navController) }
                            // Define the home page route
                            composable("home") { HomeScreen(navController) }
                            // Define the questionnaire page route, supporting the edit mode parameter
                            composable("questionnaire?isEdit={isEdit}") { backStackEntry ->
                                QuestionnaireScreen(
                                    navController,
                                    isEdit = backStackEntry.arguments?.getString("isEdit")?.toBoolean() ?: false
                                )
                            }
                            // Define the insights page route
                            composable("insights") { InsightsScreen(navController) }
                            // Define the NutriCoach page route
                            composable("nutricoach") { NutriCoachScreen(navController) }
                            // Define the settings page route
                            composable("setting") { SettingScreen(navController) }
                        }
                    }
                }
            }
        }
    }
}

// Define the bottom navigation bar as a Composable function
@Composable
fun BottomNavigationBar(navController: NavController) {
    // Get the current navigation route to highlight the selected item
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // NavigationBar is the Material 3 bottom navigation bar component
    NavigationBar {
        // Home navigation item
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") }, // Home icon
            label = { Text("Home") }, // Label text
            selected = currentRoute == "home", // Highlight when the current route is "home"
            onClick = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true } // Navigate to Home and clear the stack up to Home
                }
            }
        )
        // Insights navigation item
        NavigationBarItem(
            icon = { Icon(Icons.Default.Insights, contentDescription = "Insights") }, // Insights icon
            label = { Text("Insights") },
            selected = currentRoute == "insights",
            onClick = { navController.navigate("insights") } // Navigate to Insights
        )
        // NutriCoach navigation item
        NavigationBarItem(
            icon = { Icon(Icons.Default.FitnessCenter, contentDescription = "NutriCoach") }, // NutriCoach icon
            label = { Text("NutriCoach") },
            selected = currentRoute == "nutricoach",
            onClick = { navController.navigate("nutricoach") } // Navigate to NutriCoach
        )
        // Setting navigation item
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Setting") }, // Settings icon
            label = { Text("Setting") },
            selected = currentRoute == "setting",
            onClick = { navController.navigate("setting") } // Navigate to Setting
        )
    }
}