package com.example.doctor_appointment_app

import android.annotation.SuppressLint
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doctor_appointment_app.network.NetworkModule
import com.example.doctor_appointment_app.repository.NotificationRepository
import com.example.doctor_appointment_app.service.NotificationService
import com.example.doctor_appointment_app.ui.screens.common.notifications.NotificationScreen
import com.example.doctor_appointment_app.ui.theme.Doctor_appointment_appTheme
import com.example.doctor_appointment_app.viewmodel.NotificationViewModel

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Doctor_appointment_appTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    MainApp(navController,Modifier.padding(innerPadding))
                }
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


    }
}


