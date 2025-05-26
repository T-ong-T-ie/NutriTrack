package com.fit2081.hulongxi33555397

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fit2081.hulongxi33555397.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminViewScreen(navController: NavController) {
    // Using ViewModel
    val viewModel: AdminViewModel = viewModel()

    // Observing ViewModel Status
    val isLoading by viewModel.isLoading.observeAsState(true)
    val isAnalyzing by viewModel.isAnalyzing.observeAsState(false)
    val maleAvgScore by viewModel.maleAvgScore.observeAsState(0f)
    val femaleAvgScore by viewModel.femaleAvgScore.observeAsState(0f)
    val patterns by viewModel.patterns.observeAsState(listOf())
    val patientStats by viewModel.patientStats.observeAsState(emptyMap())

    // Loading data
    LaunchedEffect(Unit) {
        viewModel.loadData()
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
                                onClick = { viewModel.analyzeDataWithGenAI() },
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