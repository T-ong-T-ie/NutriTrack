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

data class User(val userId: String, val phoneNumber: String)

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
        e.printStackTrace()
    }
    return users
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)
    val users = loadUsersFromCsv()
    val userIds = users.map { it.userId }
    var selectedId by remember { mutableStateOf("Select User ID") }
    var phoneNumber by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(16.dp))

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
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .heightIn(max = 200.dp)
                        .verticalScroll(rememberScrollState())
                ) {
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

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(onClick = {
                val selectedUser = users.find { it.userId == selectedId }
                if (selectedUser != null && selectedUser.phoneNumber == phoneNumber) {
                    with(prefs.edit()) {
                        putString("user_id", selectedId)
                        putBoolean("is_logged_in", true)
                        apply()
                    }
                    val hasQuestionnaireData = prefs.contains("categories")
                    if (hasQuestionnaireData) {
                        navController.navigate("home") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    } else {
                        navController.navigate("questionnaire")
                    }
                } else {
                    errorMessage = "Invalid User ID or Phone Number"
                }
            }) {
                Text("Continue")
            }
        }
    }
}