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

// 定义食物类别分数的结构体，包含名称、得分和最大分值
data class CategoryScore(
    val name: String,
    val score: Float,
    val maxScore: Float
)

// InsightsScreen 显示用户的食物质量分数详情，包括各类别得分和总分
@Composable
fun InsightsScreen(navController: NavController) {
    // 获取当前上下文，用于访问 SharedPreferences 和启动 Intent
    val context = LocalContext.current
    // 使用 SharedPreferences 存储用户数据
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)

    // 获取用户 ID，默认为 "Unknown"
    val userId = prefs.getString("user_id", "Unknown") ?: "Unknown"
    // 从 CSV 文件加载用户数据
    val userData = loadUserDataFromCsv(context, userId)
    // 判断用户性别是否为男性
    val isMale = userData?.sex == "Male"

    // 创建一个可变列表，用于存储各类别的分数
    val categoryScores = mutableListOf<CategoryScore>()

    // 如果用户数据存在，则填充 categoryScores 列表
    userData?.let { data ->
        // 添加 "Discretionary" 类别的得分
        categoryScores.add(CategoryScore(
            name = "Discretionary",
            score = if (isMale) data.DiscretionaryHEIFAscoreMale else data.DiscretionaryHEIFAscoreFemale,
            maxScore = 10f
        ))

        // 添加 "Vegetables" 类别的得分
        categoryScores.add(CategoryScore(
            name = "Vegetables",
            score = if (isMale) data.VegetablesHEIFAscoreMale else data.VegetablesHEIFAscoreFemale,
            maxScore = 10f
        ))

        // 添加 "Fruits" 类别的得分
        categoryScores.add(CategoryScore(
            name = "Fruits",
            score = if (isMale) data.FruitHEIFAscoreMale else data.FruitHEIFAscoreFemale,
            maxScore = 10f
        ))

        // 添加 "Grains & Cereals" 类别的得分
        categoryScores.add(CategoryScore(
            name = "Grains & Cereals",
            score = if (isMale) data.GrainsandcerealsHEIFAscoreMale else data.GrainsandcerealsHEIFAscoreFemale,
            maxScore = 5f
        ))

        // 添加 "Whole Grains" 类别的得分
        categoryScores.add(CategoryScore(
            name = "Whole Grains",
            score = if (isMale) data.WholegrainsHEIFAscoreMale else data.WholegrainsHEIFAscoreFemale,
            maxScore = 5f
        ))

        // 添加 "Meat & Alternatives" 类别的得分
        categoryScores.add(CategoryScore(
            name = "Meat & Alternatives",
            score = if (isMale) data.MeatandalternativesHEIFAscoreMale else data.MeatandalternativesHEIFAscoreFemale,
            maxScore = 10f
        ))

        // 添加 "Dairy & Alternatives" 类别的得分
        categoryScores.add(CategoryScore(
            name = "Dairy & Alternatives",
            score = if (isMale) data.DairyandalternativesHEIFAscoreMale else data.DairyandalternativesHEIFAscoreFemale,
            maxScore = 10f
        ))

        // 添加 "Sodium" 类别的得分
        categoryScores.add(CategoryScore(
            name = "Sodium",
            score = if (isMale) data.SodiumHEIFAscoreMale else data.SodiumHEIFAscoreFemale,
            maxScore = 10f
        ))

        // 添加 "Alcohol" 类别的得分
        categoryScores.add(CategoryScore(
            name = "Alcohol",
            score = if (isMale) data.AlcoholHEIFAscoreMale else data.AlcoholHEIFAscoreFemale,
            maxScore = 5f
        ))

        // 添加 "Water" 类别的得分
        categoryScores.add(CategoryScore(
            name = "Water",
            score = if (isMale) data.WaterHEIFAscoreMale else data.WaterHEIFAscoreFemale,
            maxScore = 5f
        ))

        // 添加 "Added Sugar" 类别的得分
        categoryScores.add(CategoryScore(
            name = "Added Sugar",
            score = if (isMale) data.SugarHEIFAscoreMale else data.SugarHEIFAscoreFemale,
            maxScore = 10f
        ))

        // 添加 "Saturated Fat" 类别的得分
        categoryScores.add(CategoryScore(
            name = "Saturated Fat",
            score = if (isMale) data.SaturatedFatHEIFAscoreMale else data.SaturatedFatHEIFAscoreFemale,
            maxScore = 5f
        ))

        // 添加 "Unsaturated Fat" 类别的得分
        categoryScores.add(CategoryScore(
            name = "Unsaturated Fat",
            score = if (isMale) data.UnsaturatedFatHEIFAscoreMale else data.UnsaturatedFatHEIFAscoreFemale,
            maxScore = 5f
        ))
    }

    // 获取用户总分，基于性别从 CSV 数据中提取
    val totalScore = if (isMale) userData?.heifaTotalScoreMale ?: 0f else userData?.heifaTotalScoreFemale ?: 0f
    // 总分的最大值固定为 100
    val maxTotalScore = 100f

    // 主内容区域，使用 Column 垂直排列
    Column(
        modifier = Modifier
            .fillMaxSize() // 填充整个屏幕
            .verticalScroll(rememberScrollState()) // 启用垂直滚动
            .padding(16.dp), // 四周 padding 16dp
        horizontalAlignment = Alignment.CenterHorizontally, // 内容水平居中
        verticalArrangement = Arrangement.Top // 从顶部开始排列
    ) {
        // 页面标题
        Text(
            text = "Insights: Food Score",
            style = MaterialTheme.typography.headlineMedium, // 中等标题样式
            fontWeight = FontWeight.Bold // 加粗
        )

        // 添加 24dp 的垂直间距
        Spacer(modifier = Modifier.height(24.dp))

        // 遍历并显示每个类别的进度条
        categoryScores.forEach { category ->
            // 计算得分百分比
            val scorePercentage = (category.score / category.maxScore)
            // 根据得分百分比选择进度条颜色
            val scoreColor = when {
                scorePercentage >= 0.8f -> Color(0xFF006400) // 深绿色，高分
                scorePercentage >= 0.6f -> Color(0xFF90EE90) // 浅绿色，中高分
                scorePercentage >= 0.4f -> Color(0xFFFFD700) // 黄色，中等
                else -> Color(0xFFFF6347) // 红色，低分
            }

            // 类别名称、进度条和分数的水平布局
            Row(
                modifier = Modifier
                    .fillMaxWidth() // 占满宽度
                    .padding(vertical = 4.dp), // 上下 padding 4dp，确保对齐
                verticalAlignment = Alignment.CenterVertically // 垂直居中
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyMedium, // 中等正文样式
                    modifier = Modifier.width(150.dp) // 固定宽度，确保对齐
                )
                LinearProgressIndicator(
                    progress = (category.score / category.maxScore).coerceIn(0f, 1f), // 进度值限制在 0-1
                    modifier = Modifier
                        .weight(1f) // 占用剩余空间
                        .height(8.dp), // 进度条高度 8dp
                    color = scoreColor // 根据得分设置颜色
                )
                Spacer(modifier = Modifier.width(8.dp)) // 添加 8dp 水平间距
                Text(
                    text = "${category.score.format(2)}/${category.maxScore.toInt()}", // 显示分数，保留两位小数
                    style = MaterialTheme.typography.bodySmall, // 小正文样式
                    modifier = Modifier.width(60.dp) // 固定宽度，确保对齐
                )
            }
        }

        // 添加 40dp 的垂直间距，分隔类别和总分
        Spacer(modifier = Modifier.height(40.dp))

        // 总分标题，左对齐
        Text(
            text = "Total Food Quality Score",
            style = MaterialTheme.typography.titleLarge, // 大标题样式
            fontWeight = FontWeight.Bold, // 加粗
            modifier = Modifier.align(Alignment.Start) // 左对齐
        )

        // 添加 8dp 的垂直间距
        Spacer(modifier = Modifier.height(8.dp))

        // 总分进度条和数值
        Row(
            modifier = Modifier
                .fillMaxWidth() // 占满宽度
                .padding(vertical = 4.dp), // 上下 padding 4dp
            verticalAlignment = Alignment.CenterVertically // 垂直居中
        ) {
            LinearProgressIndicator(
                progress = (totalScore / maxTotalScore).coerceIn(0f, 1f), // 总分进度，限制在 0-1
                modifier = Modifier
                    .weight(1f) // 占用剩余空间
                    .height(12.dp), // 进度条高度 12dp
                color = Color(0xFF006400) // 固定为深绿色
            )
            Spacer(modifier = Modifier.width(8.dp)) // 添加 8dp 水平间距
            Text(
                text = "${totalScore.format(2)}/$maxTotalScore", // 显示总分，保留两位小数
                style = MaterialTheme.typography.bodySmall, // 小正文样式
                modifier = Modifier.width(60.dp) // 固定宽度
            )
        }

        // 添加 24dp 的垂直间距
        Spacer(modifier = Modifier.height(24.dp))

        // 分享按钮，启动分享 Intent
        Button(onClick = {
            val shareMessage = "Hi, I just got a Food Quality Score of ${totalScore.format(2)}/$maxTotalScore!"
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareMessage) // 设置分享文本
                type = "text/plain" // 文本类型
            }
            // 启动分享选择器
            context.startActivity(Intent.createChooser(shareIntent, "Share your Food Quality Score"))
        }) {
            Text("Share")
        }

        // 添加 8dp 的垂直间距
        Spacer(modifier = Modifier.height(8.dp))

        // 改善饮食按钮，导航到 NutriCoach 页面
        Button(onClick = { navController.navigate("nutricoach") }) {
            Text("Improve My Diet!")
        }
    }
}

// 扩展函数：将浮点数格式化为指定小数位数的字符串
fun Float.format(digits: Int) = "%.${digits}f".format(this)