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

// HomeScreen is the main page of the app, displaying user information, diet score, and logout functionality
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreen(navController: NavController) {
    // Get the current context for accessing SharedPreferences and resources
    val context = LocalContext.current
    // Use SharedPreferences to store user data and login status
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)

    // Get the user ID, defaulting to "Unknown"
    val userId = prefs.getString("user_id", "Unknown") ?: "Unknown"
    // Load user data from a CSV file
    val userData = loadUserDataFromCsv(context, userId)
    // Get the food categories selected by the user, stored as a comma-separated string
    val selectedCategories = prefs.getString("${userId}_categories", "")?.split(",") ?: emptyList()

    // Determine if the user is male based on their gender
    val isMale = userData?.sex == "Male"
    // Select the corresponding HEIFA score based on gender, defaulting to 0f if no data is found
    val totalScore = if (isMale) userData?.heifaTotalScoreMale ?: 0f else userData?.heifaTotalScoreFemale ?: 0f
    // The maximum score is fixed at 100
    val maxTotalScore = 100f

    // Use Scaffold to manage the page layout, leaving the top bar empty
    Scaffold(
        topBar = { } // Top bar is not used, left empty
    ) { padding ->
        // Main content area, using a Column for vertical arrangement
        Column(
            modifier = Modifier
                .fillMaxSize() // Fill the entire screen
                .padding(horizontal = 16.dp) // Horizontal padding of 16dp
                .padding(
                    top = 16.dp, // Top padding of 16dp
                    bottom = padding.calculateBottomPadding() // Bottom padding to adapt to the navigation bar
                )
                .verticalScroll(rememberScrollState()), // Enable vertical scrolling
            horizontalAlignment = Alignment.Start // Align content to the left
        ) {
            // Display a gray greeting "Hello,"
            Text(
                text = "Hello,",
                style = MaterialTheme.typography.titleLarge, // Use a large title style
                color = Color.Gray // Gray font color
            )

            // Display the user ID in bold with a large title style
            Text(
                text = userId,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            // Add vertical spacing of 8dp
            Spacer(modifier = Modifier.height(8.dp))

            // Horizontal layout for prompt text and Edit button
            Row(
                modifier = Modifier.fillMaxWidth(), // Fill the width
                horizontalArrangement = Arrangement.SpaceBetween, // Align to both ends
                verticalAlignment = Alignment.CenterVertically // Vertically center
            ) {
                Text(
                    text = "You've already filled in your Food Intake Questionnaire, but you can change details here:",
                    style = MaterialTheme.typography.bodyMedium, // Medium body style
                    modifier = Modifier.weight(1f) // Occupy remaining space
                )

                // Add horizontal spacing of 8dp
                Spacer(modifier = Modifier.width(8.dp))

                // Edit button, navigates to the questionnaire editing page when clicked
                Button(onClick = { navController.navigate("questionnaire?isEdit=true") }) {
                    Text("Edit")
                }
            }

            // Add vertical spacing of 24dp
            Spacer(modifier = Modifier.height(24.dp))

            // Display a healthy eating infographic
            Image(
                painter = painterResource(id = R.drawable.homescreen_picture), // Load image from resources
                contentDescription = "Healthy eating infographic", // Accessibility description
                modifier = Modifier
                    .fillMaxWidth() // Fill the width
                    .aspectRatio(1f), // Maintain a 1:1 aspect ratio
                contentScale = ContentScale.Fit // Display the entire image without cropping
            )

            // Add vertical spacing of 24dp
            Spacer(modifier = Modifier.height(24.dp))

            // Horizontal layout for "My Score" title and "See all scores" button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Score",
                    style = MaterialTheme.typography.headlineMedium, // Large title style
                    fontWeight = FontWeight.Bold // Bold
                )
                // Button to view detailed scores, navigates to the Insights page when clicked
                TextButton(onClick = { navController.navigate("insights") }) {
                    Text("See all scores >")
                }
            }

            // Add vertical spacing of 8dp
            Spacer(modifier = Modifier.height(8.dp))

            // Display the food quality score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Food Quality score",
                    style = MaterialTheme.typography.bodyLarge // Large body style
                )
                Text(
                    text = "${totalScore.format(2)}/100", // Display the score with two decimal places
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF006400) // Use dark green color
                )
            }

            // Add vertical spacing of 36dp
            Spacer(modifier = Modifier.height(36.dp))

            // Add a gray divider line
            Box(
                modifier = Modifier
                    .fillMaxWidth() // Fill the width
                    .height(1.dp) // Height of 1dp
                    .background(Color.Gray) // Gray background
            )

            // Add vertical spacing of 36dp
            Spacer(modifier = Modifier.height(36.dp))

            // Title
            Text(
                text = "What is the Food Quality Score?",
                style = MaterialTheme.typography.headlineSmall, // Small title style
                fontWeight = FontWeight.Bold // Bold
            )

            // Add vertical spacing of 16dp
            Spacer(modifier = Modifier.height(16.dp))

            // Explanation text 1 for the food quality score
            Text(
                text = "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet.",
                style = MaterialTheme.typography.bodyMedium // Medium body style
            )

            // Add vertical spacing of 16dp
            Spacer(modifier = Modifier.height(16.dp))

            // Explanation text 2 for the food quality score
            Text(
                text = "This personalized measurement considers various food groups including vegetables, fruits, whole grains, and proteins to give you practical insights for making healthier food choices.",
                style = MaterialTheme.typography.bodyMedium // Medium body style
            )

            // Add vertical spacing of 16dp
            Spacer(modifier = Modifier.height(16.dp))

            // Logout button
            Button(
                onClick = {
                    with(prefs.edit()) {
                        // Remove only the login status, keep other data (e.g., questionnaire)
                        remove("is_logged_in")
                        apply()
                    }
                    // Navigate to the welcome page, clearing the navigation stack
                    navController.navigate("welcome") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally) // Center the button
            ) {
                Text("Logout")
            }

            // Add vertical spacing of 16dp to ensure bottom padding
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}