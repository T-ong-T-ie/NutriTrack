package com.fit2081.hulongxi33555397

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.content.Context
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit2081.hulongxi33555397.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showAdminLogin by remember { mutableStateOf(false) }
    var adminKey by remember { mutableStateOf("") }
    var keyError by remember { mutableStateOf(false) }

    // Get User ID
    val userId = prefs.getString("user_id", "Unknown") ?: "Unknown"

    // Using ViewModel
    val viewModel: SettingsViewModel = viewModel()

    // Observing ViewModel Status
    val name by viewModel.name.observeAsState("Loading...")
    val phoneNumber by viewModel.phoneNumber.observeAsState("Loading...")
    val isLoading by viewModel.isLoading.observeAsState(true)

    // Loading User Data
    LaunchedEffect(userId) {
        viewModel.loadUserData(userId)
    }

    Scaffold(
        topBar = {
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // User Information Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "User Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    } else {
                        // User Name
                        Text(
                            text = "Name",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Phone Number
                        Text(
                            text = "Phone Number",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = phoneNumber,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // User ID
                        Text(
                            text = "User ID",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = userId,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            OutlinedButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout"
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Logout",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Admin Login Button
            OutlinedButton(
                onClick = { showAdminLogin = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Admin View"
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Admin View",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // Logout confirmation dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Logout") },
                text = { Text("Are you sure you want to log out?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // Clear login status
                            with(prefs.edit()) {
                                putBoolean("is_logged_in", false)
                                apply()
                            }
                            // Navigate to the Welcome page
                            navController.navigate("welcome") {
                                popUpTo("home") { inclusive = true }
                            }
                            showLogoutDialog = false
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("No")
                    }
                }
            )
        }

        // Admin Login Dialog Box
        if (showAdminLogin) {
            AlertDialog(
                onDismissRequest = {
                    showAdminLogin = false
                    adminKey = ""
                    keyError = false
                },
                title = { Text("Clinician Validation") },
                text = {
                    Column {
                        if (keyError) {
                            Text(
                                "Incorrect authentication key",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        OutlinedTextField(
                            value = adminKey,
                            onValueChange = {
                                adminKey = it
                                keyError = false
                            },
                            label = { Text("Enter the verification key") },
                            singleLine = true,
                            isError = keyError
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (adminKey == "dollar-entry-apples") {
                                showAdminLogin = false
                                adminKey = ""
                                navController.navigate("admin_view")
                            } else {
                                keyError = true
                            }
                        }
                    ) {
                        Text("Verify")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showAdminLogin = false
                        adminKey = ""
                        keyError = false
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}