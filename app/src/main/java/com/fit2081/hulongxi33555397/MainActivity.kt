package com.fit2081.hulongxi33555397

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// 主活动类，负责设置应用的 UI 和导航结构
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 创建导航控制器，用于管理页面跳转
            val navController = rememberNavController()
            // 获取当前上下文，用于访问 SharedPreferences
            val context = LocalContext.current
            // 使用 SharedPreferences 存储用户数据和登录状态
            val prefs = context.getSharedPreferences("NutriTrackPrefs", MODE_PRIVATE)
            // 检查用户是否已登录，默认为 false
            val isLoggedIn = prefs.getBoolean("is_logged_in", false)

            // 动态确定应用的起始页面
            val startDestination = if (isLoggedIn) {
                // 获取用户 ID，默认为 "Unknown"
                val userId = prefs.getString("user_id", "Unknown") ?: "Unknown"
                // 检查用户是否已填写问卷数据（以用户特定的 categories 键检查）
                if (prefs.getString("${userId}_categories", null) != null) {
                    "home" // 如果已有问卷数据，启动时进入 HomeScreen
                } else {
                    "questionnaire" // 如果没有问卷数据，进入 QuestionnaireScreen
                }
            } else {
                "welcome" // 未登录时，进入 WelcomeScreen
            }

            // 使用 MaterialTheme 提供一致的主题样式
            MaterialTheme {
                // Surface 填充整个屏幕，提供背景
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 获取当前导航路由，用于动态显示导航栏
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route ?: ""

                    // 判断是否应显示底部导航栏
                    // 仅在非 welcome、login 和 questionnaire 页面显示
                    val shouldShowBottomBar = !currentRoute.startsWith("welcome") &&
                            !currentRoute.startsWith("login") &&
                            !currentRoute.startsWith("questionnaire")

                    // 使用 Scaffold 管理应用的布局结构，包括底部导航栏
                    Scaffold(
                        bottomBar = {
                            if (shouldShowBottomBar) {
                                BottomNavigationBar(navController) // 显示导航栏
                            }
                            // 未登录或特定页面时，bottomBar 为空，不显示导航栏
                        }
                    ) { paddingValues ->
                        // NavHost 定义导航图，管理所有页面路由
                        NavHost(
                            navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier.padding(paddingValues) // 应用 Scaffold 的内边距，避免内容被导航栏遮挡
                        ) {
                            // 定义欢迎页面路由
                            composable("welcome") { WelcomeScreen(navController) }
                            // 定义登录页面路由
                            composable("login") { LoginScreen(navController) }
                            // 定义首页路由
                            composable("home") { HomeScreen(navController) }
                            // 定义问卷页面路由，支持编辑模式参数
                            composable("questionnaire?isEdit={isEdit}") { backStackEntry ->
                                QuestionnaireScreen(
                                    navController,
                                    isEdit = backStackEntry.arguments?.getString("isEdit")?.toBoolean() ?: false
                                )
                            }
                            // 定义洞察页面路由
                            composable("insights") { InsightsScreen(navController) }
                            // 定义营养教练页面路由
                            composable("nutricoach") { NutriCoachScreen(navController) }
                            // 定义设置页面路由
                            composable("setting") { SettingScreen(navController) }
                        }
                    }
                }
            }
        }
    }
}

// 定义底部导航栏的 Composable 函数
@Composable
fun BottomNavigationBar(navController: NavController) {
    // 获取当前导航路由，用于高亮选中项
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // NavigationBar 是 Material 3 的底部导航栏组件
    NavigationBar {
        // Home 导航项
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") }, // 首页图标
            label = { Text("Home") }, // 标签文本
            selected = currentRoute == "home", // 当前路由为 "home" 时高亮
            onClick = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true } // 导航到 Home 并清空栈至 Home
                }
            }
        )
        // Insights 导航项
        NavigationBarItem(
            icon = { Icon(Icons.Default.Insights, contentDescription = "Insights") }, // 洞察图标
            label = { Text("Insights") },
            selected = currentRoute == "insights",
            onClick = { navController.navigate("insights") } // 导航到 Insights
        )
        // NutriCoach 导航项
        NavigationBarItem(
            icon = { Icon(Icons.Default.FitnessCenter, contentDescription = "NutriCoach") }, // 营养教练图标
            label = { Text("NutriCoach") },
            selected = currentRoute == "nutricoach",
            onClick = { navController.navigate("nutricoach") } // 导航到 NutriCoach
        )
        // Setting 导航项
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Setting") }, // 设置图标
            label = { Text("Setting") },
            selected = currentRoute == "setting",
            onClick = { navController.navigate("setting") } // 导航到 Setting
        )
    }
}