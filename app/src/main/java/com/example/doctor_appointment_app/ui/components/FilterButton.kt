package com.example.dzdoc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dzdoc.data.AppointmentStatus

@Composable
fun FilterButton(
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color(0xFF0B8FAC))
            .clickable { onFilterClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Filter",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun FilterDialog(
    selectedStatuses: Set<AppointmentStatus>,
    onStatusToggle: (AppointmentStatus) -> Unit,
    onDismissRequest: () -> Unit,
    onClearFilters: () -> Unit,
    onApplyFilters: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Filter by Status", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Select appointment status", color = Color(0xFF93AFB5))
                Spacer(modifier = Modifier.height(16.dp))

                StatusFilterOption(
                    text = "Confirmed",
                    isSelected = selectedStatuses.contains(AppointmentStatus.CONFIRMED),
                    color = Color(0xFF4CAF50),
                    onClick = { onStatusToggle(AppointmentStatus.CONFIRMED) }
                )

                StatusFilterOption(
                    text = "Pending",
                    isSelected = selectedStatuses.contains(AppointmentStatus.PENDING),
                    color = Color(0xFF0B8FAC),
                    onClick = { onStatusToggle(AppointmentStatus.PENDING) }
                )

                StatusFilterOption(
                    text = "Declined",
                    isSelected = selectedStatuses.contains(AppointmentStatus.DECLINED),
                    color = Color(0xFFF44336),
                    onClick = { onStatusToggle(AppointmentStatus.DECLINED) }
                )

                StatusFilterOption(
                    text = "Completed",
                    isSelected = selectedStatuses.contains(AppointmentStatus.COMPLETED),
                    color = Color(0xFF9C6E9F),
                    onClick = { onStatusToggle(AppointmentStatus.COMPLETED) }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onApplyFilters,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B8FAC))
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onClearFilters) {
                Text("Clear All", color = Color(0xFF93AFB5))
            }
        }
    )
}

@Composable
fun StatusFilterOption(
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
                .background(if (isSelected) color else Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Text(
            text = text,
            modifier = Modifier.padding(start = 16.dp),
            color = if (isSelected) color else Color(0xFF93AFB5),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}