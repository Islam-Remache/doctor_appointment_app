package com.example.learning.ui.navigation

import android.os.Build
import android.util.Log // For placeholder click
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.learning.ui.screens.DoctorDetailScreen
import com.example.learning.ui.screens.DoctorsListScreen
import com.example.learning.viewmodel.DoctorDetailViewModel
import com.example.learning.viewmodel.DoctorDetailViewModelFactory


object AppDestinations {
    const val DOCTORS_LIST_ROUTE = "doctorsList"
    const val DOCTOR_DETAIL_ROUTE = "doctorDetail"
    const val DOCTOR_ID_ARG = "doctorId"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.DOCTORS_LIST_ROUTE,
        modifier = modifier
    ) {
        composable(AppDestinations.DOCTORS_LIST_ROUTE) {
            DoctorsListScreen(
                onDoctorClick = { doctorId ->
                    navController.navigate("${AppDestinations.DOCTOR_DETAIL_ROUTE}/$doctorId")
                }
            )
        }

        composable(
            route = "${AppDestinations.DOCTOR_DETAIL_ROUTE}/{${AppDestinations.DOCTOR_ID_ARG}}",
            arguments = listOf(
                navArgument(AppDestinations.DOCTOR_ID_ARG) { type = NavType.IntType }
            )
        ) {
            val detailViewModel: DoctorDetailViewModel = viewModel(
                factory = DoctorDetailViewModelFactory()
            )

            DoctorDetailScreen(
                viewModel = detailViewModel,
                onBackClick = { navController.popBackStack() },
                onViewAppointmentsClick = {

                    Log.d("AppNavigation", "View My Appointments clicked - Navigation destination not yet implemented.")
                }
            )
        }
    }
}