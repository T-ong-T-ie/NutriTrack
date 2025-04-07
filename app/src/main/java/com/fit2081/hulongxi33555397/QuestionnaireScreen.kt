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

// Define the Persona data structure, including name and description
data class Persona(val name: String, val description: String)

// QuestionnaireScreen is the food intake questionnaire page, supporting both first-time and edit modes
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireScreen(navController: NavController, isEdit: Boolean = false) {
    // Get the current context for accessing SharedPreferences and resources
    val context = LocalContext.current
    // Use SharedPreferences to store user data
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)
    // Get the user ID, defaulting to "Unknown"
    val userId = prefs.getString("user_id", "Unknown") ?: "Unknown"

    // Define the list of selectable food categories
    val foodCategories = listOf("Fruits", "Vegetables", "Grains", "Red Meat", "Seafood", "Poultry", "Fish", "Nuts/Seeds", "Eggs")
    // Use remember to create a mutable state list to store selected food categories
    val selectedCategories = remember { mutableStateListOf<String>() }

    // Define the list of Persona options
    val personas = listOf(
        Persona("Health Devotee", "I'm passionate about healthy eating & health plays a big part in my life. I use social media to follow active lifestyle personalities or get new recipes/exercise ideas. I may even buy superfoods or follow a particular type of diet. I like to think I am super healthy."),
        Persona("Mindful Eater", "I'm health-conscious and being healthy and eating healthy is important to me. Although health means different things to different people, I make conscious lifestyle decisions about eating based on what I believe healthy means. I look for new recipes and healthy eating information on social media."),
        Persona("Wellness Striver", "I aspire to be healthy (but struggle sometimes). Healthy eating is hard work! I've tried to improve my diet, but always find things that make it difficult to stick with the changes. Sometimes I notice recipe ideas or healthy eating hacks, and if it seems easy enough, I'll give it a go."),
        Persona("Balance Seeker", "I try and live a balanced lifestyle, and I think that all foods are okay in moderation. I shouldn't have to feel guilty about eating a piece of cake now and again. I get all sorts of inspiration from social media like finding out about new restaurants, fun recipes and sometimes healthy eating tips."),
        Persona("Health Procrastinator", "I'm contemplating healthy eating but it's not a priority for me right now. I know the basics about what it means to be healthy, but it doesn't seem relevant to me right now. I have taken a few steps to be healthier but I am not motivated to make it a high priority because I have too many other things going on in my life."),
        Persona("Food Carefree", "I'm not bothered about healthy eating. I don't really see the point and I don't think about it. I don't really notice healthy eating tips or recipes and I don't care what I eat.")
    )

    // Use remember to save the selected Persona state
    var selectedPersona by remember { mutableStateOf<String?>(null) }
    // Use remember to save the modal dialog display state
    var showModal by remember { mutableStateOf(false) }
    // Use remember to save the currently viewed Persona
    var currentPersona by remember { mutableStateOf<Persona?>(null) }

    // Define the list of time-related questions
    val timeQuestions = listOf(
        "What time of day approx. do you normally eat your biggest meal?",
        "What time of day approx. do you go to sleep at night?",
        "What time of day approx. do you wake up in the morning?"
    )
    // Use remember to create a mutable state list to store selected times
    val selectedTimes = remember { mutableStateListOf("", "", "") }
    // Create time picker states for each time question, with an initial value of 12:00
    val timePickerStates = List(3) { rememberTimePickerState(initialHour = 12, initialMinute = 0) }
    // Use remember to save the time picker display state, -1 means not displayed
    var showTimePicker by remember { mutableStateOf(-1) }

    // Print debug information to show whether it is in edit mode
    println("QuestionnaireScreen: isEdit=$isEdit")

    // If in edit mode, load existing data
    if (isEdit) {
        LaunchedEffect(Unit) {
            selectedCategories.clear()
            // Load existing food categories from SharedPreferences
            selectedCategories.addAll(prefs.getString("${userId}_categories", "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList())
            // Load the selected Persona
            selectedPersona = prefs.getString("${userId}_persona", null)
            // Load existing time data
            selectedTimes[0] = prefs.getString("${userId}_biggest_meal_time", "") ?: ""
            selectedTimes[1] = prefs.getString("${userId}_sleep_time", "") ?: ""
            selectedTimes[2] = prefs.getString("${userId}_wake_time", "") ?: ""
        }
    } else {
        // If not in edit mode and data already exists, navigate to the home page
        LaunchedEffect(Unit) {
            if (prefs.getString("${userId}_categories", null) != null) {
                println("Existing data found, navigating to home")
                navController.navigate("home")
            }
        }
    }

    // Use Scaffold to manage the page layout, including the top bar
    Scaffold(
        topBar = {
            // Top bar displaying the title and back button
            TopAppBar(
                title = { Text("Food Intake Questionnaire") }, // Title
                navigationIcon = {
                    // Back button, navigates to the home page when clicked
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back" // Accessibility description
                        )
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0) // No additional margins
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0) // No additional margins for content area
    ) { innerPadding ->
        // Main content area, using a Column for vertical arrangement
        Column(
            modifier = Modifier
                .fillMaxSize() // Fill the entire screen
                .padding(innerPadding) // Adapt to the inner padding of Scaffold
                .padding(horizontal = 16.dp) // Horizontal padding of 16dp
                .verticalScroll(rememberScrollState()), // Enable vertical scrolling
            horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally
        ) {
            // Food category title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start // Align to the left
            ) {
                Text(
                    text = "Tick all the food categories you can eat",
                    style = MaterialTheme.typography.bodyLarge, // Large body style
                    fontWeight = FontWeight.Bold, // Bold
                    modifier = Modifier.align(Alignment.CenterVertically) // Vertically center
                )
            }
            Spacer(modifier = Modifier.height(8.dp)) // Add 8dp spacing

            // 3x3 layout for food category selectors
            Column(modifier = Modifier.fillMaxWidth()) {
                for (i in 0 until 3) { // 3 rows
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween // Distribute evenly
                    ) {
                        // Each row has 3 options
                        for (j in 0 until 3) {
                            val index = i * 3 + j
                            if (index < foodCategories.size) {
                                val category = foodCategories[index]
                                Column(
                                    modifier = Modifier
                                        .weight(1f) // Distribute width evenly
                                        .padding(4.dp) // Padding of 4dp on all sides
                                        .selectable(
                                            selected = selectedCategories.contains(category),
                                            onClick = {
                                                // Toggle selection state on click
                                                if (selectedCategories.contains(category)) {
                                                    selectedCategories.remove(category)
                                                } else {
                                                    selectedCategories.add(category)
                                                }
                                            }
                                        )
                                        .padding(4.dp), // Additional padding
                                    horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
                                ) {
                                    Checkbox(
                                        checked = selectedCategories.contains(category),
                                        onCheckedChange = null // Handled by selectable
                                    )
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.bodyMedium, // Medium body style
                                        textAlign = TextAlign.Center // Center text
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) // Add 16dp spacing

            // Persona category title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start // Align to the left
            ) {
                Text(
                    text = "Your Persona",
                    style = MaterialTheme.typography.bodyLarge, // Large body style
                    fontWeight = FontWeight.Bold, // Bold
                    modifier = Modifier.align(Alignment.CenterVertically) // Vertically center
                )
            }
            Spacer(modifier = Modifier.height(8.dp)) // Add 8dp spacing

            // Persona description text
            Text(
                text = "People can be broadly classified into 6 different types based on their eating preferences. " +
                        "Click on each button below to find out the different types, and select the type that best fits you!",
                style = MaterialTheme.typography.bodyMedium, // Medium body style
                modifier = Modifier.padding(bottom = 8.dp) // Custom padding
            )
            Spacer(modifier = Modifier.height(8.dp)) // Add 8dp spacing

            // 3x2 layout for Persona buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp) // 8dp spacing between rows
            ) {
                for (i in 0 until 3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // 8dp spacing between columns
                    ) {
                        val persona1 = personas[i * 2]
                        val persona2 = personas[i * 2 + 1]
                        PersonaButton(
                            persona = persona1,
                            isSelected = selectedPersona == persona1.name,
                            onClick = {
                                currentPersona = persona1 // Set the currently viewed Persona
                                showModal = true // Show the modal dialog
                            },
                            modifier = Modifier.weight(1f) // Distribute width evenly
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
            Spacer(modifier = Modifier.height(16.dp)) // Add 16dp spacing

            // Timing questions title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start // Align to the left
            ) {
                Text(
                    text = "Timing",
                    style = MaterialTheme.typography.bodyLarge, // Large body style
                    fontWeight = FontWeight.Bold, // Bold
                    modifier = Modifier.align(Alignment.CenterVertically) // Vertically center
                )
            }
            Spacer(modifier = Modifier.height(8.dp)) // Add 8dp spacing

            // Display timing questions and selection buttons
            timeQuestions.forEachIndexed { index, question ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp), // Vertical padding of 4dp
                    verticalAlignment = Alignment.CenterVertically // Vertically center
                ) {
                    Text(
                        text = question,
                        style = MaterialTheme.typography.bodyMedium, // Medium body style
                        modifier = Modifier
                            .weight(1f) // Occupy remaining space
                            .padding(end = 8.dp) // Right padding of 8dp
                    )
                    Button(
                        onClick = { showTimePicker = index }, // Show the corresponding time picker
                        modifier = Modifier
                            .width(100.dp) // Fixed width of 100dp
                    ) {
                        Text(
                            text = selectedTimes[index].ifEmpty { "Select" }, // Display selected time or prompt
                            textAlign = TextAlign.Center // Center text
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) // Add 16dp spacing

            // Submit button
            Button(onClick = {
                // Print debug information
                println("Submit clicked: categories=$selectedCategories, persona=$selectedPersona, times=$selectedTimes")
                // Check if all fields are filled
                if (selectedCategories.isNotEmpty() && selectedPersona != null && selectedTimes.all { it.isNotEmpty() }) {
                    with(prefs.edit()) {
                        // Save the user's selected food categories, Persona, and times
                        putString("${userId}_categories", selectedCategories.joinToString(","))
                        putString("${userId}_persona", selectedPersona)
                        putString("${userId}_biggest_meal_time", selectedTimes[0])
                        putString("${userId}_sleep_time", selectedTimes[1])
                        putString("${userId}_wake_time", selectedTimes[2])
                        apply()
                    }
                    println("Navigating to home from Submit")
                    // Navigate to the home page
                    navController.navigate("home")
                } else {
                    // If not fully filled, print the failure reason
                    println("Submit failed: categories=${selectedCategories.isEmpty()}, persona=$selectedPersona, times=${selectedTimes.any { it.isEmpty() }}")
                }
            }) {
                Text("Submit")
            }
        }
    }

    // Display the time picker dialog
    if (showTimePicker >= 0) {
        AlertDialog(
            onDismissRequest = { showTimePicker = -1 }, // Close when clicking outside
            title = { Text(timeQuestions[showTimePicker]) }, // Display the corresponding question
            text = {
                // Time picker component
                TimePicker(state = timePickerStates[showTimePicker])
            },
            confirmButton = {
                TextButton(onClick = {
                    // Format the selected time as HH:MM
                    val hour = timePickerStates[showTimePicker].hour.toString().padStart(2, '0')
                    val minute = timePickerStates[showTimePicker].minute.toString().padStart(2, '0')
                    selectedTimes[showTimePicker] = "$hour:$minute"
                    showTimePicker = -1 // Close the dialog
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
            onDismissRequest = { showModal = false }, // Close when clicking outside
            title = { Text(currentPersona!!.name) }, // Display the Persona name
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Dynamically load the image based on the Persona name
                    val imageName = currentPersona!!.name.lowercase().replace(" ", "")
                    val resourceId = context.resources.getIdentifier(
                        imageName, "drawable", context.packageName
                    )

                    Image(
                        painter = painterResource(id = resourceId),
                        contentDescription = currentPersona!!.name, // Accessibility description
                        modifier = Modifier
                            .size(100.dp) // Image size of 100dp
                            .border(1.dp, MaterialTheme.colorScheme.onSurface) // Add border
                    )

                    Spacer(modifier = Modifier.height(8.dp)) // Add 8dp spacing
                    Text(currentPersona!!.description, textAlign = TextAlign.Center) // Display Persona description
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedPersona = currentPersona!!.name // Select the Persona
                        showModal = false // Close the dialog
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

// PersonaButton is a custom component for displaying Persona buttons with support for selected state
@Composable
fun PersonaButton(
    persona: Persona,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Define the button style with a rounded shape
    val buttonShape = RoundedCornerShape(50)

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // If selected, display an outer border
        if (isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize() // Match the button size
                    .padding(1.dp) // Extend the border outward
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary, // Primary color border
                        shape = buttonShape
                    )
            )
        }

        // Inner button
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(1.dp), // Shrink to show the border
            shape = buttonShape // Rounded shape
        ) {
            Text(persona.name) // Display the Persona name
        }
    }
}