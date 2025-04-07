package com.fit2081.hulongxi33555397

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.io.BufferedReader
import java.io.InputStreamReader
import android.content.Context
import androidx.compose.material.icons.filled.ArrowBack

// 定义用户数据的结构体，包含用户 ID 和电话号码
data class User(val userId: String, val phoneNumber: String)

// 从 assets 中的 users.csv 文件加载用户列表的函数
@Composable
fun loadUsersFromCsv(): List<User> {
    // 获取当前上下文，用于访问 assets
    val context = LocalContext.current
    // 创建一个可变列表，用于存储用户数据
    val users = mutableListOf<User>()
    try {
        // 打开 assets 目录下的 users.csv 文件
        context.assets.open("users.csv").use { inputStream ->
            // 使用 BufferedReader 读取 CSV 文件内容
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // 跳过标题行，逐行读取数据
                reader.readLines().drop(1).forEach { line ->
                    // 将每行按逗号分隔成列
                    val columns = line.split(",")
                    // 第一列为电话号码，第二列为用户 ID
                    val phoneNumber = columns[0]
                    val userId = columns[1]
                    // 创建 User 对象并添加到列表
                    users.add(User(userId, phoneNumber))
                }
            }
        }
    } catch (e: Exception) {
        // 如果读取文件或解析数据时发生异常，打印堆栈跟踪
        e.printStackTrace()
    }
    // 返回加载的用户列表
    return users
}

// LoginScreen 是登录页面，允许用户选择 ID 并输入电话号码进行验证
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    // 获取当前上下文，用于访问 SharedPreferences
    val context = LocalContext.current
    // 使用 SharedPreferences 存储用户数据和登录状态
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)
    // 加载用户数据列表
    val users = loadUsersFromCsv()
    // 提取所有用户 ID 用于下拉菜单
    val userIds = users.map { it.userId }
    // 使用 remember 保存选中用户 ID 的状态，初始值为提示文本
    var selectedId by remember { mutableStateOf("Select User ID") }
    // 使用 remember 保存输入的电话号码状态
    var phoneNumber by remember { mutableStateOf("") }
    // 使用 remember 保存错误消息状态
    var errorMessage by remember { mutableStateOf("") }

    // 使用 Scaffold 管理页面布局，包含顶部栏
    Scaffold(
        topBar = {
            // 顶部栏，显示标题和返回按钮
            TopAppBar(
                title = { Text("Login") }, // 标题为 "Login"
                navigationIcon = {
                    // 返回按钮，点击后返回上一页面
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back" // 无障碍描述
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        // 主内容区域，使用 Column 垂直排列
        Column(
            modifier = Modifier
                .fillMaxSize() // 填充整个屏幕
                .padding(innerPadding) // 适配 Scaffold 的内边距
                .padding(16.dp), // 额外四周 padding 16dp
            horizontalAlignment = Alignment.CenterHorizontally, // 内容水平居中
            verticalArrangement = Arrangement.Center // 内容垂直居中
        ) {
            // 添加 16dp 的垂直间距
            Spacer(modifier = Modifier.height(16.dp))

            // 使用 remember 保存下拉菜单的展开状态
            var expanded by remember { mutableStateOf(false) }
            // 下拉菜单组件，用于选择用户 ID
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded } // 点击时切换展开状态
            ) {
                // 显示当前选中的用户 ID，只读
                OutlinedTextField(
                    value = selectedId,
                    onValueChange = { }, // 只读，无需处理输入
                    label = { Text("User ID") }, // 字段标签
                    readOnly = true, // 设置为只读
                    modifier = Modifier.menuAnchor() // 作为下拉菜单的锚点
                )
                // 下拉菜单内容
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }, // 点击外部时关闭菜单
                    modifier = Modifier
                        .heightIn(max = 200.dp) // 最大高度 200dp
                        .verticalScroll(rememberScrollState()) // 启用垂直滚动
                ) {
                    // 为每个用户 ID 创建菜单项
                    userIds.forEach { id ->
                        DropdownMenuItem(
                            text = { Text(id) }, // 显示用户 ID
                            onClick = {
                                selectedId = id // 选中后更新状态
                                expanded = false // 关闭菜单
                            }
                        )
                    }
                }
            }

            // 添加 16dp 的垂直间距
            Spacer(modifier = Modifier.height(16.dp))

            // 输入电话号码的文本字段
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it }, // 更新输入值
                label = { Text("Phone Number") } // 字段标签
            )

            // 添加 16dp 的垂直间距
            Spacer(modifier = Modifier.height(16.dp))

            // 如果有错误消息，显示红色文本
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp)) // 添加 8dp 间距
            }

            // 继续按钮，验证用户输入并导航
            Button(onClick = {
                // 查找匹配选中 ID 的用户
                val selectedUser = users.find { it.userId == selectedId }
                // 验证用户 ID 和电话号码是否匹配
                if (selectedUser != null && selectedUser.phoneNumber == phoneNumber) {
                    with(prefs.edit()) {
                        // 保存用户 ID 和登录状态
                        putString("user_id", selectedId)
                        putBoolean("is_logged_in", true)
                        apply()
                    }
                    // 检查是否已有问卷数据
                    val hasQuestionnaireData = prefs.contains("categories")
                    if (hasQuestionnaireData) {
                        // 如果有问卷数据，导航到首页并清空导航栈至欢迎页面
                        navController.navigate("home") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    } else {
                        // 如果无问卷数据，导航到问卷页面
                        navController.navigate("questionnaire")
                    }
                } else {
                    // 如果验证失败，显示错误消息
                    errorMessage = "Invalid User ID or Phone Number"
                }
            }) {
                Text("Continue")
            }
        }
    }
}