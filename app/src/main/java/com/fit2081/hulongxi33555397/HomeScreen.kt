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

// HomeScreen 是应用的主页面，展示用户信息、饮食分数和退出功能
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreen(navController: NavController) {
    // 获取当前上下文，用于访问 SharedPreferences 和资源
    val context = LocalContext.current
    // 使用 SharedPreferences 存储用户数据和登录状态
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)

    // 获取用户 ID，默认为 "Unknown"
    val userId = prefs.getString("user_id", "Unknown") ?: "Unknown"
    // 从 CSV 文件加载用户数据
    val userData = loadUserDataFromCsv(context, userId)
    // 获取用户选择的食物类别，以逗号分隔的字符串形式存储
    val selectedCategories = prefs.getString("${userId}_categories", "")?.split(",") ?: emptyList()

    // 根据用户性别判断是否为男性
    val isMale = userData?.sex == "Male"
    // 根据性别选择对应的 HEIFA 分数，未找到数据时默认为 0f
    val totalScore = if (isMale) userData?.heifaTotalScoreMale ?: 0f else userData?.heifaTotalScoreFemale ?: 0f
    // 分数的最大值固定为 100
    val maxTotalScore = 100f

    // 使用 Scaffold 管理页面布局，顶部栏留空
    Scaffold(
        topBar = { } // 未使用顶部栏，留空
    ) { padding ->
        // 主内容区域，使用 Column 垂直排列
        Column(
            modifier = Modifier
                .fillMaxSize() // 填充整个屏幕
                .padding(horizontal = 16.dp) // 左右 padding 16dp
                .padding(
                    top = 16.dp, // 顶部 padding 16dp
                    bottom = padding.calculateBottomPadding() // 底部 padding 适配导航栏
                )
                .verticalScroll(rememberScrollState()), // 启用垂直滚动
            horizontalAlignment = Alignment.Start // 内容左对齐
        ) {
            // 显示灰色问候语 "Hello,"
            Text(
                text = "Hello,",
                style = MaterialTheme.typography.titleLarge, // 使用较大的标题样式
                color = Color.Gray // 灰色字体
            )

            // 显示用户 ID，加粗且使用大标题样式
            Text(
                text = userId,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            // 添加 8dp 的垂直间距
            Spacer(modifier = Modifier.height(8.dp))

            // 提示文本和 Edit 按钮的水平布局
            Row(
                modifier = Modifier.fillMaxWidth(), // 占满宽度
                horizontalArrangement = Arrangement.SpaceBetween, // 两端对齐
                verticalAlignment = Alignment.CenterVertically // 垂直居中
            ) {
                Text(
                    text = "You've already filled in your Food Intake Questionnaire, but you can change details here:",
                    style = MaterialTheme.typography.bodyMedium, // 中等正文样式
                    modifier = Modifier.weight(1f) // 占用剩余空间
                )

                // 添加 8dp 的水平间距
                Spacer(modifier = Modifier.width(8.dp))

                // 编辑按钮，点击后导航到问卷编辑页面
                Button(onClick = { navController.navigate("questionnaire?isEdit=true") }) {
                    Text("Edit")
                }
            }

            // 添加 24dp 的垂直间距
            Spacer(modifier = Modifier.height(24.dp))

            // 显示健康饮食信息图
            Image(
                painter = painterResource(id = R.drawable.homescreen_picture), // 从资源加载图片
                contentDescription = "Healthy eating infographic", // 无障碍描述
                modifier = Modifier
                    .fillMaxWidth() // 占满宽度
                    .aspectRatio(1f), // 保持 1:1 宽高比
                contentScale = ContentScale.Fit // 完整显示图片，不裁剪
            )

            // 添加 24dp 的垂直间距
            Spacer(modifier = Modifier.height(24.dp))

            // "My Score" 标题和 "See all scores" 按钮的水平布局
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Score",
                    style = MaterialTheme.typography.headlineMedium, // 大标题样式
                    fontWeight = FontWeight.Bold // 加粗
                )
                // 查看详细分数的按钮，点击后导航到 Insights 页面
                TextButton(onClick = { navController.navigate("insights") }) {
                    Text("See all scores >")
                }
            }

            // 添加 8dp 的垂直间距
            Spacer(modifier = Modifier.height(8.dp))

            // 显示食物质量分数
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Food Quality score",
                    style = MaterialTheme.typography.bodyLarge // 大正文样式
                )
                Text(
                    text = "${totalScore.format(2)}/100", // 显示分数，保留两位小数
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF006400) // 使用深绿色
                )
            }

            // 添加 36dp 的垂直间距
            Spacer(modifier = Modifier.height(36.dp))

            // 添加灰色分隔线
            Box(
                modifier = Modifier
                    .fillMaxWidth() // 占满宽度
                    .height(1.dp) // 高度 1dp
                    .background(Color.Gray) // 灰色背景
            )

            // 添加 36dp 的垂直间距
            Spacer(modifier = Modifier.height(36.dp))

            // "What is the Food Quality Score?" 标题
            Text(
                text = "What is the Food Quality Score?",
                style = MaterialTheme.typography.headlineSmall, // 小标题样式
                fontWeight = FontWeight.Bold // 加粗
            )

            // 添加 16dp 的垂直间距
            Spacer(modifier = Modifier.height(16.dp))

            // 食物质量分数说明文本1
            Text(
                text = "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet.",
                style = MaterialTheme.typography.bodyMedium // 中等正文样式
            )

            // 添加 16dp 的垂直间距
            Spacer(modifier = Modifier.height(16.dp))

            // 食物质量分数说明文本2
            Text(
                text = "This personalized measurement considers various food groups including vegetables, fruits, whole grains, and proteins to give you practical insights for making healthier food choices.",
                style = MaterialTheme.typography.bodyMedium // 中等正文样式
            )

            // 添加 16dp 的垂直间距
            Spacer(modifier = Modifier.height(16.dp))

            // 退出登录按钮
            Button(
                onClick = {
                    with(prefs.edit()) {
                        // 仅移除登录状态，保留其他数据（如问卷）
                        remove("is_logged_in")
                        apply()
                    }
                    // 导航到欢迎页面，清空导航栈
                    navController.navigate("welcome") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally) // 按钮居中
            ) {
                Text("Logout")
            }

            // 添加 16dp 的垂直间距，确保底部留白
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}