package com.fit2081.hulongxi33555397

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun WelcomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        // Add top blank space to shift content down
        Spacer(modifier = Modifier.height(128.dp))

        // Top title
        Text(
            text = "NutriTrack",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Add logo image
        Image(
            painter = painterResource(id = R.drawable.nutritrack_logo),
            contentDescription = "NutriTrack Logo",
            modifier = Modifier
                .size(240.dp)
                .padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Disclaimer in the middle
        Text(
            text = "This app provides general health and nutrition information for " +
                    "educational purposes only. It is not intended as medical advice, " +
                    "diagnosis, or treatment. Always consult a qualified healthcare " +
                    "professional before making any changes to your diet, exercise, or " +
                    "health regimen.\n" +
                    "Use this app at your own risk.\n" +
                    "If you'd like to an Accredited Practicing Dietitian (APD), please " +
                    "visit the Monash Nutrition/Dietetics Clinic (discounted rates for " +
                    "students):\n" +
                    "https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        // Login button
        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Footer signature
        Text(
            text = "Designed with ❤ by HU LONGXI (33555397)",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}