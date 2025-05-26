package com.fit2081.hulongxi33555397

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fit2081.hulongxi33555397.db.NutritrackRepository
import kotlinx.coroutines.launch
import com.google.ai.client.generativeai.GenerativeModel
import kotlin.toString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminViewScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { NutritrackRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    // State variables
    var isLoading by remember { mutableStateOf(true) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var maleAvgScore by remember { mutableStateOf(0f) }
    var femaleAvgScore by remember { mutableStateOf(0f) }
    var patterns by remember { mutableStateOf(listOf<String>()) }
    var patientStats by remember { mutableStateOf<Map<String, Any>>(emptyMap()) }

    // Initialize the Gemini model
    val generativeModel = remember {
        GenerativeModel(
            modelName = "gemini-1.5-flash-latest",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    // Analyzing Data with GenAI
    fun analyzeDataWithGenAI() {
        if (patientStats.isEmpty()) return

        isAnalyzing = true
        patterns = emptyList()

        coroutineScope.launch {
            try {
                val prompt = buildString {
                    append("As a nutrition data analyst, please find 3 interesting patterns or insights based on the following data:\n")
                    append("- Average HEIFA score for male users:${String.format("%.2f", maleAvgScore)}\n")
                    append("- Average HEIFA score for female users:${String.format("%.2f", femaleAvgScore)}\n")

                    // Add more detailed statistics
                    patientStats.forEach { (key, value) ->
                        append("- $key: $value\n")
                    }

                    append("\nPlease provide 3 interesting findings based on data. Each finding should be a complete and concise sentence. Do not use Markdown format.")
                    append("Do not include serial numbers or prefixes. I will automatically add serial numbers on the interface.")
                    append("All replies must be in English.")
                }

                val response = generativeModel.generateContent(prompt)
                val analysisText = response.text ?: "Unable to generate analysis results"

                // Process the response text, splitting it into individual findings
                patterns = analysisText
                    .split("\n")
                    .filter { it.isNotBlank() }
                    .map { it.trim().removePrefix("-").trim() }
                    .take(3)

                if (patterns.isEmpty()) {
                    patterns = listOf("No obvious patterns can be discerned from the data")
                }
            } catch (e: Exception) {
                patterns = listOf(
                    "An error occurred while generating data analysis",
                    "Please check your network connection or try again",
                    "If the problem persists, please contact technical support"
                )
            } finally {
                isAnalyzing = false
            }
        }
    }

    // Loading data
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                // Get all patient data
                val patients = repository.getAllPatients()
                val stats = mutableMapOf<String, Any>()

                // Calculating the average HEIFA score for men
                val malePatients = patients.filter { it.sex.equals("Male", ignoreCase = true) }
                maleAvgScore = if (malePatients.isNotEmpty()) {
                    malePatients.sumOf { it.heifaTotalScoreMale.toDouble() }.toFloat() / malePatients.size
                } else {
                    0f
                }
                stats["Number of male users"] = malePatients.size

                // Calculating the average HEIFA score for women
                val femalePatients = patients.filter { it.sex.equals("Female", ignoreCase = true) }
                femaleAvgScore = if (femalePatients.isNotEmpty()) {
                    femalePatients.sumOf { it.heifaTotalScoreFemale.toDouble() }.toFloat() / femalePatients.size
                } else {
                    0f
                }
                stats["Number of female users"] = femalePatients.size

                // Calculate other useful statistics
                if (malePatients.isNotEmpty()) {
                    stats["Average vegetable score for Men:"] = malePatients.sumOf { it.VegetablesHEIFAscoreMale.toDouble() }.toFloat() / malePatients.size
                    stats["Average fruit score for Men:"] = malePatients.sumOf { it.FruitHEIFAscoreMale.toDouble() }.toFloat() / malePatients.size
                    stats["Average Whole Grain Score for Men:"] = malePatients.sumOf { it.WholegrainsHEIFAscoreMale.toDouble() }.toFloat() / malePatients.size
                    stats["Average Sodium Score for Men:"] = malePatients.sumOf { it.SodiumHEIFAscoreMale.toDouble() }.toFloat() / malePatients.size
                    stats["Average Sugar Score for Men:"] = malePatients.sumOf { it.SugarHEIFAscoreMale.toDouble() }.toFloat() / malePatients.size
                }

                if (femalePatients.isNotEmpty()) {
                    stats["Average vegetable score for Women:"] = femalePatients.sumOf { it.VegetablesHEIFAscoreFemale.toDouble() }.toFloat() / femalePatients.size
                    stats["Average fruit score for Women:"] = femalePatients.sumOf { it.FruitHEIFAscoreFemale.toDouble() }.toFloat() / femalePatients.size
                    stats["Average Whole Grain Score for Women:"] = femalePatients.sumOf { it.WholegrainsHEIFAscoreFemale.toDouble() }.toFloat() / femalePatients.size
                    stats["Average Sodium Score for Women:"] = femalePatients.sumOf { it.SodiumHEIFAscoreFemale.toDouble() }.toFloat() / femalePatients.size
                    stats["Average Sugar Score for Women:"] = femalePatients.sumOf { it.SugarHEIFAscoreFemale.toDouble() }.toFloat() / femalePatients.size
                }

                patientStats = stats

                // Initial analysis data
                analyzeDataWithGenAI()

                isLoading = false
            } catch (e: Exception) {
                e.printStackTrace()
                isLoading = false
                patterns = listOf(
                    "An error occurred while loading data",
                    "Please check your database connection",
                    "If the problem persists, please contact technical support"
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin view") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(32.dp))
            } else {
                // Show Statistics
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "User statistics",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Average HEIFA score for male users：")
                            Text("${String.format("%.2f", maleAvgScore)}")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Average HEIFA score for female users：")
                            Text("${String.format("%.2f", femaleAvgScore)}")
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Spacer(modifier = Modifier.height(16.dp))

                        // Show more statistics
                        Text(
                            "Male user statistics",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        patientStats.entries.forEach { (key, value) ->
                            if (key !in listOf("Number of male users", "Number of female users")) {
                                if (key.contains("Male") || key.contains("Men")) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(key)
                                        Text(
                                            when (value) {
                                                is Float -> String.format("%.2f", value)
                                                else -> value.toString()
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Female user statistics",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        patientStats.entries.forEach { (key, value) ->
                            if (key !in listOf("Number of male users", "Number of female users")) {
                                if (key.contains("female") || key.contains("Women")) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(key)
                                        Text(
                                            when (value) {
                                                is Float -> String.format("%.2f", value)
                                                else -> value.toString()
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Display GenAI data pattern analysis
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "AI data analysis results",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            IconButton(
                                onClick = { analyzeDataWithGenAI() },
                                enabled = !isAnalyzing
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Reanalysis")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isAnalyzing) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator()
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("AI is analyzing data...", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        } else {
                            patterns.forEachIndexed { index, pattern ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = "${index + 1}.",
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(
                                            text = pattern,
                                            textAlign = TextAlign.Start
                                        )
                                    }
                                }

                                if (index < patterns.size - 1) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}