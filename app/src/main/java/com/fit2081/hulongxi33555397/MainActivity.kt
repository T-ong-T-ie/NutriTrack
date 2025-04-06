package com.fit2081.hulongxi33555397

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter // 替换 SportsCoach
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current
            val prefs = context.getSharedPreferences("NutriTrackPrefs", MODE_PRIVATE)
            val isLoggedIn = prefs.getBoolean("is_logged_in", false)

            val startDestination = if (isLoggedIn) "home" else "welcome"

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Scaffold(
                        bottomBar = {
                            if (isLoggedIn) {
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

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Insights, contentDescription = "Insights") },
            label = { Text("Insights") },
            selected = currentRoute == "insights",
            onClick = { navController.navigate("insights") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.FitnessCenter, contentDescription = "NutriCoach") }, // 替换为 FitnessCenter
            label = { Text("NutriCoach") },
            selected = currentRoute == "nutricoach",
            onClick = { navController.navigate("nutricoach") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Setting") },
            label = { Text("Setting") },
            selected = currentRoute == "setting",
            onClick = { navController.navigate("setting") }
        )
    }
}