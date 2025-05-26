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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import com.fit2081.hulongxi33555397.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)

    // Using ViewModel
    val viewModel: LoginViewModel = viewModel()

    // Observing State from ViewModel
    val isProcessing by viewModel.isProcessing.observeAsState(false)
    val loginResult by viewModel.loginResult.observeAsState()
    val foodIntake by viewModel.foodIntake.observeAsState()
    val registrationSuccess by viewModel.registrationSuccess.observeAsState()
    val isUserRegistered by viewModel.isUserRegistered.observeAsState(false)
    val hasCompletedQuestionnaire by viewModel.hasCompletedQuestionnaire.observeAsState(false)

    // Local UI Status
    var errorMessage by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isFirstLogin by remember { mutableStateOf(false) }
    var isPasswordLogin by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }


    LaunchedEffect(registrationSuccess) {
        if (registrationSuccess == true) {
            with(prefs.edit()) {
                putBoolean("is_logged_in", true)
                putString("user_id", userId)
                apply()
            }

            // Directly determine navigation based on the questionnaire completion status
            if (hasCompletedQuestionnaire == true) {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            } else {
                navController.navigate("questionnaire") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
    }

    // Processing login results
    LaunchedEffect(loginResult, foodIntake, isUserRegistered) {
        loginResult?.let { patient ->
            if (isFirstLogin) {
                // After successfully setting the password for the first login
                if (patient.password?.isNotBlank() == true) {
                    // Update local status
                    isFirstLogin = false
                    isPasswordLogin = true

                    // Optional: Direct login success
                    with(prefs.edit()) {
                        putBoolean("is_logged_in", true)
                        putString("user_id", userId)
                        apply()
                    }

                    // Determine navigation direction based on whether there is a questionnaire survey
                    if (foodIntake != null) {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        navController.navigate("questionnaire") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            } else if (isPasswordLogin) {
                // Password login successful, save login status
                with(prefs.edit()) {
                    putBoolean("is_logged_in", true)
                    putString("user_id", userId)
                    apply()
                }

                // Determine navigation direction based on whether there is a questionnaire survey
                if (foodIntake != null) {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    navController.navigate("questionnaire") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            } else {
                // Initial check of user status
                if (isUserRegistered) {
                    // The user has registered, enter the password login process
                    isPasswordLogin = true
                    isFirstLogin = false
                } else {
                    // The user is not registered, enter the first registration process
                    isFirstLogin = true
                    isPasswordLogin = false
                }
            }
        }
    }

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
                                isFirstLogin = false
                                phoneNumber = ""
                                name = ""
                                password = ""
                                confirmPassword = ""
                            }
                            isPasswordLogin -> {
                                isPasswordLogin = false
                                password = ""
                            }
                            else -> {
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
                            android.util.Log.d("LoginScreen", "Click the Complete Registration button")

                            // Client Validation
                            if (name.isBlank()) {
                                viewModel.setErrorMessage("Please enter your name")
                                return@Button
                            }

                            if (password.length < 6) {
                                viewModel.setErrorMessage("Password must be at least 6 characters long")
                                return@Button
                            }

                            if (password != confirmPassword) {
                                viewModel.setErrorMessage("The passwords you entered twice do not match")
                                return@Button
                            }

                            if (!isValidPhoneNumber(phoneNumber)) {
                                viewModel.setErrorMessage("Please enter a valid mobile number")
                                return@Button
                            }

                            // Handling first login using ViewModel
                            viewModel.setFirstTimeUserInfo(userId, name, password, phoneNumber)

                            // Adding visual feedback
                            android.util.Log.d("LoginScreen", "Registration request sent")
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
                                return@Button
                            }

                            // Using ViewModel to validate password
                            viewModel.authenticateUser(userId, password)
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
                    Text(
                        text = "Please enter your user ID to continue",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "This app is only for pre-registered users. Please enter\n" +
                                "your ID, phone number and password to claim your\n" +
                                "account.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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

                            // Use ViewModel to check if the user exists and is registered
                            viewModel.checkIfUserExists(userId)
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
    val phoneRegex = Regex("^\\d{10,11}$")
    return phoneRegex.matches(phone)
}