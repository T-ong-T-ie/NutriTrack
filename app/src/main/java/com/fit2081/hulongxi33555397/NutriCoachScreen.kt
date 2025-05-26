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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
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
import com.google.ai.client.generativeai.GenerativeModel

// Data classes for FruityVice API response
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

// Retrofit API Service interface
interface FruityViceApiService {
    @GET("api/fruit/{name}")
    suspend fun getFruitByName(@Path("name") fruitName: String): retrofit2.Response<FruitData>
}

// Simplified Retrofit Client
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

    // State variables
    var fruitNameQuery by remember { mutableStateOf("") }
    var fruitDetails by remember { mutableStateOf<FruitData?>(null) }
    var isLoadingFruit by remember { mutableStateOf(false) }
    var fruitError by remember { mutableStateOf<String?>(null) }

    var genAIResponse by remember { mutableStateOf("") }
    var isLoadingGenAI by remember { mutableStateOf(false) }
    var showTipsHistory by remember { mutableStateOf(false) }
    var savedTips by remember { mutableStateOf<List<NutriCoachTip>>(emptyList()) }

    // Get the user's fruit score
    var patientData by remember { mutableStateOf<Patient?>(null) }
    var fruitScore by remember { mutableStateOf(0f) }
    var isOptimalFruitScore by remember { mutableStateOf(false) }

    // Initialize the Gemini model
    val generativeModel = remember {
        GenerativeModel(
            modelName = "gemini-1.5-flash-latest",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    // Fruit search function
    fun searchFruit() {
        if (fruitNameQuery.isBlank()) {
            fruitError = "Please enter the name of the fruit"
            return
        }
        coroutineScope.launch {
            isLoadingFruit = true
            fruitDetails = null
            fruitError = null
            try {
                val response = ApiClient.fruityViceApi.getFruitByName(fruitNameQuery)
                if (response.isSuccessful && response.body() != null) {
                    fruitDetails = response.body()
                } else {
                    fruitError = "No information found for this fruit"
                }
            } catch (e: Exception) {
                fruitError = "Query error: ${e.message}"
            } finally {
                isLoadingFruit = false
            }
        }
    }

    // Load history prompt function
    fun loadSavedTips() {
        coroutineScope.launch {
            try {
                savedTips = repository.getUserTips(userId)
            } catch (e: Exception) {
            }
        }
    }

    // Generate AI reply function
    fun generateAIResponse() {
        isLoadingGenAI = true
        genAIResponse = ""

        // Create more detailed prompt words, including user data and fruit information
        val prompt = buildString {
            append("Generate a short encouraging message to help someone improve their fruit intake, without using Markdown format. ")
            append("User's fruit score is ${fruitScore}/10. ")

            // If there are details about the fruits, add this information
            if (fruitDetails != null) {
                append("They just searched for ${fruitDetails?.name} fruit which contains ")
                append("${fruitDetails?.nutritions?.calories} calories, ")
                append("${fruitDetails?.nutritions?.sugar}, ")
                append("${fruitDetails?.nutritions?.carbohydrates}, ")
                append("${fruitDetails?.nutritions?.protein}, ")
                append("and ${fruitDetails?.nutritions?.fat}. ")
            }

            append("Provide brief suggestions on the benefits and recommendations of fruit intake, without using Markdown format.")
        }

        coroutineScope.launch {
            try {
                // Generate responses using Gemini
                val response = generativeModel.generateContent(prompt)
                genAIResponse = response.text ?: "Unable to generate a reply"

                // Save to the database
                if (genAIResponse.isNotEmpty()) {
                    val tip = NutriCoachTip(
                        userId = userId,
                        content = genAIResponse,
                        category = if (fruitDetails != null) "Fruit Analysis" else "Health advice",
                        timestamp = System.currentTimeMillis()
                    )
                    repository.saveTip(tip)
                    // Break the historical record
                    loadSavedTips()
                }
            } catch (e: Exception) {
                genAIResponse = "An error occurred when generating the reply: ${e.message}"
            } finally {
                isLoadingGenAI = false
            }
        }
    }

    // Loading user data and history prompts
    LaunchedEffect(userId) {
        coroutineScope.launch {
            try {
                // Load user data
                patientData = repository.getPatientById(userId)

                // Obtain the correct fruit score based on gender
                if (patientData != null) {
                    val isMale = patientData?.sex?.equals("male", ignoreCase = true) ?: false
                    fruitScore = if (isMale) {
                        patientData?.FruitHEIFAscoreMale ?: 0f
                    } else {
                        patientData?.FruitHEIFAscoreFemale ?: 0f
                    }

                    // Determine whether the fruit score reaches the ideal value (greater than or equal to 5 points)
                    isOptimalFruitScore = fruitScore >= 5f
                }

                // Loading historical prompts
                loadSavedTips()

                // Automatically generate initial AI suggestions
                generateAIResponse()
            } catch (e: Exception) {
            }
        }
    }

    Scaffold(
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NutriCoach",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(16.dp))

        // Fruit search section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isOptimalFruitScore) {
                Text("Your fruit intake score is good！", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = "https://picsum.photos/seed/${userId}/400/300", // Example Random Image
                    contentDescription = "Celebrating healthy fruit intake",
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .aspectRatio(4f / 3f)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Text("Fruit intake score：${String.format("%.1f", fruitScore)}/10", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(16.dp))

                // Search box section
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Fruit Name",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = fruitNameQuery,
                            onValueChange = { fruitNameQuery = it },
                            placeholder = { Text("Enter the name of the fruit") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(onClick = { searchFruit() }, enabled = !isLoadingFruit) {
                            Icon(Icons.Filled.Search, contentDescription = "Search")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Details")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                fruitError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                }

                // Fruit Details Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Header row
                        if (fruitDetails != null) {
                            Text(
                                text = "Fruit Details: ${fruitDetails?.name ?: ""}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        } else if (!isLoadingFruit) {
                            Text(
                                text = "Fruit Details",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        // Loading Indicator
                        if (isLoadingFruit) {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            // Fruit Details Table
                            Row(modifier = Modifier.fillMaxWidth()) {
                                // Left Column - Tags
                                Column(modifier = Modifier.weight(0.4f)) {
                                    Text("Family")
                                    Text("Calories")
                                    Text("Fat")
                                    Text("Sugar")
                                    Text("Carbohydrates")
                                    Text("Protein")
                                }

                                // Middle separator column
                                Column(modifier = Modifier.width(20.dp)) {
                                    Text(":")
                                    Text(":")
                                    Text(":")
                                    Text(":")
                                    Text(":")
                                    Text(":")
                                }

                                // Right Column - Value
                                Column(modifier = Modifier.weight(0.5f)) {
                                    Text(fruitDetails?.family ?: "")
                                    Text(fruitDetails?.nutritions?.calories?.toString() ?: "")
                                    Text("${fruitDetails?.nutritions?.fat ?: ""} ")
                                    Text("${fruitDetails?.nutritions?.sugar ?: ""} ")
                                    Text("${fruitDetails?.nutritions?.carbohydrates ?: ""} ")
                                    Text("${fruitDetails?.nutritions?.protein ?: ""} ")
                                }
                            }
                        }
                    }
                }
            }
        }

        Divider()

// AI Suggestions Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("NutriCoach AI Nutrition Advice", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

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
                    Text("Hit the refresh button or search for fruit to get suggestions.")
                }
            }
        }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { generateAIResponse() }
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Refresh")
                }

                Button(
                    onClick = {
                        loadSavedTips()
                        showTipsHistory = true
                    }
                ) {
                    Icon(Icons.Filled.History, contentDescription = "Show All tips")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Show All tips")
                }
            }

            if (showTipsHistory) {
                AlertDialog(
                    onDismissRequest = { showTipsHistory = false },
                    title = { Text("AI Tips") },
                    text = {
                        if (savedTips.isEmpty()) {
                            Text("No Tips record yet.")
                        } else {
                            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                savedTips.forEach { tip ->
                                    Card(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                    ) {
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
                            Text("Close")
                        }
                    }
                )
            }
        }
    }
}