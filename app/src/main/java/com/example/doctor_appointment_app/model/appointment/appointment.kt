package com.example.doctor_appointment_app.model.appointment


data class Appointment(
    val id: String,
    val status: AppointmentStatus = AppointmentStatus.PENDING,
    val date: String,
    val time: String,
    val location: String,
    val area: String,
    val patientName: String,
    val patientAge: Int,
    val patientImage: String,
    val sessionPrice: Double,
    val currency: String = "DA"
)

enum class AppointmentStatus {
    CONFIRMED, PENDING, CANCELLED
}

// Response wrapper for API calls
data class AppointmentResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val errors: List<String>? = null
)