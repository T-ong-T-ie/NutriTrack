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
        Text(
            text = "Insights: Food Score",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

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
                    modifier = Modifier.width(120.dp)
                )
                LinearProgressIndicator(
                    progress = progress.coerceIn(0f, 1f),
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${category.score}/${category.maxScore}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(60.dp)
                )
            }
        }

        // 增加类别进度条与总分进度条之间的间距
        Spacer(modifier = Modifier.height(40.dp))

        // 总分标题左对齐
        Text(
            text = "Total Food Quality Score",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // 总分进度条
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
                    .height(12.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$totalScore/$maxTotalScore",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.width(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            val shareMessage = "Hi, I just got a Food Quality Score of $totalScore/100!"
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareMessage)
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share your Food Quality Score"))
        }) {
            Text("Share")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { navController.navigate("nutricoach") }) {
            Text("Improve My Diet!")
        }
    }
}