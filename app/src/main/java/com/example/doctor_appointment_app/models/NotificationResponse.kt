package com.example.dzdoc.ui.model

import com.example.dzdoc.ui.model.NotificationType
import java.util.*
import kotlin.math.abs
import java.text.SimpleDateFormat




data class NotificationItemDto(
    val id: Int,
    val message: String,
    val user_type: String,
    val title: String,// or use an enum if you prefer
    val user_id: Int,
    val is_read: Boolean,
    val sent_at: String,
    val type: NotificationType
)

data class NotificationResponseDto(
    val notifications: List<NotificationItemDto>,
    val total: Int,
    val unread_count: Int
)

fun NotificationItemDto.toDomainModel(): NotificationItem {
    return NotificationItem(
        id = id,
        message = message,
        title = title,
        timestamp = formatTimeDifference( sent_at),
        isRead = is_read,
        type = type
    )
}


fun formatTimeDifference(timeString: String): String {
    // If the input contains 'T' between date and time, we need to handle ISO 8601 format
    val adjustedTimeString = if (timeString.contains('T')) {
        timeString
    } else {
        // Replace space with 'T' to match ISO 8601 format
        timeString.replace(" ", "T")
    }

    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val targetDate = dateFormat.parse(adjustedTimeString)
    val currentDate = Date()

    // Calculate absolute difference in milliseconds
    val diffInMillis = abs(targetDate.time - currentDate.time)

    // Convert to various units
    val seconds = diffInMillis / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> "$days d"
        hours > 0 -> "$hours h"
        minutes > 0 -> "$minutes m"
        else -> "$seconds s"
    }
}
