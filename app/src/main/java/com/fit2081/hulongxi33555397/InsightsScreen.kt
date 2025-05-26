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
import com.fit2081.hulongxi33555397.db.NutritrackRepository
import com.fit2081.hulongxi33555397.utils.format
import kotlinx.coroutines.launch

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
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)
    val repository = remember { NutritrackRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    val userId = prefs.getString("user_id", "Unknown") ?: "Unknown"

    // Obtain user data from the database
    var patientData by remember { mutableStateOf<com.fit2081.hulongxi33555397.db.Patient?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Load user data
    LaunchedEffect(userId) {
        coroutineScope.launch {
            try {
                patientData = repository.getPatientById(userId)
                isLoading = false
            } catch (e: Exception) {
                e.printStackTrace()
                isLoading = false
            }
        }
    }

    // Create a mutable list to store category scores
    val categoryScores = mutableListOf<CategoryScore>()
    val isMale = patientData?.sex == "Male"

    // If user data exists, populate the categoryScores list
    patientData?.let { data ->
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

    // Get the user's total score, extracted from database data based on gender
    val totalScore = if (isMale) patientData?.heifaTotalScoreMale ?: 0f else patientData?.heifaTotalScoreFemale ?: 0f
    // The maximum total score is fixed at 100
    val maxTotalScore = 100f

    // Main content area
    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            // Main content area, using a Column for vertical arrangement
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Page title
                Text(
                    text = "Insights: Food Score",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold // Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                categoryScores.forEach { category ->
                    val scorePercentage = (category.score / category.maxScore)
                    val scoreColor = when {
                        scorePercentage >= 0.8f -> Color(0xFF006400) // Dark green for high scores
                        scorePercentage >= 0.6f -> Color(0xFF90EE90) // Light green for medium-high scores
                        scorePercentage >= 0.4f -> Color(0xFFFFD700) // Yellow for medium scores
                        else -> Color(0xFFFF6347) // Red for low scores
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.bodyMedium, // Medium body style
                            modifier = Modifier.width(150.dp)
                        )
                        LinearProgressIndicator(
                            progress = (category.score / category.maxScore).coerceIn(0f, 1f),
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp),
                            color = scoreColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${category.score.format(2)}/${category.maxScore.toInt()}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.width(60.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Total score title, left-aligned
                Text(
                    text = "Total Food Quality Score",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Total score progress bar and value
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = (totalScore / maxTotalScore).coerceIn(0f, 1f),
                        modifier = Modifier
                            .weight(1f)
                            .height(12.dp),
                        color = Color(0xFF006400)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${totalScore.format(2)}/$maxTotalScore",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.width(60.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Share button, launches a share Intent
                Button(onClick = {
                    val shareMessage = "Hi, I just got a Food Quality Score of ${totalScore.format(2)}/$maxTotalScore!"
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareMessage) // Set share text
                        type = "text/plain"
                    }
                    // Launch the share chooser
                    context.startActivity(Intent.createChooser(shareIntent, "Share your Food Quality Score"))
                }) {
                    Text("Share")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Improve diet button, navigates to the NutriCoach page
                Button(onClick = { navController.navigate("nutricoach") }) {
                    Text("Improve My Diet!")
                }
            }
        }
    }
}

