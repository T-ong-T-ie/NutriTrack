package com.fit2081.hulongxi33555397

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.io.BufferedReader
import java.io.InputStreamReader
import android.content.Context
import androidx.compose.material.icons.filled.ArrowBack

// Define the structure for user data, including user ID and phone number
data class User(val userId: String, val phoneNumber: String)

// Function to load the user list from the users.csv file in assets
@Composable
fun loadUsersFromCsv(): List<User> {
    // Get the current context for accessing assets
    val context = LocalContext.current
    // Create a mutable list to store user data
    val users = mutableListOf<User>()
    try {
        // Open the users.csv file in the assets directory
        context.assets.open("users.csv").use { inputStream ->
            // Use BufferedReader to read the CSV file content
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // Skip the header row and read data line by line
                reader.readLines().drop(1).forEach { line ->
                    // Split each line into columns by commas
                    val columns = line.split(",")
                    // The first column is the phone number, the second column is the user ID
                    val phoneNumber = columns[0]
                    val userId = columns[1]
                    // Create a User object and add it to the list
                    users.add(User(userId, phoneNumber))
                }
            }
        }
    } catch (e: Exception) {
        // Print stack trace if an exception occurs while reading the file or parsing data
        e.printStackTrace()
    }
    // Return the loaded user list
    return users
}

// LoginScreen is the login page, allowing users to select an ID and enter a phone number for verification
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    // Get the current context for accessing SharedPreferences
    val context = LocalContext.current
    // Use SharedPreferences to store user data and login status
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)
    // Load the user data list
    val users = loadUsersFromCsv()
    // Extract all user IDs for the dropdown menu
    val userIds = users.map { it.userId }
    // Use remember to save the selected user ID state, with an initial value as a prompt text
    var selectedId by remember { mutableStateOf("Select User ID") }
    // Use remember to save the entered phone number state
    var phoneNumber by remember { mutableStateOf("") }
    // Use remember to save the error message state
    var errorMessage by remember { mutableStateOf("") }

    // Use Scaffold to manage the page layout, including the top bar
    Scaffold(
        topBar = {
            // Top bar displaying the title and back button
            TopAppBar(
                title = { Text("Login") }, // Title is "Login"
                navigationIcon = {
                    // Back button, navigates to the previous page when clicked
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back" // Accessibility description
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        // Main content area, using a Column for vertical arrangement
        Column(
            modifier = Modifier
                .fillMaxSize() // Fill the entire screen
                .padding(innerPadding) // Adapt to the inner padding of Scaffold
                .padding(16.dp), // Additional padding of 16dp on all sides
            horizontalAlignment = Alignment.CenterHorizontally, // Center content horizontally
            verticalArrangement = Arrangement.Center // Center content vertically
        ) {
            // Add vertical spacing of 16dp
            Spacer(modifier = Modifier.height(16.dp))

            // Use remember to save the dropdown menu's expanded state
            var expanded by remember { mutableStateOf(false) }
            // Dropdown menu component for selecting a user ID
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded } // Toggle expanded state on click
            ) {
                // Display the currently selected user ID, read-only
                OutlinedTextField(
                    value = selectedId,
                    onValueChange = { }, // Read-only, no input handling needed
                    label = { Text("User ID") }, // Field label
                    readOnly = true, // Set to read-only
                    modifier = Modifier.menuAnchor() // Anchor for the dropdown menu
                )
                // Dropdown menu content
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }, // Close menu when clicking outside
                    modifier = Modifier
                        .heightIn(max = 200.dp) // Maximum height of 200dp
                        .verticalScroll(rememberScrollState()) // Enable vertical scrolling
                ) {
                    // Create menu items for each user ID
                    userIds.forEach { id ->
                        DropdownMenuItem(
                            text = { Text(id) }, // Display the user ID
                            onClick = {
                                selectedId = id // Update state when selected
                                expanded = false // Close the menu
                            }
                        )
                    }
                }
            }

            // Add vertical spacing of 16dp
            Spacer(modifier = Modifier.height(16.dp))

            // Text field for entering the phone number
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it }, // Update the input value
                label = { Text("Phone Number") } // Field label
            )

            // Add vertical spacing of 16dp
            Spacer(modifier = Modifier.height(16.dp))

            // If there is an error message, display it in red text
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp)) // Add spacing of 8dp
            }

            // Continue button to validate user input and navigate
            Button(onClick = {
                // Find the user matching the selected ID
                val selectedUser = users.find { it.userId == selectedId }
                // Validate if the user ID and phone number match
                if (selectedUser != null && selectedUser.phoneNumber == phoneNumber) {
                    with(prefs.edit()) {
                        // Save the user ID and login status
                        putString("user_id", selectedId)
                        putBoolean("is_logged_in", true)
                        apply()
                    }
                    // Check if questionnaire data already exists
                    val hasQuestionnaireData = prefs.contains("categories")
                    if (hasQuestionnaireData) {
                        // If questionnaire data exists, navigate to the home page and clear the navigation stack to the welcome page
                        navController.navigate("home") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    } else {
                        // If no questionnaire data, navigate to the questionnaire page
                        navController.navigate("questionnaire")
                    }
                } else {
                    // If validation fails, display an error message
                    errorMessage = "Invalid User ID or Phone Number"
                }
            }) {
                Text("Continue")
            }
        }
    }
}