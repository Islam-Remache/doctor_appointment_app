package com.example.dzdoc.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dzdoc.R
import com.example.dzdoc.data.AppointmentStatus
import com.example.dzdoc.ui.model.PatientScreenAppointmentItem
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.util.Locale


@Composable
fun DoctorAppointmentCard(
    appointment: PatientScreenAppointmentItem,
    onReschedule: (PatientScreenAppointmentItem) -> Unit,
    onCancel: (PatientScreenAppointmentItem) -> Unit,
    onAppointmentClick: () -> Unit = {}
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    var showCancellationDialog by remember { mutableStateOf(false) }

    val statusColor = when (appointment.status) {
        AppointmentStatus.CONFIRMED -> Color(0xFF4CAF50)
        AppointmentStatus.PENDING -> Color(0xFF0B8FAC)
        AppointmentStatus.DECLINED -> Color(0xFFF44336)
        AppointmentStatus.COMPLETED -> Color(0xFF9C6E9F)
    }

    val statusText = appointment.status.name.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }


    if (showCancellationDialog) {
        ConfirmActionDialog(
            message = "Are you sure you want to cancel this appointment with ${appointment.doctorName}?",
            onDismiss = { showCancellationDialog = false },
            onConfirm = {
                onCancel(appointment)
                showCancellationDialog = false
            },
            confirmButtonText = "YES, CANCEL",
            dismissButtonText = "NO"
        )
    }

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable(onClick = onAppointmentClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(120.dp)
                .background(color = statusColor, shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp, topStart = 0.dp, bottomStart = 0.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Appointment date",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF93AFB5)
                    )


                    if (appointment.status == AppointmentStatus.PENDING ||
                        appointment.status == AppointmentStatus.CONFIRMED
                    ) {
                        Box {
                            IconButton(onClick = { isMenuExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More options",
                                    tint = Color(0xFF93AFB5)
                                )
                            }
                            DropdownMenu(
                                expanded = isMenuExpanded,
                                onDismissRequest = { isMenuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Reschedule", color = Color(0xFF0B8FAC), fontSize = 14.sp) },
                                    onClick = {
                                        onReschedule(appointment)
                                        isMenuExpanded = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Create,
                                            contentDescription = "Reschedule",
                                            tint = Color(0xFF0B8FAC),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Cancel Appointment", color = Color(0xFFF44336), fontSize = 14.sp) },
                                    onClick = {
                                        isMenuExpanded = false
                                        showCancellationDialog = true
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Cancel",
                                            tint = Color(0xFFF44336),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.clock),
                        contentDescription = "clock icon",
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(Color.Black)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = appointment.appointmentDate,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(" • ", fontSize = 14.sp, color = Color(0xFF93AFB5))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = appointment.appointmentTime,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp
                    )
                }

                HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))

                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(appointment.doctorPhotoUrl ?: R.drawable.doctor1)
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.doctor1),
                            error = painterResource(R.drawable.doctor1),
                            contentDescription = "Doctor: ${appointment.doctorName}",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f, fill = false)) {
                            Text(
                                text = appointment.doctorName,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            appointment.doctorSpecialty?.let { specialty ->
                                Text(
                                    text = specialty,
                                    color = Color(0xFF757575),
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = statusText.uppercase(),
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.Bottom)
                    )
                }
            }
        }
    }
}