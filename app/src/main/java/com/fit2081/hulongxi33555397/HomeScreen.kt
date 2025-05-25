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
import com.fit2081.hulongxi33555397.db.NutritrackRepository
import com.fit2081.hulongxi33555397.utils.format
import kotlinx.coroutines.launch

// HomeScreen is the main page of the app, displaying user information, diet score, and logout functionality
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)
    val repository = remember { NutritrackRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    // Get the user ID, defaulting to "Unknown"
    val userId = prefs.getString("user_id", "Unknown") ?: "Unknown"
    val selectedCategories = prefs.getString("${userId}_categories", "")?.split(",") ?: emptyList()

    // 从数据库获取用户数据
    var patientData by remember { mutableStateOf<com.fit2081.hulongxi33555397.db.Patient?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // 加载用户数据
    LaunchedEffect(userId) {
        coroutineScope.launch {
            try {
                patientData = repository.getPatientById(userId)
                isLoading = false
            } catch (e: Exception) {
                e.printStackTrace()
                isLoading = false
            }
        }
    }

    // 从用户数据中提取分数信息
    val isMale = patientData?.sex == "Male"
    val totalScore = if (isMale) patientData?.heifaTotalScoreMale ?: 0f else patientData?.heifaTotalScoreFemale ?: 0f
    val maxTotalScore = 100f

    // Use Scaffold to manage the page layout, leaving the top bar empty
    Scaffold(
        topBar = { } // Top bar is not used, left empty
    ) { padding ->
        // 主要内容区域
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Main content area, using a Column for vertical arrangement
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(
                        top = 16.dp,
                        bottom = padding.calculateBottomPadding()
                    )
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                // Display a gray greeting "Hello,"
                Text(
                    text = "Hello,",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Gray
                )

                // 显示用户名或用户ID
                Text(
                    text = patientData?.name ?: userId,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                // Add vertical spacing of 8dp
                Spacer(modifier = Modifier.height(8.dp))

                // Horizontal layout for prompt text and Edit button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "You've already filled in your Food Intake Questionnaire, but you can change details here:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(onClick = { navController.navigate("questionnaire?isEdit=true") }) {
                        Text("Edit")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Display a healthy eating infographic
                Image(
                    painter = painterResource(id = R.drawable.homescreen_picture), // Load image from resources
                    contentDescription = "Healthy eating infographic",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Horizontal layout for "My Score" title and "See all scores" button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Score",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    // Button to view detailed scores, navigates to the Insights page when clicked
                    TextButton(onClick = { navController.navigate("insights") }) {
                        Text("See all scores >")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Display the food quality score
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Food Quality score",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "${totalScore.format(2)}/100",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF006400)
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                // Add a gray divider line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.Gray)
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Title
                Text(
                    text = "What is the Food Quality Score?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Explanation text 1 for the food quality score
                Text(
                    text = "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Explanation text 2 for the food quality score
                Text(
                    text = "This personalized measurement considers various food groups including vegetables, fruits, whole grains, and proteins to give you practical insights for making healthier food choices.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

