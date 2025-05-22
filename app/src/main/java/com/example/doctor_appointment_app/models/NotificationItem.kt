package com.example.dzdoc.ui.model


import com.example.dzdoc.ui.model.NotificationType
data class NotificationItem(
    val id: Int,
    val message: String,
    val title: String,
    val timestamp: String,
    val isRead: Boolean,
    val type: NotificationType
)