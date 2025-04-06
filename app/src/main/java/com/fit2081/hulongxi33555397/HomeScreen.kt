package com.fit2081.hulongxi33555397

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)

    val userId = prefs.getString("user_id", "Unknown") ?: "Unknown"
    val userData = loadUserDataFromCsv(userId)
    val heifaScore = if (userData != null) {
        if (userData.sex == "Male") userData.heifaTotalScoreMale else userData.heifaTotalScoreFemale
    } else 50.0f

    val categoryScores = listOf(
        CategoryScore("Discretionary", 5f, 10f),
        CategoryScore("Vegetables", 8f, 10f),
        CategoryScore("Fruits", 5f, 10f),
        CategoryScore("Grains", 5f, 10f),
        CategoryScore("Meat", 10f, 10f),
        CategoryScore("Dairy", 10f, 10f),
        CategoryScore("Water", 3f, 5f),
        CategoryScore("Fats", 4f, 10f),
        CategoryScore("Sodium", 8f, 10f),
        CategoryScore("Added Sugars", 7f, 10f),
        CategoryScore("Alcohol", 5f, 5f)
    )
    val totalScore = categoryScores.sumOf { it.score.toDouble() }.toFloat()
    val maxTotalScore = 100f

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Hello,",
                        color = Color.Gray,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    Button(onClick = { navController.navigate("questionnaire?isEdit=true") }) {
                        Text("Edit")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = userId,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "You've already filled in your Food Intake Questionnaire, but you can change details here:",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

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
                TextButton(onClick = { navController.navigate("insights") }) {
                    Text("See all score >")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your Food Quality Score  $totalScore/100",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.Gray)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "What is the Food Quality Score?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement. This personalized measurement considers various food groups including vegetables, fruits, whole grains, and proteins to give you practical insights for making healthier food choices.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 修改HomeScreen.kt中的登出按钮点击处理
            Button(
                onClick = {
                    with(prefs.edit()) {
                        clear()
                        putBoolean("is_logged_in", false) // 标记未登录
                        apply()
                    }
                    // 修改导航逻辑，确保回到welcome页面
                    navController.navigate("welcome") {
                        popUpTo(0) { inclusive = true } // 清除整个导航栈
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Logout")
            }
        }
    }
}