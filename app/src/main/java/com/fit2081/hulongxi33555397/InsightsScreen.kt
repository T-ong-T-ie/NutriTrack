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
    val userData = loadUserDataFromCsv(context, userId)
    val isMale = userData?.sex == "Male"

    // 直接从用户数据中获取各类别分数
    val categoryScores = mutableListOf<CategoryScore>()

    userData?.let { data ->
        // 添加所有食物类别的得分
        categoryScores.add(CategoryScore(
            name = "Discretionary",
            score = if (isMale) data.DiscretionaryHEIFAscoreMale else data.DiscretionaryHEIFAscoreFemale,
            maxScore = 10f
        ))

        categoryScores.add(CategoryScore(
            name = "Vegetables",
            score = if (isMale) data.VegetablesHEIFAscoreMale else data.VegetablesHEIFAscoreFemale,
            maxScore = 10f
        ))

        categoryScores.add(CategoryScore(
            name = "Fruits",
            score = if (isMale) data.FruitHEIFAscoreMale else data.FruitHEIFAscoreFemale,
            maxScore = 10f
        ))

        categoryScores.add(CategoryScore(
            name = "Grains & Cereals",
            score = if (isMale) data.GrainsandcerealsHEIFAscoreMale else data.GrainsandcerealsHEIFAscoreFemale,
            maxScore = 5f
        ))

        categoryScores.add(CategoryScore(
            name = "Whole Grains",
            score = if (isMale) data.WholegrainsHEIFAscoreMale else data.WholegrainsHEIFAscoreFemale,
            maxScore = 5f
        ))

        categoryScores.add(CategoryScore(
            name = "Meat & Alternatives",
            score = if (isMale) data.MeatandalternativesHEIFAscoreMale else data.MeatandalternativesHEIFAscoreFemale,
            maxScore = 10f
        ))

        categoryScores.add(CategoryScore(
            name = "Dairy & Alternatives",
            score = if (isMale) data.DairyandalternativesHEIFAscoreMale else data.DairyandalternativesHEIFAscoreFemale,
            maxScore = 10f
        ))

        categoryScores.add(CategoryScore(
            name = "Sodium",
            score = if (isMale) data.SodiumHEIFAscoreMale else data.SodiumHEIFAscoreFemale,
            maxScore = 10f
        ))

        categoryScores.add(CategoryScore(
            name = "Alcohol",
            score = if (isMale) data.AlcoholHEIFAscoreMale else data.AlcoholHEIFAscoreFemale,
            maxScore = 5f
        ))

        categoryScores.add(CategoryScore(
            name = "Water",
            score = if (isMale) data.WaterHEIFAscoreMale else data.WaterHEIFAscoreFemale,
            maxScore = 5f
        ))

        categoryScores.add(CategoryScore(
            name = "Added Sugar",
            score = if (isMale) data.SugarHEIFAscoreMale else data.SugarHEIFAscoreFemale,
            maxScore = 10f
        ))

        categoryScores.add(CategoryScore(
            name = "Saturated Fat",
            score = if (isMale) data.SaturatedFatHEIFAscoreMale else data.SaturatedFatHEIFAscoreFemale,
            maxScore = 5f
        ))

        categoryScores.add(CategoryScore(
            name = "Unsaturated Fat",
            score = if (isMale) data.UnsaturatedFatHEIFAscoreMale else data.UnsaturatedFatHEIFAscoreFemale,
            maxScore = 5f
        ))
    }

    // 直接使用 CSV 中的总分值
    val totalScore = if (isMale) userData?.heifaTotalScoreMale ?: 0f else userData?.heifaTotalScoreFemale ?: 0f
    val maxTotalScore = 100f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
            val scorePercentage = (category.score / category.maxScore)
            // 根据得分百分比决定颜色
            val scoreColor = when {
                scorePercentage >= 0.8f -> Color(0xFF006400) // 深绿色
                scorePercentage >= 0.6f -> Color(0xFF90EE90) // 浅绿色
                scorePercentage >= 0.4f -> Color(0xFFFFD700) // 黄色
                else -> Color(0xFFFF6347) // 红色
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp), // 统一的垂直内边距，确保对齐
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyMedium,
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

        Button(onClick = {
            val shareMessage = "Hi, I just got a Food Quality Score of ${totalScore.format(2)}/$maxTotalScore!"
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

// 用于格式化浮点数的扩展函数，修改为两位小数
fun Float.format(digits: Int) = "%.${digits}f".format(this)