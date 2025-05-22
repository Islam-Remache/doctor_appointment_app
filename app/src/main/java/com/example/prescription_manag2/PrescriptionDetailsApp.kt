package com.example.prescription_manag2

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.prescription_manag2.ui.screens.patient.PrescriptionDetailScreen
import com.example.prescription_manag2.viewmodel.PrescriptionViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PrescriptionDetailsApp(
    prescriptionId: Long,
    navController: NavController
) {
    val context = LocalContext.current
    val application = context.applicationContext as MyApplication
    val prescriptionViewModel = PrescriptionViewModel(application.prescriptionRepository)
    PrescriptionDetailScreen(prescriptionId, navController, context, prescriptionViewModel)
}