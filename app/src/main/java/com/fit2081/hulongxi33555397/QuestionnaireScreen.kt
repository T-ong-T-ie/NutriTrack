package com.fit2081.hulongxi33555397

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fit2081.hulongxi33555397.viewmodel.QuestionnaireViewModel
import com.fit2081.hulongxi33555397.viewmodel.UserSessionViewModel

data class Persona(val name: String, val description: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireScreen(navController: NavController, isEdit: Boolean = false) {
    val context = LocalContext.current

    // Using ViewModel
    val viewModel: QuestionnaireViewModel = viewModel()
    val sessionViewModel: UserSessionViewModel = viewModel()

    // Observing ViewModel Status
    val userId by sessionViewModel.userId.observeAsState("Unknown")
    val selectedCategories by viewModel.selectedCategories.observeAsState(emptyList())
    val selectedPersona by viewModel.selectedPersona.observeAsState(null)
    val selectedTimes by viewModel.selectedTimes.observeAsState(List(3) { "" })
    val isDataComplete by viewModel.isDataComplete.observeAsState(false)
    val isLoading by viewModel.isLoading.observeAsState(false)
    val hasCompletedQuestionnaire by viewModel.hasCompletedQuestionnaire.observeAsState(false)

    // List of food groups
    val foodCategories = listOf("Fruits", "Vegetables", "Grains", "Red Meat", "Seafood", "Poultry", "Fish", "Nuts/Seeds", "Eggs")

    // Persona list
    val personas = listOf(
        Persona("Health Devotee", "I'm passionate about healthy eating & health plays a big part in my life. I use social media to follow active lifestyle personalities or get new recipes/exercise ideas. I may even buy superfoods or follow a particular type of diet. I like to think I am super healthy."),
        Persona("Mindful Eater", "I'm health-conscious and being healthy and eating healthy is important to me. Although health means different things to different people, I make conscious lifestyle decisions about eating based on what I believe healthy means. I look for new recipes and healthy eating information on social media."),
        Persona("Wellness Striver", "I aspire to be healthy (but struggle sometimes). Healthy eating is hard work! I've tried to improve my diet, but always find things that make it difficult to stick with the changes. Sometimes I notice recipe ideas or healthy eating hacks, and if it seems easy enough, I'll give it a go."),
        Persona("Balance Seeker", "I try and live a balanced lifestyle, and I think that all foods are okay in moderation. I shouldn't have to feel guilty about eating a piece of cake now and again. I get all sorts of inspiration from social media like finding out about new restaurants, fun recipes and sometimes healthy eating tips."),
        Persona("Health Procrastinator", "I'm contemplating healthy eating but it's not a priority for me right now. I know the basics about what it means to be healthy, but it doesn't seem relevant to me right now. I have taken a few steps to be healthier but I am not motivated to make it a high priority because I have too many other things going on in my life."),
        Persona("Food Carefree", "I'm not bothered about healthy eating. I don't really see the point and I don't think about it. I don't really notice healthy eating tips or recipes and I don't care what I eat.")
    )

    var showModal by remember { mutableStateOf(false) }
    var currentPersona by remember { mutableStateOf<Persona?>(null) }

    val timeQuestions = listOf(
        "What time of day approx. do you normally eat your biggest meal?",
        "What time of day approx. do you go to sleep at night?",
        "What time of day approx. do you wake up in the morning?"
    )

    val timePickerStates = List(3) { rememberTimePickerState(initialHour = 12, initialMinute = 0) }
    var showTimePicker by remember { mutableStateOf(-1) }

    // Loading data
    LaunchedEffect(Unit) {
        viewModel.checkQuestionnaireStatus(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Intake Questionnaire") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 食物类别部分
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Tick all the food categories you can eat",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    for (i in 0 until 3) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            for (j in 0 until 3) {
                                val index = i * 3 + j
                                if (index < foodCategories.size) {
                                    val category = foodCategories[index]
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(4.dp)
                                            .selectable(
                                                selected = selectedCategories.contains(category),
                                                onClick = {
                                                    viewModel.toggleCategorySelection(category)
                                                }
                                            )
                                            .padding(4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Checkbox(
                                            checked = selectedCategories.contains(category),
                                            onCheckedChange = null
                                        )
                                        Text(
                                            text = category,
                                            style = MaterialTheme.typography.bodyMedium,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Persona部分
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Your Persona",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "People can be broadly classified into 6 different types based on their eating preferences. " +
                            "Click on each button below to find out the different types, and select the type that best fits you!",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in 0 until 3) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val persona1 = personas[i * 2]
                            val persona2 = personas[i * 2 + 1]
                            PersonaButton(
                                persona = persona1,
                                isSelected = selectedPersona == persona1.name,
                                onClick = {
                                    currentPersona = persona1
                                    showModal = true
                                },
                                modifier = Modifier.weight(1f)
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
                Spacer(modifier = Modifier.height(16.dp))

                // Time part
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Timing",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                timeQuestions.forEachIndexed { index, question ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = question,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        )
                        Button(
                            onClick = { showTimePicker = index },
                            modifier = Modifier.width(100.dp)
                        ) {
                            Text(
                                text = selectedTimes[index].ifEmpty { "Select" },
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = {
                        viewModel.saveQuestionnaireData(userId)
                        navController.navigate("home")
                    },
                    enabled = isDataComplete
                ) {
                    Text("Submit")
                }
            }
        }
    }

    // Display the time picker dialog
    if (showTimePicker >= 0) {
        AlertDialog(
            onDismissRequest = { showTimePicker = -1 },
            title = { Text(timeQuestions[showTimePicker]) },
            text = { TimePicker(state = timePickerStates[showTimePicker]) },
            confirmButton = {
                TextButton(onClick = {
                    val hour = timePickerStates[showTimePicker].hour.toString().padStart(2, '0')
                    val minute = timePickerStates[showTimePicker].minute.toString().padStart(2, '0')
                    viewModel.setTimeSelection(showTimePicker, "$hour:$minute")
                    showTimePicker = -1
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

    // Display the Persona details modal dialog
    if (showModal && currentPersona != null) {
        AlertDialog(
            onDismissRequest = { showModal = false },
            title = { Text(currentPersona!!.name) },
            text = {
                val dialogContext = LocalContext.current
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val imageName = currentPersona!!.name.lowercase().replace(" ", "")
                    val resourceId = dialogContext.resources.getIdentifier(
                        imageName, "drawable", dialogContext.packageName
                    )

                    Image(
                        painter = painterResource(id = resourceId),
                        contentDescription = currentPersona!!.name,
                        modifier = Modifier
                            .size(100.dp)
                            .border(1.dp, MaterialTheme.colorScheme.onSurface)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(currentPersona!!.description, textAlign = TextAlign.Center)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.selectPersona(currentPersona!!.name)
                        showModal = false
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

@Composable
fun PersonaButton(
    persona: Persona,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonShape = RoundedCornerShape(50)

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(1.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = buttonShape
                    )
            )
        }

        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(1.dp),
            shape = buttonShape
        ) {
            Text(persona.name)
        }
    }
}