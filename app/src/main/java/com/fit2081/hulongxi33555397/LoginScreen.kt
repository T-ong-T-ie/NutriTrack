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
    val context = LocalContext.current
    val users = mutableListOf<User>()
    try {
        context.assets.open("users.csv").use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                reader.readLines().drop(1).forEach { line ->
                    val columns = line.split(",")
                    val phoneNumber = columns[0]
                    val userId = columns[1]
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
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)
    val users = loadUsersFromCsv()
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
            TopAppBar(
                title = { Text("Login") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        // Main content area, using a Column for vertical arrangement
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Use remember to save the dropdown menu's expanded state
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedId,
                    onValueChange = { },
                    label = { Text("User ID") },
                    readOnly = true,
                    modifier = Modifier.menuAnchor()
                )
                // Dropdown menu content
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .heightIn(max = 200.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Create menu items for each user ID
                    userIds.forEach { id ->
                        DropdownMenuItem(
                            text = { Text(id) },
                            onClick = {
                                selectedId = id
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Text field for entering the phone number
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // If there is an error message, display it in red text
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Continue button to validate user input and navigate
            Button(onClick = {
                val selectedUser = users.find { it.userId == selectedId }
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