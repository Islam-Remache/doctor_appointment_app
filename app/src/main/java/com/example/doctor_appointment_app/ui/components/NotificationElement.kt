package com.example.doctor_appointment_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doctor_appointment_app.model.notifications.NotificationType

data class NotificationMeta(
    val color: Color,
    val icon: ImageVector
)

object NotificationStyle {
    val typeToMeta: Map<NotificationType, NotificationMeta> = mapOf(
        NotificationType.CANCELLED to NotificationMeta(Color(0xFFE53935), Icons.Default.Close),
        NotificationType.RESCHEDULED to NotificationMeta(Color(0xFF039BE5), Icons.Outlined.Refresh),
        NotificationType.ACCEPTED to NotificationMeta(Color(0xFF43A047), Icons.Default.Check),
        NotificationType.DECLINED to NotificationMeta(Color(0xFFE53935), Icons.Default.Close),
        NotificationType.UPCOMING to NotificationMeta(Color(0xFF8E24AA), Icons.Outlined.Refresh),
        NotificationType.PRESCRIPTION to NotificationMeta(Color(0xFFFFA000), Icons.Outlined.Notifications),
    )

    fun getMeta(type: NotificationType): NotificationMeta {
        return typeToMeta[type] !!
    }
}


@Composable
fun NotificationElement(
    notification: com.example.doctor_appointment_app.model.notifications.NotificationItem,
    onDismiss: () -> Unit,
    onRead: () -> Unit
) {
    val color = NotificationStyle.getMeta(notification.type).color
    val icon = NotificationStyle.getMeta(notification.type).icon

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE6F7EC)) // Light green background
    ) {
        // Vertical colored line on the left
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(80.dp)
                .background(color)
                .align(Alignment.CenterStart)
        )

        // Dismiss (X) button at top right
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Dismiss",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }

        // Main content
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 40.dp, top = 16.dp, bottom = 16.dp), // leave space for X button
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Texts
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "notification.title",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = notification.timestamp,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    color = Color.DarkGray,
                    fontSize = 14.sp
                )
            }
        }
    }
}
