import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.fit2081.hulongxi33555397.viewmodel.NutriCoachViewModel
import com.fit2081.hulongxi33555397.models.FruitDetails
import com.fit2081.hulongxi33555397.models.NutriCoachTip
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutriCoachScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)
    val userId = prefs.getString("user_id", "") ?: ""

    val viewModel: NutriCoachViewModel = viewModel()

    val fruitNameQuery by viewModel.fruitNameQuery.observeAsState("")
    val fruitDetails by viewModel.fruitDetails.observeAsState<FruitDetails?>()
    val isLoadingFruit by viewModel.isLoadingFruit.observeAsState(false)
    val fruitError by viewModel.fruitError.observeAsState()
    val fruitScore by viewModel.fruitScore.observeAsState(0f)
    val isOptimalFruitScore by viewModel.isOptimalFruitScore.observeAsState(false)

    val genAIResponse by viewModel.genAIResponse.observeAsState("")
    val isLoadingGenAI by viewModel.isLoadingGenAI.observeAsState(false)
    val savedTips by viewModel.savedTips.observeAsState(emptyList<NutriCoachTip>())
    val showTipsHistory by viewModel.showTipsHistory.observeAsState(false)

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            viewModel.loadUserData(userId)
            viewModel.loadSavedTips()
            viewModel.generateAIResponse()
        }
    }

    Scaffold { paddingValues ->
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isOptimalFruitScore) {
                    Text("Your fruit intake score is good!", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = "https://picsum.photos/seed/${userId}/400/300",
                        contentDescription = "Celebrating healthy fruit intake",
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .aspectRatio(4f / 3f)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Text("Fruit intake score: ${String.format("%.1f", fruitScore)}/10", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
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
                                onValueChange = { viewModel.setFruitNameQuery(it) },
                                placeholder = { Text("Enter the name of the fruit") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { viewModel.searchFruit() }, enabled = !isLoadingFruit) {
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

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
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

                            if (isLoadingFruit) {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.weight(0.4f)) {
                                        Text("Family")
                                        Text("Calories")
                                        Text("Fat")
                                        Text("Sugar")
                                        Text("Carbohydrates")
                                        Text("Protein")
                                    }
                                    Column(modifier = Modifier.width(20.dp)) {
                                        Text(":")
                                        Text(":")
                                        Text(":")
                                        Text(":")
                                        Text(":")
                                        Text(":")
                                    }
                                    Column(modifier = Modifier.weight(0.5f)) {
                                        Text(fruitDetails?.family ?: "")
                                        Text(fruitDetails?.nutritions?.calories?.toString() ?: "")
                                        Text("${fruitDetails?.nutritions?.fat?.toString() ?: ""}g")
                                        Text("${fruitDetails?.nutritions?.sugar?.toString() ?: ""}g")
                                        Text("${fruitDetails?.nutritions?.carbohydrates?.toString() ?: ""}g")
                                        Text("${fruitDetails?.nutritions?.protein?.toString() ?: ""}g")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Divider()

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
                            genAIResponse,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            textAlign = TextAlign.Justify
                        )
                    } else {
                        Text("Hit the refresh button or search for fruit to get suggestions。")
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { viewModel.generateAIResponse() }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Refresh")
                }
                Button(onClick = {
                    viewModel.loadSavedTips()
                    viewModel.setShowTipsHistory(true)
                }) {
                    Icon(Icons.Filled.History, contentDescription = "Show All tips")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Show All tips")
                }
            }

            if (showTipsHistory) {
                AlertDialog(
                    onDismissRequest = { viewModel.setShowTipsHistory(false) },
                    title = { Text("AI Tips") },
                    text = {
                        if (savedTips.isEmpty()) {
                            Text("No Tips record yet.")
                        } else {
                            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                savedTips.forEach { tip ->
                                    Card(
                                        modifier = Modifier
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
                        TextButton(onClick = { viewModel.setShowTipsHistory(false) }) {
                            Text("Close")
                        }
                    }
                )
            }
        }
    }
}