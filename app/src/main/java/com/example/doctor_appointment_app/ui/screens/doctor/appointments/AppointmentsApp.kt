package com.example.doctor_appointment_app.ui.screens.doctor.appointments

import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun AppointmentsApp() {
    val navController = rememberNavController()

    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = "appointment_details"
    ) {
        composable("appointment_details") {
            AppointmentDetailsScreen(navController)
        }

        composable("qr_code") {
            QRCodeScannerScreen(navController)
        }
    }
}