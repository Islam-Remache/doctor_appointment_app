package com.example.learning.model
// Matches the backend model structure for time slots
data class TimeSlot(
    val id: Int,
    val doctor_id: Int,
    val date: String,
    val start_time: String,
    val end_time: String,
    val status: String
)