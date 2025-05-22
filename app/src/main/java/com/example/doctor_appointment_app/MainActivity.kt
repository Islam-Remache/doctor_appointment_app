package com.example.prescription_manag2

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.example.prescription_manag2.ui.navigation.Navigation
import com.example.prescription_manag2.ui.theme.Prescription_manag2Theme
import com.example.prescription_manag2.utils.NetworkConnectionObserver

class MainActivity : ComponentActivity() {
    private lateinit var networkConnectionObserver: NetworkConnectionObserver
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkConnectionObserver = NetworkConnectionObserver(this)
        networkConnectionObserver.register()
        enableEdgeToEdge()
        setContent {
            Prescription_manag2Theme {
                val navController = rememberNavController()
                Scaffold(
                    containerColor = Color.White
                ) { innerPadding ->
                        Navigation(navController)
                }
            }
        }
    }

}

