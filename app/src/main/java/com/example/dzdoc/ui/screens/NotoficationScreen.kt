package com.example.doctor_appointment_app.ui.screens.common.notifications

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dzdoc.ui.components.NotificationElement
import com.example.dzdoc.ui.viewmodel.NotificationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel,
    doctorId: Int
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 for All, 1 for Unread
    val scope = rememberCoroutineScope()
    val notifications = viewModel.notifications
    val unreadCount = viewModel.unreadCount
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Notifications",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Row {
                // Refresh button
                IconButton(
                    onClick = { viewModel.loadNotifications() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE1F5FE))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Refresh notifications",
                        tint = Color(0xFF03A9F4)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Notifications icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE1F5FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = Color(0xFF03A9F4)
                    )
                }
            }
        }

        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50.dp))
                .background(Color(0xFFD2EBE7))
                .padding(1.dp)
        ) {
            TabButton(
                text = "All",
                isSelected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                modifier = Modifier.weight(1f)
            )

            TabButton(
                text = "$unreadCount Unread",
                isSelected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Error message
        error?.let { errorMessage ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFB71C1C)
                    )
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("Dismiss")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Loading indicator or content
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading && notifications.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF03A9F4)
                )
            } else {
                // Notification list
                if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No notifications found",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val filteredNotifications = if (selectedTab == 0) {
                            notifications
                        } else {
                            notifications.filter { !it.isRead }
                        }

                        items(
                            items = filteredNotifications,
                            key = { it.id }
                        ) { notification ->
                            var isVisible by remember { mutableStateOf(true) }

                            androidx.compose.animation.AnimatedVisibility(
                                visible = isVisible,
                                exit = shrinkVertically(
                                    animationSpec = tween(durationMillis = 300)
                                ) + fadeOut()
                            ) {
                                // Wrap NotificationElement with clickable modifier for the whole item
                                Box(
                                    modifier = Modifier
                                        .clickable {
                                            if (!notification.isRead) {
                                                viewModel.markAsRead(notification.id)
                                            }
                                        }
                                ) {
                                    // Apply shadow only to unread notifications
                                    NotificationElement(
                                        notification = notification,
                                        modifier = Modifier
                                            .then(
                                                if (!notification.isRead) {
                                                    Modifier
                                                        .shadow(
                                                            elevation = 12.dp,
                                                            shape = RoundedCornerShape(12.dp)
                                                        )

                                                } else {
                                                    Modifier
                                                }
                                            ),
                                        onDismiss = {
                                            scope.launch {
                                                isVisible = false
                                                delay(300) // Wait for animation to complete
                                                viewModel.removeNotification(notification.id)
                                            }
                                        },
                                        onRead = {
                                            viewModel.markAsRead(notification.id)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(
                if (isSelected) Color(0xFF0B8FAC) else Color.Transparent,
                RoundedCornerShape(50.dp)
            )
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.DarkGray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}
