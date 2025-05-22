package com.example.prescription_manag2.ui.navigation

sealed class Destination(val route: String) {
    object PrescriptionDetailsApp : Destination("PrescriptionDetailsApp/{prescriptionId}") {
        fun createRoute(prescriptionId: Long) = "PrescriptionDetailsApp/$prescriptionId"
    }
    object PrescriptionAddApp : Destination("PrescriptionAddApp")
    object PrescriptionListApp : Destination("PrescriptionListApp")


}