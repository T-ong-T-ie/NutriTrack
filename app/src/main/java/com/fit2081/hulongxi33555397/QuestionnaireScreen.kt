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

data class Persona(val name: String, val description: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireScreen(navController: NavController, isEdit: Boolean = false) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)
    val foodCategories = listOf("Fruits", "Vegetables", "Grains", "Red Meat", "Seafood", "Poultry", "Fish", "Nuts/Seeds", "Eggs")
    val selectedCategories = remember { mutableStateListOf<String>() }

    val personas = listOf(
        Persona("Health Devotee", "I'm passionate about healthy eating & health plays a big part in my life. I use social media to follow active lifestyle personalities or get new recipes/exercise ideas. I may even buy superfoods or follow a particular type of diet. I like to think I am super healthy."),
        Persona("Mindful Eater", "I'm health-conscious and being healthy and eating healthy is important to me. Although health means different things to different people, I make conscious lifestyle decisions about eating based on what I believe healthy means. I look for new recipes and healthy eating information on social media."),
        Persona("Wellness Striver", "I aspire to be healthy (but struggle sometimes). Healthy eating is hard work! I've tried to improve my diet, but always find things that make it difficult to stick with the changes. Sometimes I notice recipe ideas or healthy eating hacks, and if it seems easy enough, I'll give it a go."),
        Persona("Balance Seeker", "I try and live a balanced lifestyle, and I think that all foods are okay in moderation. I shouldn't have to feel guilty about eating a piece of cake now and again. I get all sorts of inspiration from social media like finding out about new restaurants, fun recipes and sometimes healthy eating tips."),
        Persona("Health Procrastinator", "I'm contemplating healthy eating but it's not a priority for me right now. I know the basics about what it means to be healthy, but it doesn't seem relevant to me right now. I have taken a few steps to be healthier but I am not motivated to make it a high priority because I have too many other things going on in my life."),
        Persona("Food Carefree", "I'm not bothered about healthy eating. I don't really see the point and I don't think about it. I don't really notice healthy eating tips or recipes and I don't care what I eat.")
    )

    var selectedPersona by remember { mutableStateOf<String?>(null) }
    var showModal by remember { mutableStateOf(false) }
    var currentPersona by remember { mutableStateOf<Persona?>(null) }

    val timeQuestions = listOf(
        "What time of day approx. do you normally eat your biggest meal?",
        "What time of day approx. do you go to sleep at night?",
        "What time of day approx. do you wake up in the morning?"
    )
    val selectedTimes = remember { mutableStateListOf("", "", "") }
    val timePickerStates = List(3) { rememberTimePickerState(initialHour = 12, initialMinute = 0) }
    var showTimePicker by remember { mutableStateOf(-1) }

    println("QuestionnaireScreen: isEdit=$isEdit")

    if (isEdit) {
        LaunchedEffect(Unit) {
            selectedCategories.clear()
            selectedCategories.addAll(prefs.getString("categories", "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList())
            selectedPersona = prefs.getString("persona", null)
            selectedTimes[0] = prefs.getString("biggest_meal_time", "") ?: ""
            selectedTimes[1] = prefs.getString("sleep_time", "") ?: ""
            selectedTimes[2] = prefs.getString("wake_time", "") ?: ""
        }
    } else {
        LaunchedEffect(Unit) {
            if (prefs.getString("categories", null) != null) {
                println("Existing data found, navigating to home")
                navController.navigate("home")
            }
        }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 添加食物类别标题，与Timing部分样式保持一致
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

            // 3x3布局的食物类别选择器
            Column(modifier = Modifier.fillMaxWidth()) {
                for (i in 0 until 3) { // 3行
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // 每行3个选项
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
                                                if (selectedCategories.contains(category)) {
                                                    selectedCategories.remove(category)
                                                } else {
                                                    selectedCategories.add(category)
                                                }
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

            // 添加人格类别标题
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
                        modifier = Modifier
                            .width(100.dp)
                    ) {
                        Text(
                            text = selectedTimes[index].ifEmpty { "Select" },
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                println("Submit clicked: categories=$selectedCategories, persona=$selectedPersona, times=$selectedTimes")
                if (selectedCategories.isNotEmpty() && selectedPersona != null && selectedTimes.all { it.isNotEmpty() }) {
                    with(prefs.edit()) {
                        putString("categories", selectedCategories.joinToString(","))
                        putString("persona", selectedPersona)
                        putString("biggest_meal_time", selectedTimes[0])
                        putString("sleep_time", selectedTimes[1])
                        putString("wake_time", selectedTimes[2])
                        apply()
                    }
                    println("Navigating to home from Submit")
                    navController.navigate("home")
                } else {
                    println("Submit failed: categories=${selectedCategories.isEmpty()}, persona=$selectedPersona, times=${selectedTimes.any { it.isEmpty() }}")
                }
            }) {
                Text("Submit")
            }
        }
    }

    if (showTimePicker >= 0) {
        AlertDialog(
            onDismissRequest = { showTimePicker = -1 },
            title = { Text(timeQuestions[showTimePicker]) },
            text = {
                TimePicker(state = timePickerStates[showTimePicker])
            },
            confirmButton = {
                TextButton(onClick = {
                    val hour = timePickerStates[showTimePicker].hour.toString().padStart(2, '0')
                    val minute = timePickerStates[showTimePicker].minute.toString().padStart(2, '0')
                    selectedTimes[showTimePicker] = "$hour:$minute"
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

    if (showModal && currentPersona != null) {
        AlertDialog(
            onDismissRequest = { showModal = false },
            title = { Text(currentPersona!!.name) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // 添加人格图片
                    val imageName = currentPersona!!.name.lowercase().replace(" ", "")
                    val resourceId = context.resources.getIdentifier(
                        imageName, "drawable", context.packageName
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
                        selectedPersona = currentPersona!!.name
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
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isSelected) Modifier.border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(4.dp)
                ) else Modifier
            ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(persona.name)
    }
}