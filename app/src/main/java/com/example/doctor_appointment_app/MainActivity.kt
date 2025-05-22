package com.example.doctor_appointment_app

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import com.example.doctor_appointment_app.ui.navigation.Destination
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.doctor_appointment_app.network.NetworkModule
import com.example.doctor_appointment_app.repository.AppointmentRepository
import com.example.doctor_appointment_app.repository.NotificationRepository
import com.example.doctor_appointment_app.service.NotificationService
import com.example.doctor_appointment_app.ui.screens.common.notifications.NotificationScreen
import com.example.doctor_appointment_app.ui.screens.doctor.appointments.AppointmentDetailsScreen
import com.example.doctor_appointment_app.ui.theme.Doctor_appointment_appTheme
import com.example.doctor_appointment_app.viewmodel.AppointmentViewModel
import com.example.doctor_appointment_app.viewmodel.NotificationViewModel
import android.Manifest
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //requestNotificationPermission()
        setContent {
            Doctor_appointment_appTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    MainApp(navController,Modifier.padding(innerPadding))
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        // Check for Android 13+ (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Ask for permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001 // Arbitrary request code
                )
            }
        }
    }

}

@Composable
fun MainApp(navController: NavHostController, modifier: Modifier){
    NavHost (navController = navController, startDestination = Destination.NotificationScreen.route, modifier = modifier) {
        composable(Destination.NotificationScreen.route) {
            NotificationScreen(
                viewModel = NotificationViewModel(
                    NotificationRepository(
                        NetworkModule.provideNotificationService(NetworkModule.provideRetrofit(NetworkModule.provideHttpClient(),NetworkModule.provideConverterFactory()))
                    )
                )
            )
        }

        composable(
            route = Destination.AppointmentDetails.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getInt("id") ?: 0

            AppointmentDetailsScreen(
                navController = navController,
                appointmentId = appointmentId,
                viewModel = AppointmentViewModel(
                    AppointmentRepository(
                        NetworkModule.provideAppointmentService(
                            NetworkModule.provideRetrofit(
                                NetworkModule.provideHttpClient(),
                                NetworkModule.provideConverterFactory()
                            )
                        )
                    )
                )
            )
        }
    }


    }



