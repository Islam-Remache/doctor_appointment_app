package com.example.dzdoc.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.doctor_appointment_app.ui.screens.common.notifications.NotificationScreen
import com.example.dzdoc.data.remote.NotificationService
import com.example.dzdoc.data.remote.RetrofitInstance
import com.example.dzdoc.data.repository.AppointmentRepositoryImpl
import com.example.dzdoc.data.repository.NotificationRepository
import com.example.dzdoc.ui.screens.*
import com.example.dzdoc.ui.viewmodel.NotificationViewModel
import com.example.dzdoc.ui.viewmodel.PatientAppointmentDetailsViewModel
import com.example.dzdoc.ui.viewmodel.PatientAppointmentDetailsViewModelFactory

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.AppointmentsList.route
    ) {
        composable(route = AppDestination.AppointmentsList.route) {
            AppointmentsList(navController = navController)
        }

        composable(route = AppDestination.PatientsAppointmentsList.route) {
            PatientAppointmentsListScreen(navController = navController)
        }


        composable(
            route = AppDestination.PatientAppointmentDetails.route, // Patient views their appointment
            arguments = listOf(navArgument("appointmentId") { type = NavType.IntType })
        ) { backStackEntry ->
            val repository = remember { AppointmentRepositoryImpl(RetrofitInstance.api) }
            val savedStateHandle = backStackEntry.savedStateHandle.apply {
                set("appointmentId", backStackEntry.arguments?.getInt("appointmentId") ?: -1)
            }
            val detailsViewModel: PatientAppointmentDetailsViewModel = viewModel(
                factory = PatientAppointmentDetailsViewModelFactory(repository, savedStateHandle)
            )
            val appointmentIdFromArgs = backStackEntry.arguments?.getInt("appointmentId") ?: -1
            if (appointmentIdFromArgs != -1) {
                PatientAppointmentDetailsScreen(navController = navController, viewModel = detailsViewModel)
            } else {
                Text("Error: Invalid Appointment ID.")
            }
        }

        composable(
            route = AppDestination.DoctorViewPatientDetails.route,
            arguments = listOf(
                navArgument("patientId") { type = NavType.IntType },
                navArgument("patientName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getInt("patientId") ?: -1
            val patientName = backStackEntry.arguments?.getString("patientName") ?: "N/A"
            if (patientId != -1) {
                DoctorViewPatientDetailsScreen(
                    navController = navController,
                    patientId = patientId,
                    patientName = patientName
                )
            } else {
                Text("Error: Invalid Patient ID for details.")
            }
        }

        composable(
            route = AppDestination.QRCode.route,
            arguments = listOf(navArgument("appointmentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
            if (appointmentId.isNotEmpty()) {
                QRCodeScreen(appointmentId = appointmentId, onBackClick = { navController.popBackStack() })
            }
        }

        composable(
            route = AppDestination.Notifications.route,
            arguments = listOf(
                navArgument("doctorId") { type = NavType.IntType },
                navArgument("doctorName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getInt("doctorId") ?: -1
            val doctorName = backStackEntry.arguments?.getString("doctorName") ?: "N/A"
            if (doctorId != -1) {
                NotificationScreen(NotificationViewModel(NotificationRepository(RetrofitInstance.Notifapi)),doctorId)
            } else {
                Text("Error: Invalid Patient ID for details.")
            }
        }

    }
}