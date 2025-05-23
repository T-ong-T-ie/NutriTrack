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

// MainActivity is responsible for setting up the application UI and navigation structure
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current
            val prefs = context.getSharedPreferences("NutriTrackPrefs", MODE_PRIVATE)
            val isLoggedIn = prefs.getBoolean("is_logged_in", false)

            // Dynamically determine the startup page
            val startDestination = if (isLoggedIn) {
                val userId = prefs.getString("user_id", "Unknown") ?: "Unknown"
                if (prefs.getString("${userId}_categories", null) != null) {
                    "home"
                } else {
                    "questionnaire"
                }
            } else {
                "welcome"
            }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route ?: ""

                    // Show bottom navigation bar only on certain pages
                    val shouldShowBottomBar = !currentRoute.startsWith("welcome") &&
                            !currentRoute.startsWith("login") &&
                            !currentRoute.startsWith("questionnaire")

                    Scaffold(
                        bottomBar = {
                            if (shouldShowBottomBar) {
                                BottomNavigationBar(navController)
                            }
                        }
                    ) { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            composable("welcome") { WelcomeScreen(navController) }
                            composable("login") { LoginScreen(navController) }
                            composable("home") { HomeScreen(navController) }
                            composable("questionnaire?isEdit={isEdit}") { backStackEntry ->
                                QuestionnaireScreen(
                                    navController,
                                    isEdit = backStackEntry.arguments?.getString("isEdit")?.toBoolean() ?: false
                                )
                            }
                            composable("insights") { InsightsScreen(navController) }
                            composable("nutricoach") { NutriCoachScreen(navController) }
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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // NavigationBar is the Material 3 bottom navigation bar component
    NavigationBar {
        // Home navigation item
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "home",
            onClick = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            }
        )
        // Insights navigation item
        NavigationBarItem(
            icon = { Icon(Icons.Default.Insights, contentDescription = "Insights") },
            label = { Text("Insights") },
            selected = currentRoute == "insights",
            onClick = { navController.navigate("insights") }
        )
        // NutriCoach navigation item
        NavigationBarItem(
            icon = { Icon(Icons.Default.FitnessCenter, contentDescription = "NutriCoach") },
            label = { Text("NutriCoach") },
            selected = currentRoute == "nutricoach",
            onClick = { navController.navigate("nutricoach") }
        )
        // Setting navigation item
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Setting") },
            label = { Text("Setting") },
            selected = currentRoute == "setting",
            onClick = { navController.navigate("setting") }
        )
    }
}