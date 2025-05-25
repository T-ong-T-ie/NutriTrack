package com.fit2081.hulongxi33555397

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.fit2081.hulongxi33555397.db.NutriCoachTip
import com.fit2081.hulongxi33555397.db.NutritrackRepository
import com.fit2081.hulongxi33555397.db.Patient
// 修正 BuildConfig 的导入
import com.fit2081.hulongxi33555397.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel

// Data classes for FruityVice API response (保持不变)
data class FruitNutrition(
    val carbohydrates: Double?,
    val protein: Double?,
    val fat: Double?,
    val calories: Double?,
    val sugar: Double?
)

data class FruitData(
    val genus: String?,
    val name: String?,
    val id: Int?,
    val family: String?,
    val order: String?,
    val nutritions: FruitNutrition?
)

// Retrofit API Service interface (保持不变)
interface FruityViceApiService {
    @GET("api/fruit/{name}")
    suspend fun getFruitByName(@Path("name") fruitName: String): retrofit2.Response<FruitData>
}

// Simplified Retrofit Client (保持不变)
object ApiClient {
    private const val BASE_URL = "https://www.fruityvice.com/"

    val fruityViceApi: FruityViceApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FruityViceApiService::class.java)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutriCoachScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)
    val userId = prefs.getString("user_id", "Unknown") ?: "Unknown"
    val repository = remember { NutritrackRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    // 状态变量
    var fruitNameQuery by remember { mutableStateOf("") }
    var fruitDetails by remember { mutableStateOf<FruitData?>(null) }
    var isLoadingFruit by remember { mutableStateOf(false) }
    var fruitError by remember { mutableStateOf<String?>(null) }

    var genAIResponse by remember { mutableStateOf("") }
    var isLoadingGenAI by remember { mutableStateOf(false) }
    var showTipsHistory by remember { mutableStateOf(false) }
    var savedTips by remember { mutableStateOf<List<NutriCoachTip>>(emptyList()) }

    // 获取用户水果得分
    var patientData by remember { mutableStateOf<Patient?>(null) }
    var fruitScore by remember { mutableStateOf(0f) }
    var isOptimalFruitScore by remember { mutableStateOf(false) }

    // 初始化 Gemini 模型
    val generativeModel = remember {
        GenerativeModel(
            modelName = "gemini-pro",
            apiKey = BuildConfig.GEMINI_API_KEY // 使用正确的 BuildConfig
        )
    }

    // 前向声明函数，以便在 LaunchedEffect 中使用
    lateinit var generateAIResponse: () -> Unit
    lateinit var loadSavedTips: () -> Unit

    LaunchedEffect(userId) {
        coroutineScope.launch {
            try {
                patientData = repository.getPatientById(userId)
                if (patientData != null) {
                    // 假设 Patient 类使用单一 fruitScore 字段
                    fruitScore = patientData?.fruitScore ?: 0f
                    isOptimalFruitScore = fruitScore >= 5f // 假设理想分数为5分或更高
                }
                if (::loadSavedTips.isInitialized) { // 确保函数已初始化
                    loadSavedTips()
                }
                if (::generateAIResponse.isInitialized) { // 确保函数已初始化
                    generateAIResponse() // 初始加载时生成AI建议
                }
            } catch (e: Exception) {
                fruitError = "加载初始数据失败: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    // 水果搜索功能
    fun searchFruit() {
        if (fruitNameQuery.isBlank()) {
            fruitError = "请输入水果名称"
            return
        }
        coroutineScope.launch {
            isLoadingFruit = true
            fruitDetails = null
            fruitError = null
            try {
                val response = ApiClient.fruityViceApi.getFruitByName(fruitNameQuery)
                if (response.isSuccessful) {
                    fruitDetails = response.body()
                    if (fruitDetails == null) {
                        fruitError = "未找到水果信息"
                    } else {
                        // 成功获取水果信息后，生成AI建议
                        if (::generateAIResponse.isInitialized) {
                            generateAIResponse()
                        }
                    }
                } else {
                    fruitError = "搜索水果失败: ${response.message()}"
                }
            } catch (e: Exception) {
                fruitError = "搜索水果时发生错误: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoadingFruit = false
            }
        }
    }

    // 将函数定义移到 LaunchedEffect 之后，并进行初始化赋值
    generateAIResponse = {
        isLoadingGenAI = true
        genAIResponse = ""
        val currentFruitDetails = fruitDetails // 捕获当前状态

        val prompt = if (currentFruitDetails != null) {
            "请作为一名营养师，根据以下水果数据提供健康建议：" +
                    "水果名称：${currentFruitDetails.name}，" +
                    "碳水化合物：${currentFruitDetails.nutritions?.carbohydrates}g，" +
                    "蛋白质：${currentFruitDetails.nutritions?.protein}g，" +
                    "脂肪：${currentFruitDetails.nutritions?.fat}g，" +
                    "卡路里：${currentFruitDetails.nutritions?.calories}，" +
                    "糖分：${currentFruitDetails.nutritions?.sugar}g。" +
                    "提供5条简短的中文健康饮食建议。"
        } else {
            "请作为一名营养师，给出5条简短的水果健康饮食建议，使用中文回答。"
        }

        coroutineScope.launch {
            try {
                val response = generativeModel.generateContent(prompt)
                genAIResponse = response.text ?: "无法生成回复"

                if (genAIResponse.isNotBlank()) {
                    val tip = NutriCoachTip(
                        userId = userId,
                        content = genAIResponse,
                        category = if (currentFruitDetails != null) "水果分析: ${currentFruitDetails.name}" else "健康建议",
                        timestamp = System.currentTimeMillis()
                        // imageUrl 字段可以根据需要添加
                    )
                    repository.saveTip(tip) // 确保 repository.saveTip 已实现
                    if (::loadSavedTips.isInitialized) {
                        loadSavedTips() // 保存后刷新历史记录
                    }
                }
            } catch (e: Exception) {
                genAIResponse = "生成回复时发生错误: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoadingGenAI = false
            }
        }
    }

    loadSavedTips = {
        coroutineScope.launch {
            try {
                savedTips = repository.getUserTips(userId) // 确保 repository.getUserTips 已实现
            } catch (e: Exception) {
                // 可以考虑在这里显示错误信息给用户
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NutriCoach") },
                actions = {
                    IconButton(onClick = { if (::generateAIResponse.isInitialized) generateAIResponse() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "刷新建议")
                    }
                    IconButton(onClick = {
                        if (::loadSavedTips.isInitialized) loadSavedTips()
                        showTipsHistory = true
                    }) {
                        Icon(Icons.Filled.History, contentDescription = "历史记录")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 上半部分: 水果查询 或 图片 (根据 fruitScore)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // 占据一半高度
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isOptimalFruitScore) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("您的水果摄入评分理想！", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        AsyncImage(
                            model = "https://picsum.photos/seed/${userId}/400/300", // 示例随机图片
                            contentDescription = "庆祝健康水果摄入",
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .aspectRatio(4f / 3f)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("水果摄入评分：${String.format("%.1f", fruitScore)}/10", style = MaterialTheme.typography.titleMedium)
                        Text("您的水果摄入有待提高，搜索水果了解更多：", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = fruitNameQuery,
                            onValueChange = { fruitNameQuery = it },
                            label = { Text("输入水果名称 (英文)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { searchFruit() }, enabled = !isLoadingFruit) {
                            if (isLoadingFruit) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else {
                                Text("搜索水果")
                            }
                        }
                        fruitError?.let {
                            Text(it, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                        }
                        fruitDetails?.let { details ->
                            Card(modifier = Modifier.padding(top = 8.dp)) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("水果: ${details.name ?: "N/A"}", fontWeight = FontWeight.Bold)
                                    Text("科属: ${details.family ?: "N/A"}, ${details.genus ?: "N/A"}")
                                    details.nutritions?.let { nutr ->
                                        Text("热量: ${nutr.calories ?: 0.0} kcal")
                                        Text("碳水: ${nutr.carbohydrates ?: 0.0}g, 糖: ${nutr.sugar ?: 0.0}g")
                                        Text("蛋白质: ${nutr.protein ?: 0.0}g, 脂肪: ${nutr.fat ?: 0.0}g")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Divider()

            // 下半部分: GenAI 建议
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // 占据一半高度
                    .padding(top = 8.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("NutriCoach AI 营养建议", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isLoadingGenAI) {
                        CircularProgressIndicator()
                    } else {
                        if (genAIResponse.isNotBlank()) {
                            Text(
                                text = genAIResponse,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                textAlign = TextAlign.Justify
                            )
                        } else {
                            Text("点击刷新按钮或搜索水果以获取建议。")
                        }
                    }
                }
            }
        }

        if (showTipsHistory) {
            AlertDialog(
                onDismissRequest = { showTipsHistory = false },
                title = { Text("历史营养贴士") },
                text = {
                    if (savedTips.isEmpty()) {
                        Text("暂无历史记录。")
                    } else {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            savedTips.forEach { tip ->
                                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = tip.category,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(tip.timestamp)),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(tip.content, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showTipsHistory = false }) {
                        Text("关闭")
                    }
                }
            )
        }
    }
}