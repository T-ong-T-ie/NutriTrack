package com.fit2081.hulongxi33555397

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.fit2081.hulongxi33555397.viewmodel.HomeViewModel
import kotlin.math.roundToInt

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)

    // Using ViewModel
    val viewModel: HomeViewModel = viewModel()

    // Get user ID, default is "Unknown"
    val userId = prefs.getString("user_id", "Unknown") ?: "Unknown"
    val selectedCategories = prefs.getString("${userId}_categories", "")?.split(",") ?: emptyList()

    // Observing State from ViewModel
    val isLoading by viewModel.isLoading.observeAsState(true)
    val patientData by viewModel.patientData.observeAsState()

    // Loading User Data
    LaunchedEffect(userId) {
        viewModel.loadPatientData(userId)
    }

    // Extract score information from user data
    val isMale = patientData?.sex == "Male"
    val totalScore = if (isMale) patientData?.heifaTotalScoreMale ?: 0f else patientData?.heifaTotalScoreFemale ?: 0f
    val maxTotalScore = 100f

    // Use Scaffold to manage page layout, leaving the top bar empty
    Scaffold(
        topBar = { }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Main content area, using a Column for vertical arrangement
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                // Display a gray greeting "Hello,"
                Text(
                    text = "Hello,",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Gray
                )

                // Display username
                Text(
                    text = patientData?.name ?: userId,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                // Add vertical spacing of 8dp
                Spacer(modifier = Modifier.height(8.dp))

                // Horizontal layout for prompt text and Edit button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "You've already filled in your Food Intake Questionnaire, but you can change details here:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(onClick = { navController.navigate("questionnaire?isEdit=true") }) {
                        Text("Edit")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Display a healthy eating infographic
                Image(
                    painter = painterResource(id = R.drawable.homescreen_picture), // Load image from resources
                    contentDescription = "Healthy eating infographic",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Horizontal layout for "My Score" title and "See all scores" button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Score",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    // Button to view detailed scores, navigate to the Insights page when clicked
                    TextButton(
                        onClick = {
                            navController.navigate("insights") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    ) {
                        Text("See all scores >")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Display the food quality score
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Food Quality score",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "${totalScore.roundToInt()}/100",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF006400)
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                // Add a gray divider line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.Gray)
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Title
                Text(
                    text = "What is the Food Quality Score?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Explanation text 1 for the food quality score
                Text(
                    text = "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Explanation text 2 for the food quality score
                Text(
                    text = "This personalized measurement considers various food groups including vegetables, fruits, whole grains, and proteins to give you practical insights for making healthier food choices.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}