package com.example.doctor_appointment_app.model.notifications

import com.example.doctor_appointment_app.model.notifications.NotificationType
data class NotificationItem(
    val id: Int,
    val message: String,
    val timestamp: String,
    val isRead: Boolean,
    val type: NotificationType
)