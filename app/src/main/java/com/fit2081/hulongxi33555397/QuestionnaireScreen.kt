package com.fit2081.hulongxi33555397

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

// 定义人格数据结构体，包含名称和描述
data class Persona(val name: String, val description: String)

// QuestionnaireScreen 是食物摄入问卷页面，支持首次填写和编辑模式
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireScreen(navController: NavController, isEdit: Boolean = false) {
    // 获取当前上下文，用于访问 SharedPreferences 和资源
    val context = LocalContext.current
    // 使用 SharedPreferences 存储用户数据
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)
    // 获取用户 ID，默认为 "Unknown"
    val userId = prefs.getString("user_id", "Unknown") ?: "Unknown"

    // 定义可选的食物类别列表
    val foodCategories = listOf("Fruits", "Vegetables", "Grains", "Red Meat", "Seafood", "Poultry", "Fish", "Nuts/Seeds", "Eggs")
    // 使用 remember 创建可变状态列表，存储选中的食物类别
    val selectedCategories = remember { mutableStateListOf<String>() }

    // 定义人格选项列表
    val personas = listOf(
        Persona("Health Devotee", "I'm passionate about healthy eating & health plays a big part in my life. I use social media to follow active lifestyle personalities or get new recipes/exercise ideas. I may even buy superfoods or follow a particular type of diet. I like to think I am super healthy."),
        Persona("Mindful Eater", "I'm health-conscious and being healthy and eating healthy is important to me. Although health means different things to different people, I make conscious lifestyle decisions about eating based on what I believe healthy means. I look for new recipes and healthy eating information on social media."),
        Persona("Wellness Striver", "I aspire to be healthy (but struggle sometimes). Healthy eating is hard work! I've tried to improve my diet, but always find things that make it difficult to stick with the changes. Sometimes I notice recipe ideas or healthy eating hacks, and if it seems easy enough, I'll give it a go."),
        Persona("Balance Seeker", "I try and live a balanced lifestyle, and I think that all foods are okay in moderation. I shouldn't have to feel guilty about eating a piece of cake now and again. I get all sorts of inspiration from social media like finding out about new restaurants, fun recipes and sometimes healthy eating tips."),
        Persona("Health Procrastinator", "I'm contemplating healthy eating but it's not a priority for me right now. I know the basics about what it means to be healthy, but it doesn't seem relevant to me right now. I have taken a few steps to be healthier but I am not motivated to make it a high priority because I have too many other things going on in my life."),
        Persona("Food Carefree", "I'm not bothered about healthy eating. I don't really see the point and I don't think about it. I don't really notice healthy eating tips or recipes and I don't care what I eat.")
    )

    // 使用 remember 保存选中人格的状态
    var selectedPersona by remember { mutableStateOf<String?>(null) }
    // 使用 remember 保存模态框显示状态
    var showModal by remember { mutableStateOf(false) }
    // 使用 remember 保存当前查看的人格
    var currentPersona by remember { mutableStateOf<Persona?>(null) }

    // 定义时间相关问题列表
    val timeQuestions = listOf(
        "What time of day approx. do you normally eat your biggest meal?",
        "What time of day approx. do you go to sleep at night?",
        "What time of day approx. do you wake up in the morning?"
    )
    // 使用 remember 创建可变状态列表，存储选中的时间
    val selectedTimes = remember { mutableStateListOf("", "", "") }
    // 为每个时间问题创建时间选择器状态，初始值为 12:00
    val timePickerStates = List(3) { rememberTimePickerState(initialHour = 12, initialMinute = 0) }
    // 使用 remember 保存时间选择器显示状态，-1 表示不显示
    var showTimePicker by remember { mutableStateOf(-1) }

    // 打印调试信息，显示是否为编辑模式
    println("QuestionnaireScreen: isEdit=$isEdit")

    // 如果是编辑模式，加载已有数据
    if (isEdit) {
        LaunchedEffect(Unit) {
            selectedCategories.clear()
            // 从 SharedPreferences 加载已有食物类别
            selectedCategories.addAll(prefs.getString("${userId}_categories", "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList())
            // 加载已有选中人格
            selectedPersona = prefs.getString("${userId}_persona", null)
            // 加载已有时间数据
            selectedTimes[0] = prefs.getString("${userId}_biggest_meal_time", "") ?: ""
            selectedTimes[1] = prefs.getString("${userId}_sleep_time", "") ?: ""
            selectedTimes[2] = prefs.getString("${userId}_wake_time", "") ?: ""
        }
    } else {
        // 如果不是编辑模式且已有数据，跳转到首页
        LaunchedEffect(Unit) {
            if (prefs.getString("${userId}_categories", null) != null) {
                println("Existing data found, navigating to home")
                navController.navigate("home")
            }
        }
    }

    // 使用 Scaffold 管理页面布局，包含顶部栏
    Scaffold(
        topBar = {
            // 顶部栏，显示标题和返回按钮
            TopAppBar(
                title = { Text("Food Intake Questionnaire") }, // 标题
                navigationIcon = {
                    // 返回按钮，点击后导航到首页
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back" // 无障碍描述
                        )
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0) // 无额外边距
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0) // 内容区域无额外边距
    ) { innerPadding ->
        // 主内容区域，使用 Column 垂直排列
        Column(
            modifier = Modifier
                .fillMaxSize() // 填充整个屏幕
                .padding(innerPadding) // 适配 Scaffold 的内边距
                .padding(horizontal = 16.dp) // 左右 padding 16dp
                .verticalScroll(rememberScrollState()), // 启用垂直滚动
            horizontalAlignment = Alignment.CenterHorizontally // 内容水平居中
        ) {
            // 食物类别标题
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start // 左对齐
            ) {
                Text(
                    text = "Tick all the food categories you can eat",
                    style = MaterialTheme.typography.bodyLarge, // 大正文样式
                    fontWeight = FontWeight.Bold, // 加粗
                    modifier = Modifier.align(Alignment.CenterVertically) // 垂直居中
                )
            }
            Spacer(modifier = Modifier.height(8.dp)) // 添加 8dp 间距

            // 3x3 布局的食物类别选择器
            Column(modifier = Modifier.fillMaxWidth()) {
                for (i in 0 until 3) { // 3 行
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween // 均匀分布
                    ) {
                        // 每行 3 个选项
                        for (j in 0 until 3) {
                            val index = i * 3 + j
                            if (index < foodCategories.size) {
                                val category = foodCategories[index]
                                Column(
                                    modifier = Modifier
                                        .weight(1f) // 平均分配宽度
                                        .padding(4.dp) // 四周 padding 4dp
                                        .selectable(
                                            selected = selectedCategories.contains(category),
                                            onClick = {
                                                // 点击切换选中状态
                                                if (selectedCategories.contains(category)) {
                                                    selectedCategories.remove(category)
                                                } else {
                                                    selectedCategories.add(category)
                                                }
                                            }
                                        )
                                        .padding(4.dp), // 额外 padding
                                    horizontalAlignment = Alignment.CenterHorizontally // 居中
                                ) {
                                    Checkbox(
                                        checked = selectedCategories.contains(category),
                                        onCheckedChange = null // 由 selectable 处理
                                    )
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.bodyMedium, // 中等正文样式
                                        textAlign = TextAlign.Center // 文本居中
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) // 添加 16dp 间距

            // 人格类别标题
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start // 左对齐
            ) {
                Text(
                    text = "Your Persona",
                    style = MaterialTheme.typography.bodyLarge, // 大正文样式
                    fontWeight = FontWeight.Bold, // 加粗
                    modifier = Modifier.align(Alignment.CenterVertically) // 垂直居中
                )
            }
            Spacer(modifier = Modifier.height(8.dp)) // 添加 8dp 间距

            // 人格说明文本
            Text(
                text = "People can be broadly classified into 6 different types based on their eating preferences. " +
                        "Click on each button below to find out the different types, and select the type that best fits you!",
                style = MaterialTheme.typography.bodyMedium, // 中等正文样式
                modifier = Modifier.padding(bottom = 8.dp) // 自定义 padding
            )
            Spacer(modifier = Modifier.height(8.dp)) // 添加 8dp 间距

            // 3x2 布局的人格按钮
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp) // 每行间距 8dp
            ) {
                for (i in 0 until 3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // 每列间距 8dp
                    ) {
                        val persona1 = personas[i * 2]
                        val persona2 = personas[i * 2 + 1]
                        PersonaButton(
                            persona = persona1,
                            isSelected = selectedPersona == persona1.name,
                            onClick = {
                                currentPersona = persona1 // 设置当前查看的人格
                                showModal = true // 显示模态框
                            },
                            modifier = Modifier.weight(1f) // 平均分配宽度
                        )
                        PersonaButton(
                            persona = persona2,
                            isSelected = selectedPersona == persona2.name,
                            onClick = {
                                currentPersona = persona2
                                showModal = true
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp)) // 添加 16dp 间距

            // 时间问题标题
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start // 左对齐
            ) {
                Text(
                    text = "Timing",
                    style = MaterialTheme.typography.bodyLarge, // 大正文样式
                    fontWeight = FontWeight.Bold, // 加粗
                    modifier = Modifier.align(Alignment.CenterVertically) // 垂直居中
                )
            }
            Spacer(modifier = Modifier.height(8.dp)) // 添加 8dp 间距

            // 显示时间问题和选择按钮
            timeQuestions.forEachIndexed { index, question ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp), // 上下 padding 4dp
                    verticalAlignment = Alignment.CenterVertically // 垂直居中
                ) {
                    Text(
                        text = question,
                        style = MaterialTheme.typography.bodyMedium, // 中等正文样式
                        modifier = Modifier
                            .weight(1f) // 占用剩余空间
                            .padding(end = 8.dp) // 右 padding 8dp
                    )
                    Button(
                        onClick = { showTimePicker = index }, // 显示对应时间选择器
                        modifier = Modifier
                            .width(100.dp) // 固定宽度 100dp
                    ) {
                        Text(
                            text = selectedTimes[index].ifEmpty { "Select" }, // 显示选中时间或提示
                            textAlign = TextAlign.Center // 文本居中
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) // 添加 16dp 间距

            // 提交按钮
            Button(onClick = {
                // 打印调试信息
                println("Submit clicked: categories=$selectedCategories, persona=$selectedPersona, times=$selectedTimes")
                // 检查所有字段是否已填写
                if (selectedCategories.isNotEmpty() && selectedPersona != null && selectedTimes.all { it.isNotEmpty() }) {
                    with(prefs.edit()) {
                        // 保存用户选择的食物类别、人格和时间
                        putString("${userId}_categories", selectedCategories.joinToString(","))
                        putString("${userId}_persona", selectedPersona)
                        putString("${userId}_biggest_meal_time", selectedTimes[0])
                        putString("${userId}_sleep_time", selectedTimes[1])
                        putString("${userId}_wake_time", selectedTimes[2])
                        apply()
                    }
                    println("Navigating to home from Submit")
                    // 导航到首页
                    navController.navigate("home")
                } else {
                    // 如果填写不完整，打印失败原因
                    println("Submit failed: categories=${selectedCategories.isEmpty()}, persona=$selectedPersona, times=${selectedTimes.any { it.isEmpty() }}")
                }
            }) {
                Text("Submit")
            }
        }
    }

    // 显示时间选择器对话框
    if (showTimePicker >= 0) {
        AlertDialog(
            onDismissRequest = { showTimePicker = -1 }, // 点击外部关闭
            title = { Text(timeQuestions[showTimePicker]) }, // 显示对应问题
            text = {
                // 时间选择器组件
                TimePicker(state = timePickerStates[showTimePicker])
            },
            confirmButton = {
                TextButton(onClick = {
                    // 格式化选中时间为 HH:MM
                    val hour = timePickerStates[showTimePicker].hour.toString().padStart(2, '0')
                    val minute = timePickerStates[showTimePicker].minute.toString().padStart(2, '0')
                    selectedTimes[showTimePicker] = "$hour:$minute"
                    showTimePicker = -1 // 关闭对话框
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = -1 }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 显示人格详情模态框
    if (showModal && currentPersona != null) {
        AlertDialog(
            onDismissRequest = { showModal = false }, // 点击外部关闭
            title = { Text(currentPersona!!.name) }, // 显示人格名称
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // 根据人格名称动态加载图片
                    val imageName = currentPersona!!.name.lowercase().replace(" ", "")
                    val resourceId = context.resources.getIdentifier(
                        imageName, "drawable", context.packageName
                    )

                    Image(
                        painter = painterResource(id = resourceId),
                        contentDescription = currentPersona!!.name, // 无障碍描述
                        modifier = Modifier
                            .size(100.dp) // 图片大小 100dp
                            .border(1.dp, MaterialTheme.colorScheme.onSurface) // 添加边框
                    )

                    Spacer(modifier = Modifier.height(8.dp)) // 添加 8dp 间距
                    Text(currentPersona!!.description, textAlign = TextAlign.Center) // 显示人格描述
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedPersona = currentPersona!!.name // 选中人格
                        showModal = false // 关闭对话框
                    }
                ) {
                    Text("Select")
                }
            },
            dismissButton = {
                TextButton(onClick = { showModal = false }) {
                    Text("Dismiss")
                }
            }
        )
    }
}

// PersonaButton 是自定义组件，用于显示人格按钮并支持选中状态
@Composable
fun PersonaButton(
    persona: Persona,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 定义椭圆形状的按钮样式
    val buttonShape = RoundedCornerShape(50)

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // 如果选中，显示外层边框
        if (isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize() // 与按钮同大小
                    .padding(1.dp) // 边框向外延伸
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary, // 主色边框
                        shape = buttonShape
                    )
            )
        }

        // 内层按钮
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(1.dp), // 缩小以显示边框
            shape = buttonShape // 椭圆形状
        ) {
            Text(persona.name) // 显示人格名称
        }
    }
}