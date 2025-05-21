package com.example.doctor_appointment_app.ui.navigation

sealed class Destination(val route:String) {
    object NotificationScreen : Destination("notifications")
}