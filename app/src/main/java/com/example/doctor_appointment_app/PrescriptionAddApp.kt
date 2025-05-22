package com.example.prescription_manag2

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.prescription_manag2.ui.screens.doctor.DigitalPrescription
import com.example.prescription_manag2.viewmodel.PrescriptionViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PrescriptionAddApp(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as MyApplication
    val prescriptionViewModel = PrescriptionViewModel(application.prescriptionRepository)
    DigitalPrescription(prescriptionViewModel, context, navController)
}