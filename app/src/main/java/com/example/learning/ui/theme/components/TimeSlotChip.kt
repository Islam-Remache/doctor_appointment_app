package com.example.learning.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.learning.model.TimeSlot
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeSlotChip(
    slot: TimeSlot,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val startTimeFormatted = try {
        LocalTime.parse(slot.start_time, DateTimeFormatter.ISO_LOCAL_TIME)
            .format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (e: DateTimeParseException) {
        try {
            LocalTime.parse(slot.start_time, DateTimeFormatter.ofPattern("HH:mm"))
                .format(DateTimeFormatter.ofPattern("HH:mm"))
        } catch (e2: DateTimeParseException) {
            slot.start_time
        }
    }


    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        modifier = Modifier.wrapContentWidth()
    ) {
        Text(startTimeFormatted, style = MaterialTheme.typography.labelMedium)
    }
}