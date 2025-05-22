package com.example.doctor_appointment_app.model.appointment

import com.google.gson.annotations.SerializedName


data class Appointment(
    val id: Int,
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
    @SerializedName("pending") PENDING,
    @SerializedName("confirmed") CONFIRMED,
    @SerializedName("declined") DECLINED,
    @SerializedName("completed") COMPLETED
}

// Response wrapper for API calls
data class AppointmentResponse<T>(
    val success: Boolean,
    val data: T,
    val message: String? = null,
    val errors: List<String>? = null
)