package com.example.dzdoc.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.dzdoc.navigation.AppDestination
import com.example.dzdoc.navigation.BottomNavigation

@Composable
fun MainScaffold(
    navController: NavHostController,
    content: @Composable (Modifier) -> Unit
) {

    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    val shouldShowBottomNav = when (currentRoute) {
        AppDestination.AppointmentsList.route,
        AppDestination.PatientsAppointmentsList.route -> true
        else -> false
    }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomNav) {
                BottomNavigation(navController = navController)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(
                bottom = if (shouldShowBottomNav) innerPadding.calculateBottomPadding() else innerPadding.calculateBottomPadding()
            )
        ) {
            content(Modifier)
        }
    }
}