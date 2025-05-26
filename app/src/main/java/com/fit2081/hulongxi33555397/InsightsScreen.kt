package com.fit2081.hulongxi33555397

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.content.Intent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fit2081.hulongxi33555397.viewmodel.InsightsViewModel
import android.content.Context
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.fit2081.hulongxi33555397.viewmodel.InsightsUiState
import kotlin.math.roundToInt

// Define the structure for food category scores, including name, score, and maximum score
data class CategoryScore(
    val name: String,
    val score: Float,
    val maxScore: Float
)

// Decimal formatting extension function
private fun Float.format(digits: Int) = "%.${digits}f".format(this)

@Composable
fun InsightsScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)
    val userId = prefs.getString("user_id", "") ?: ""

    // Initializing the ViewModel
    val viewModel: InsightsViewModel = viewModel(
        factory = InsightsViewModel.Factory(context)
    )

    // Collecting UI State
    val uiState by viewModel.uiState.observeAsState(InsightsUiState())

    // Loading User Data
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.loadUserData(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nutrition Score Insights",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Show Total Score Card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Overall Nutrition Score",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${uiState.totalScore.roundToInt()}/${uiState.maxTotalScore.roundToInt()}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = uiState.totalScore / uiState.maxTotalScore,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Base ${if (uiState.isMale) "Male" else "Female"} standard",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Display the scores of each category
        Text(
            text = "Category Rating",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Show scores for all categories
        uiState.categoryScores.forEach { category ->
            CategoryScoreItem(category)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Adding Extra Space
        Spacer(modifier = Modifier.height(24.dp))

        // Share Button
        Button(
            onClick = {
                val totalScore = uiState.totalScore
                val maxTotalScore = uiState.maxTotalScore
                val shareMessage = "Hi, I just got a Food Quality Score of ${totalScore.format(2)}/$maxTotalScore!"
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareMessage)
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share your Food Quality Score"))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Share")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Improve your diet button
        Button(
            onClick = {
                navController.navigate("nutricoach") {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Improve My Diet!")
        }
    }

    // Display loading status or errors
    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.error != null) {
            Text(
                text = uiState.error ?: "Unknown error",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun CategoryScoreItem(category: CategoryScore) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${category.score.roundToInt()}/${category.maxScore.roundToInt()}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = category.score / category.maxScore,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
        }
    }
}