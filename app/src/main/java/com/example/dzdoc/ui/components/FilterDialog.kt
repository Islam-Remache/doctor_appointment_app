package com.example.dzdoc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dzdoc.data.AppointmentStatus
import java.util.Locale

@Composable
fun FilterDialog(
    allAvailableStatuses: Set<AppointmentStatus>,
    selectedStatuses: Set<AppointmentStatus>,
    onStatusToggle: (AppointmentStatus) -> Unit,
    onDismissRequest: () -> Unit,
    onClearFilters: () -> Unit,
    onApplyFilters: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Filter by Status", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column {
                Text("Select appointment status", color = Color(0xFF93AFB5), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))

                val statusColors = mapOf(
                    AppointmentStatus.CONFIRMED to Color(0xFF4CAF50),
                    AppointmentStatus.PENDING to Color(0xFF0B8FAC),
                    AppointmentStatus.DECLINED to Color(0xFFF44336),
                    AppointmentStatus.COMPLETED to Color(0xFF9C6E9F)
                )

                allAvailableStatuses.sortedBy { it.name }.forEach { status ->
                    val statusName = status.name.lowercase(Locale.getDefault())
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

                    StatusFilterOptionItem(
                        text = statusName,
                        isSelected = selectedStatuses.contains(status),
                        color = statusColors[status] ?: Color.Gray,
                        onClick = { onStatusToggle(status) }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApplyFilters()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B8FAC))
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onClearFilters()
            }) {
                Text("Clear All", color = Color(0xFF93AFB5))
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}

@Composable
private fun StatusFilterOptionItem(
    text: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isSelected) color else Color.LightGray.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "$text selected",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Text(
            text = text,
            modifier = Modifier.padding(start = 16.dp),
            color = if (isSelected) color else Color(0xFF757575),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 15.sp
        )
    }
}