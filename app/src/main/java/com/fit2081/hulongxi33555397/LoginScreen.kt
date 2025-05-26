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
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import com.fit2081.hulongxi33555397.db.NutritrackRepository
import com.fit2081.hulongxi33555397.db.Patient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { NutritrackRepository(context) }

    // Login status management
    var userId by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isFirstLogin by remember { mutableStateOf(false) }
    var isPasswordLogin by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    // 密码可见性控制
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when {
                            isFirstLogin -> "First time login - Claim your account"
                            isPasswordLogin -> "Password login"
                            else -> "Login"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        when {
                            isFirstLogin -> {
                                // Return to ID entry from first login
                                isFirstLogin = false
                                phoneNumber = ""
                                name = ""
                                password = ""
                                confirmPassword = ""
                                errorMessage = ""
                            }
                            isPasswordLogin -> {
                                // Return from password login to ID input
                                isPasswordLogin = false
                                password = ""
                                errorMessage = ""
                            }
                            else -> {
                                // Return to Welcome Page
                                navController.navigateUp()
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Different input interfaces are displayed at different login stages
            when {
                // First login interface
                isFirstLogin -> {
                    // User ID (read only)
                    OutlinedTextField(
                        value = userId,
                        onValueChange = { },
                        label = { Text("User ID") },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // phone number
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Phone number") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Set password") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Password Visibility"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Confirm Password
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Password Visibility"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorMessage = "Please enter your name"
                                return@Button
                            }

                            if (password.length < 6) {
                                errorMessage = "The password must be at least 6 characters long."
                                return@Button
                            }

                            if (password != confirmPassword) {
                                errorMessage = "The passwords you entered twice do not match"
                                return@Button
                            }

                            // Add phone number format check
                            if (!isValidPhoneNumber(phoneNumber)) {
                                errorMessage = "Please enter a valid mobile number"
                                return@Button
                            }

                            isProcessing = true
                            errorMessage = ""

                            // Verify your account and set a password
                            coroutineScope.launch {
                                try {
                                    val patient = repository.getPatientById(userId)

                                    if (patient != null) {
                                        // Check if the phone number matches
                                        if (patient.phoneNumber == phoneNumber) {
                                            // Update User Information
                                            patient.name = name
                                            patient.password = password
                                            repository.updatePatient(patient)

                                            // Save login status
                                            with(prefs.edit()) {
                                                putBoolean("is_logged_in", true)
                                                putString("user_id", userId)
                                                apply()
                                            }

                                            // Navigate to the questionnaire page
                                            navController.navigate("questionnaire") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        } else {
                                            errorMessage = "The phone number does not match, please try again"
                                        }
                                    } else {
                                        errorMessage = "User ID not found, please check your input"
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "error: ${e.message}"
                                } finally {
                                    isProcessing = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Complete your registration")
                        }
                    }
                }

                // Password login interface
                isPasswordLogin -> {
                    // User ID (read only)
                    OutlinedTextField(
                        value = userId,
                        onValueChange = { },
                        label = { Text("User ID") },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Password Visibility"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (password.isBlank()) {
                                errorMessage = "Please enter your password"
                                return@Button
                            }

                            isProcessing = true
                            errorMessage = ""

                            // Verify Password
                            coroutineScope.launch {
                                try {
                                    val result = repository.authenticateUser(userId, password)

                                    if (result != null) {
                                        // Save login status
                                        with(prefs.edit()) {
                                            putBoolean("is_logged_in", true)
                                            putString("user_id", userId)
                                            apply()
                                        }

                                        // Check if you have filled out the questionnaire
                                        val foodIntake = repository.getLatestFoodIntake(userId)

                                        if (foodIntake != null) {
                                            // Completed the questionnaire, navigated to the home page
                                            navController.navigate("home") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        } else {
                                            // The questionnaire has not been filled out. Navigate to the questionnaire page
                                            navController.navigate("questionnaire") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                    } else {
                                        errorMessage = "Wrong password, please try again"
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Error logging in: ${e.message}"
                                } finally {
                                    isProcessing = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Login")
                        }
                    }
                }

                // Initial login screen - enter user ID
                else -> {
                    OutlinedTextField(
                        value = userId,
                        onValueChange = { userId = it },
                        label = { Text("User ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            if (userId.isBlank()) {
                                errorMessage = "Please enter your user ID"
                                return@Button
                            }

                            isProcessing = true
                            errorMessage = ""

                            coroutineScope.launch {
                                try {
                                    val patient = repository.getPatientById(userId)

                                    if (patient != null) {
                                        // Find the user and check if the password has been set
                                        if (patient.password.isNullOrBlank()) {
                                            // The user exists but no password is set. Enter the first login process
                                            isFirstLogin = true
                                        } else {
                                            // The user has set a password, enter the password login process
                                            isPasswordLogin = true
                                        }
                                    } else {
                                        errorMessage = "User ID does not exist, please check your input"
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "An error occurred while checking user: ${e.message}"
                                } finally {
                                    isProcessing = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Next")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Simple mobile phone number verification function
private fun isValidPhoneNumber(phone: String): Boolean {
    // Simple check: 10-11 digits
    val phoneRegex = Regex("^\\d{10,11}$")
    return phoneRegex.matches(phone)
}