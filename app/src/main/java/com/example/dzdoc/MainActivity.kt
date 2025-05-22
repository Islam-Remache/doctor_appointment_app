package com.example.dzdoc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.dzdoc.navigation.AppNavigation
import com.example.dzdoc.ui.MainScaffold
import com.example.dzdoc.ui.theme.DzDocTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DzDocTheme {
                val navController = rememberNavController()
                MainScaffold(navController = navController) { paddingModifier ->
                    AppNavigation(navController = navController)
                }
            }
        }
    }
}