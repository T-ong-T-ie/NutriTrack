package com.fit2081.hulongxi33555397

import androidx.compose.foundation.layout.*
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

data class CategoryScore(
    val name: String,
    val score: Float,
    val maxScore: Float
)

@Composable
fun InsightsScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)

    val userId = prefs.getString("user_id", "Unknown") ?: "Unknown"
    val selectedCategories = prefs.getString("categories", "None")?.split(",") ?: listOf("None")
    val persona = prefs.getString("persona", "None") ?: "None"
    val time = prefs.getString("time", "Not set") ?: "Not set"

    val userData = loadUserDataFromCsv(userId)
    val heifaScore = if (userData != null) {
        if (userData.sex == "Male") userData.heifaTotalScoreMale else userData.heifaTotalScoreFemale
    } else 0f

    // 定义所有类别及其满分
    val categoryScores = listOf(
        CategoryScore("Discretionary", if (selectedCategories.contains("Discretionary")) 5f else 0f, 10f),
        CategoryScore("Vegetables", if (selectedCategories.contains("Vegetables")) 8f else 0f, 10f),
        CategoryScore("Fruits", if (selectedCategories.contains("Fruit")) 5f else 0f, 10f),
        CategoryScore("Grains", if (selectedCategories.contains("Grains")) 5f else 0f, 10f),
        CategoryScore("Meat", if (selectedCategories.contains("Meat")) 10f else 0f, 10f),
        CategoryScore("Dairy", if (selectedCategories.contains("Dairy")) 10f else 0f, 10f),
        CategoryScore("Water", 3f, 5f),
        CategoryScore("Fats", 4f, 10f),
        CategoryScore("Sodium", 8f, 10f),
        CategoryScore("Added Sugars", 7f, 10f),
        CategoryScore("Alcohol", 5f, 5f)
    )

    val totalScore = categoryScores.sumOf { it.score.toDouble() }.toFloat()
    val maxTotalScore = 100f // 9×10 + 2×5 = 100

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Insights", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Text("User ID: $userId", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Total Score: $totalScore / $maxTotalScore",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Score Breakdown:", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        // 对齐显示每个类别的进度条
        categoryScores.forEach { category ->
            val progress = category.score / category.maxScore
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .width(120.dp) // 固定宽度确保名称对齐
                        .padding(end = 8.dp)
                )
                LinearProgressIndicator(
                    progress = progress.coerceIn(0f, 1f),
                    modifier = Modifier
                        .weight(1f) // 占用剩余空间，确保进度条对齐
                        .height(8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${category.score}/${category.maxScore}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(60.dp) // 固定宽度确保分数对齐
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Selected Persona: $persona", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Text("Time: $time", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val shareMessage = "Hi, I just got a HEIFA score of $totalScore!"
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareMessage)
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share your HEIFA Score"))
        }) {
            Text("Share")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { navController.navigate("nutricoach") }) {
            Text("Improve My Diet!")
        }
    }
}