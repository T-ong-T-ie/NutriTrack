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
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import com.fit2081.hulongxi33555397.db.NutritrackRepository
import com.fit2081.hulongxi33555397.db.Patient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { NutritrackRepository(context) }

    // Login status management
    var userId by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isFirstLogin by remember { mutableStateOf(false) }
    var isPasswordLogin by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    // 密码可见性控制
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when {
                            isFirstLogin -> "首次登录 - 认领账户"
                            isPasswordLogin -> "密码登录"
                            else -> "登录"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        when {
                            isFirstLogin -> {
                                // 从首次登录返回到ID输入
                                isFirstLogin = false
                                phoneNumber = ""
                                name = ""
                                password = ""
                                confirmPassword = ""
                                errorMessage = ""
                            }
                            isPasswordLogin -> {
                                // 从密码登录返回到ID输入
                                isPasswordLogin = false
                                password = ""
                                errorMessage = ""
                            }
                            else -> {
                                // 返回欢迎页面
                                navController.navigateUp()
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // 不同的登录阶段显示不同的输入界面
            when {
                // 首次登录界面
                isFirstLogin -> {
                    // 用户ID (只读)
                    OutlinedTextField(
                        value = userId,
                        onValueChange = { },
                        label = { Text("用户ID") },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 手机号码
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("手机号码") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 姓名
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("姓名") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 密码
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("设置密码") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "切换密码可见性"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 确认密码
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("确认密码") },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "切换密码可见性"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorMessage = "请输入姓名"
                                return@Button
                            }

                            if (password.length < 6) {
                                errorMessage = "密码长度至少为6位"
                                return@Button
                            }

                            if (password != confirmPassword) {
                                errorMessage = "两次输入的密码不一致"
                                return@Button
                            }

                            // 添加电话号码格式检查
                            if (!isValidPhoneNumber(phoneNumber)) {
                                errorMessage = "请输入有效的手机号码"
                                return@Button
                            }

                            isProcessing = true
                            errorMessage = ""

                            // 验证账户并设置密码
                            coroutineScope.launch {
                                try {
                                    val patient = repository.getPatientById(userId)

                                    if (patient != null) {
                                        // 检查手机号是否匹配
                                        if (patient.phoneNumber == phoneNumber) {
                                            // 更新用户信息
                                            patient.name = name
                                            patient.password = password
                                            repository.updatePatient(patient)

                                            // 保存登录状态
                                            with(prefs.edit()) {
                                                putBoolean("is_logged_in", true)
                                                putString("user_id", userId)
                                                apply()
                                            }

                                            // 导航到问卷页面
                                            navController.navigate("questionnaire") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        } else {
                                            errorMessage = "手机号码不匹配，请重试"
                                        }
                                    } else {
                                        errorMessage = "找不到用户ID，请检查输入"
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "发生错误: ${e.message}"
                                } finally {
                                    isProcessing = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("完成注册")
                        }
                    }
                }

                // 密码登录界面
                isPasswordLogin -> {
                    // 用户ID (只读)
                    OutlinedTextField(
                        value = userId,
                        onValueChange = { },
                        label = { Text("用户ID") },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 密码
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("密码") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "切换密码可见性"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (password.isBlank()) {
                                errorMessage = "请输入密码"
                                return@Button
                            }

                            isProcessing = true
                            errorMessage = ""

                            // 验证密码
                            coroutineScope.launch {
                                try {
                                    val result = repository.authenticateUser(userId, password)

                                    if (result != null) {
                                        // 保存登录状态
                                        with(prefs.edit()) {
                                            putBoolean("is_logged_in", true)
                                            putString("user_id", userId)
                                            apply()
                                        }

                                        // 检查是否已经填写过问卷
                                        val foodIntake = repository.getLatestFoodIntake(userId)

                                        if (foodIntake != null) {
                                            // 已填写问卷，导航到主页
                                            navController.navigate("home") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        } else {
                                            // 未填写问卷，导航到问卷页面
                                            navController.navigate("questionnaire") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                    } else {
                                        errorMessage = "密码错误，请重试"
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "登录时发生错误: ${e.message}"
                                } finally {
                                    isProcessing = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("登录")
                        }
                    }
                }

                // 初始登录界面 - 输入用户ID
                else -> {
                    OutlinedTextField(
                        value = userId,
                        onValueChange = { userId = it },
                        label = { Text("用户ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            if (userId.isBlank()) {
                                errorMessage = "请输入用户ID"
                                return@Button
                            }

                            isProcessing = true
                            errorMessage = ""

                            coroutineScope.launch {
                                try {
                                    val patient = repository.getPatientById(userId)

                                    if (patient != null) {
                                        // 找到用户，检查是否已设置密码
                                        if (patient.password.isNullOrBlank()) {
                                            // 用户存在但未设置密码，进入首次登录流程
                                            isFirstLogin = true
                                        } else {
                                            // 用户已设置密码，进入密码登录流程
                                            isPasswordLogin = true
                                        }
                                    } else {
                                        errorMessage = "用户ID不存在，请检查输入"
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "检查用户时发生错误: ${e.message}"
                                } finally {
                                    isProcessing = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("下一步")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// 简单的手机号码验证函数
private fun isValidPhoneNumber(phone: String): Boolean {
    // 简单检查：10-11位数字
    val phoneRegex = Regex("^\\d{10,11}$")
    return phoneRegex.matches(phone)
}