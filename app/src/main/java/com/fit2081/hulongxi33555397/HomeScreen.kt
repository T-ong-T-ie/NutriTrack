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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)

    val userId = prefs.getString("user_id", "Unknown") ?: "Unknown"
    val userData = loadUserDataFromCsv(context, userId)
    val selectedCategories = prefs.getString("categories", "")?.split(",") ?: emptyList()

    // 使用实际用户数据计算分数
    val categoryScores = calculateScoreFromUserData(userData, selectedCategories).map { category ->
        when (category.name) {
            "Grains", "Whole grains", "Water", "Alcohol", "Saturated Fat", "Unsaturated Fat" ->
                CategoryScore(
                    name = category.name,
                    score = category.score.coerceAtMost(5f),
                    maxScore = 5f
                )
            else -> category
        }
    }

    val totalScore = categoryScores.sumOf { it.score.toDouble() }.toFloat()
    val maxTotalScore = 100f

    Scaffold(
        topBar = { }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(
                    top = 16.dp,
                    bottom = padding.calculateBottomPadding()
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            // 问候语
            Text(
                text = "Hello,",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Gray
            )

            Text(
                text = userId,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            Image(
                painter = painterResource(id = R.drawable.homescreen_picture),
                contentDescription = "健康饮食信息图",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f), // 保持1:1的宽高比
                contentScale = ContentScale.Fit // 完整显示图片
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                    Text("See all scores >")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
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
                    text = "$totalScore/100",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF006400) // 正确的深绿色声明
                )
            }

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
                text = "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet.  This personalized measurement considers various food groups including vegetables, fruits, whole grains, and proteins to give you practical insights for making healthier food choices.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    with(prefs.edit()) {
                        clear()
                        putBoolean("is_logged_in", false)
                        apply()
                    }
                    navController.navigate("welcome") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Logout")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}