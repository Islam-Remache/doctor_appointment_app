package com.example.dzdoc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dzdoc.ui.components.AppHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorViewPatientDetailsScreen(
    navController: NavController,
    patientId: Int,
    patientName: String
) {
    Scaffold(
        topBar = {
            AppHeader(
                title = "Patient: $patientName",
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Details for Patient ID: $patientId", style = MaterialTheme.typography.headlineSmall)
            Text("Name: $patientName", style = MaterialTheme.typography.titleMedium)
            Text("More patient-specific details (medical history, etc.) would be loaded here.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
