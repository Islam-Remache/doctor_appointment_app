package com.example.prescription_manag2.ui.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.prescription_manag2.PrescriptionAddApp
import com.example.prescription_manag2.PrescriptionDetailsApp
import com.example.prescription_manag2.PrescriptionListApp

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController, startDestination = Destination.PrescriptionListApp.route) {
        composable(Destination.PrescriptionListApp.route) { PrescriptionListApp(navController) }
        composable(Destination.PrescriptionAddApp.route) { PrescriptionAddApp(navController) }
        composable(Destination.PrescriptionDetailsApp.route) { backStackEntry ->
            val prescriptionId = backStackEntry.arguments?.getString("prescriptionId")?.toLongOrNull() ?: 0L
            PrescriptionDetailsApp(prescriptionId, navController)
        }
    }
}
