package com.example.learning.model
data class AppointmentCreateRequest(
    val patient_id: Int,
    val doctor_id: Int,
    val time_slot_id: Int
)