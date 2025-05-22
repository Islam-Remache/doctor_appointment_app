package com.example.doctor_appointment_app.model.notifications

import com.google.gson.annotations.SerializedName

enum class NotificationType {
    @SerializedName("CANCELLED") CANCELLED,
    @SerializedName("RESCHEDULED") RESCHEDULED,
    @SerializedName("ACCEPTED") ACCEPTED,
    @SerializedName("DECLINED") DECLINED,
    @SerializedName("UPCOMING") UPCOMING,
    @SerializedName("PRESCRIPTION") PRESCRIPTION
}