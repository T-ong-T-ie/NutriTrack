package com.fit2081.hulongxi33555397

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color

// Define the structure for food category scores, including name, score, and maximum score
data class CategoryScore(
    val name: String,
    val score: Float,
    val maxScore: Float
)

// InsightsScreen displays detailed food quality scores for the user, including category scores and total score
@Composable
fun InsightsScreen(navController: NavController) {
    // Get the current context for accessing SharedPreferences and launching Intents
    val context = LocalContext.current
    // Use SharedPreferences to store user data
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)

    // Get the user ID, defaulting to "Unknown"
    val userId = prefs.getString("user_id", "Unknown") ?: "Unknown"
    // Load user data from a CSV file
    val userData = loadUserDataFromCsv(context, userId)
    // Determine if the user is male
    val isMale = userData?.sex == "Male"

    // Create a mutable list to store category scores
    val categoryScores = mutableListOf<CategoryScore>()

    // If user data exists, populate the categoryScores list
    userData?.let { data ->
        // Add the "Discretionary" category score
        categoryScores.add(CategoryScore(
            name = "Discretionary",
            score = if (isMale) data.DiscretionaryHEIFAscoreMale else data.DiscretionaryHEIFAscoreFemale,
            maxScore = 10f
        ))

        // Add the "Vegetables" category score
        categoryScores.add(CategoryScore(
            name = "Vegetables",
            score = if (isMale) data.VegetablesHEIFAscoreMale else data.VegetablesHEIFAscoreFemale,
            maxScore = 10f
        ))

        // Add the "Fruits" category score
        categoryScores.add(CategoryScore(
            name = "Fruits",
            score = if (isMale) data.FruitHEIFAscoreMale else data.FruitHEIFAscoreFemale,
            maxScore = 10f
        ))

        // Add the "Grains & Cereals" category score
        categoryScores.add(CategoryScore(
            name = "Grains & Cereals",
            score = if (isMale) data.GrainsandcerealsHEIFAscoreMale else data.GrainsandcerealsHEIFAscoreFemale,
            maxScore = 5f
        ))

        // Add the "Whole Grains" category score
        categoryScores.add(CategoryScore(
            name = "Whole Grains",
            score = if (isMale) data.WholegrainsHEIFAscoreMale else data.WholegrainsHEIFAscoreFemale,
            maxScore = 5f
        ))

        // Add the "Meat & Alternatives" category score
        categoryScores.add(CategoryScore(
            name = "Meat & Alternatives",
            score = if (isMale) data.MeatandalternativesHEIFAscoreMale else data.MeatandalternativesHEIFAscoreFemale,
            maxScore = 10f
        ))

        // Add the "Dairy & Alternatives" category score
        categoryScores.add(CategoryScore(
            name = "Dairy & Alternatives",
            score = if (isMale) data.DairyandalternativesHEIFAscoreMale else data.DairyandalternativesHEIFAscoreFemale,
            maxScore = 10f
        ))

        // Add the "Sodium" category score
        categoryScores.add(CategoryScore(
            name = "Sodium",
            score = if (isMale) data.SodiumHEIFAscoreMale else data.SodiumHEIFAscoreFemale,
            maxScore = 10f
        ))

        // Add the "Alcohol" category score
        categoryScores.add(CategoryScore(
            name = "Alcohol",
            score = if (isMale) data.AlcoholHEIFAscoreMale else data.AlcoholHEIFAscoreFemale,
            maxScore = 5f
        ))

        // Add the "Water" category score
        categoryScores.add(CategoryScore(
            name = "Water",
            score = if (isMale) data.WaterHEIFAscoreMale else data.WaterHEIFAscoreFemale,
            maxScore = 5f
        ))

        // Add the "Added Sugar" category score
        categoryScores.add(CategoryScore(
            name = "Added Sugar",
            score = if (isMale) data.SugarHEIFAscoreMale else data.SugarHEIFAscoreFemale,
            maxScore = 10f
        ))

        // Add the "Saturated Fat" category score
        categoryScores.add(CategoryScore(
            name = "Saturated Fat",
            score = if (isMale) data.SaturatedFatHEIFAscoreMale else data.SaturatedFatHEIFAscoreFemale,
            maxScore = 5f
        ))

        // Add the "Unsaturated Fat" category score
        categoryScores.add(CategoryScore(
            name = "Unsaturated Fat",
            score = if (isMale) data.UnsaturatedFatHEIFAscoreMale else data.UnsaturatedFatHEIFAscoreFemale,
            maxScore = 5f
        ))
    }

    // Get the user's total score, extracted from CSV data based on gender
    val totalScore = if (isMale) userData?.heifaTotalScoreMale ?: 0f else userData?.heifaTotalScoreFemale ?: 0f
    // The maximum total score is fixed at 100
    val maxTotalScore = 100f

    // Main content area, using a Column for vertical arrangement
    Column(
        modifier = Modifier
            .fillMaxSize() // Fill the entire screen
            .verticalScroll(rememberScrollState()) // Enable vertical scrolling
            .padding(16.dp), // Padding of 16dp on all sides
        horizontalAlignment = Alignment.CenterHorizontally, // Center content horizontally
        verticalArrangement = Arrangement.Top // Arrange content from the top
    ) {
        // Page title
        Text(
            text = "Insights: Food Score",
            style = MaterialTheme.typography.headlineMedium, // Medium headline style
            fontWeight = FontWeight.Bold // Bold
        )

        // Add vertical spacing of 24dp
        Spacer(modifier = Modifier.height(24.dp))

        // Iterate and display progress bars for each category
        categoryScores.forEach { category ->
            // Calculate the score percentage
            val scorePercentage = (category.score / category.maxScore)
            // Choose the progress bar color based on the score percentage
            val scoreColor = when {
                scorePercentage >= 0.8f -> Color(0xFF006400) // Dark green for high scores
                scorePercentage >= 0.6f -> Color(0xFF90EE90) // Light green for medium-high scores
                scorePercentage >= 0.4f -> Color(0xFFFFD700) // Yellow for medium scores
                else -> Color(0xFFFF6347) // Red for low scores
            }

            // Horizontal layout for category name, progress bar, and score
            Row(
                modifier = Modifier
                    .fillMaxWidth() // Fill the width
                    .padding(vertical = 4.dp), // Vertical padding of 4dp for alignment
                verticalAlignment = Alignment.CenterVertically // Center vertically
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyMedium, // Medium body style
                    modifier = Modifier.width(150.dp) // Fixed width for alignment
                )
                LinearProgressIndicator(
                    progress = (category.score / category.maxScore).coerceIn(0f, 1f), // Progress value limited to 0-1
                    modifier = Modifier
                        .weight(1f) // Occupy remaining space
                        .height(8.dp), // Progress bar height of 8dp
                    color = scoreColor // Set color based on score
                )
                Spacer(modifier = Modifier.width(8.dp)) // Add horizontal spacing of 8dp
                Text(
                    text = "${category.score.format(2)}/${category.maxScore.toInt()}", // Display score with two decimals
                    style = MaterialTheme.typography.bodySmall, // Small body style
                    modifier = Modifier.width(60.dp) // Fixed width for alignment
                )
            }
        }

        // Add vertical spacing of 40dp to separate categories and total score
        Spacer(modifier = Modifier.height(40.dp))

        // Total score title, left-aligned
        Text(
            text = "Total Food Quality Score",
            style = MaterialTheme.typography.titleLarge, // Large title style
            fontWeight = FontWeight.Bold, // Bold
            modifier = Modifier.align(Alignment.Start) // Align to the left
        )

        // Add vertical spacing of 8dp
        Spacer(modifier = Modifier.height(8.dp))

        // Total score progress bar and value
        Row(
            modifier = Modifier
                .fillMaxWidth() // Fill the width
                .padding(vertical = 4.dp), // Vertical padding of 4dp
            verticalAlignment = Alignment.CenterVertically // Center vertically
        ) {
            LinearProgressIndicator(
                progress = (totalScore / maxTotalScore).coerceIn(0f, 1f), // Total score progress, limited to 0-1
                modifier = Modifier
                    .weight(1f) // Occupy remaining space
                    .height(12.dp), // Progress bar height of 12dp
                color = Color(0xFF006400) // Fixed dark green color
            )
            Spacer(modifier = Modifier.width(8.dp)) // Add horizontal spacing of 8dp
            Text(
                text = "${totalScore.format(2)}/$maxTotalScore", // Display total score with two decimals
                style = MaterialTheme.typography.bodySmall, // Small body style
                modifier = Modifier.width(60.dp) // Fixed width
            )
        }

        // Add vertical spacing of 24dp
        Spacer(modifier = Modifier.height(24.dp))

        // Share button, launches a share Intent
        Button(onClick = {
            val shareMessage = "Hi, I just got a Food Quality Score of ${totalScore.format(2)}/$maxTotalScore!"
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareMessage) // Set share text
                type = "text/plain" // Text type
            }
            // Launch the share chooser
            context.startActivity(Intent.createChooser(shareIntent, "Share your Food Quality Score"))
        }) {
            Text("Share")
        }

        // Add vertical spacing of 8dp
        Spacer(modifier = Modifier.height(8.dp))

        // Improve diet button, navigates to the NutriCoach page
        Button(onClick = { navController.navigate("nutricoach") }) {
            Text("Improve My Diet!")
        }
    }
}

// Extension function: Format a float to a string with the specified number of decimal places
fun Float.format(digits: Int) = "%.${digits}f".format(this)