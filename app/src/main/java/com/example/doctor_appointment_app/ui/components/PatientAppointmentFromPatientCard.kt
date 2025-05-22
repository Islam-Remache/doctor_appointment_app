package com.example.dzdoc.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.dzdoc.R
import com.example.dzdoc.data.AppointmentStatus
import com.example.dzdoc.ui.model.DoctorScreenAppointmentItem

@Composable
fun PatientAppointmentFromPatientCard(
    appointmentItem: DoctorScreenAppointmentItem,
    onAccept: (DoctorScreenAppointmentItem) -> Unit,
    onDecline: (DoctorScreenAppointmentItem) -> Unit,
    onCardClick: (DoctorScreenAppointmentItem) -> Unit
) {
    var showDeclineDialog by remember { mutableStateOf(false) }

    val statusColor = when (appointmentItem.appointmentStatus) {
        AppointmentStatus.CONFIRMED -> Color(0xFF4CAF50)
        AppointmentStatus.PENDING -> Color(0xFF0B8FAC)
        AppointmentStatus.COMPLETED -> Color(0xFF9C6E9F)
        AppointmentStatus.DECLINED -> Color(0xFFF44336)
    }
    val statusText = appointmentItem.rawStatusString.replaceFirstChar { it.titlecase() }

    if (showDeclineDialog) {
        DeclineAppointmentDialog(
            patientName = appointmentItem.patientName,
            onDismiss = { showDeclineDialog = false },
            onConfirmDecline = {
                onDecline(appointmentItem)
                showDeclineDialog = false
            }
        )
    }

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onCardClick(appointmentItem) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(100.dp)
                .background(color = statusColor, shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp, topStart = 0.dp, bottomStart = 0.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Appointment date", fontSize = 12.sp, color = Color(0xFF93AFB5))
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(id = R.drawable.clock), "Time", modifier = Modifier.size(14.dp), tint = Color.DarkGray)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "${appointmentItem.appointmentDate}  •  ${appointmentItem.appointmentTime}",
                        fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE0E0E0))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(0.6f, fill = false)) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(appointmentItem.patientPhotoUrl ?: R.drawable.doctor2)
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.doctor2),
                            error = painterResource(R.drawable.doctor2),
                            contentDescription = "Patient: ${appointmentItem.patientName}",
                            modifier = Modifier.size(40.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                appointmentItem.patientName,
                                fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Black,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                            appointmentItem.reasonForVisit?.let {
                                Text(it, fontSize = 12.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    if (appointmentItem.appointmentStatus == AppointmentStatus.PENDING) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onAccept(appointmentItem) }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Filled.Check, "Accept Appointment", tint = Color(0xFF0B8FAC))
                            }
                            IconButton(onClick = { showDeclineDialog = true }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Filled.Close, "Decline Appointment", tint = Color(0xFFF44336))
                            }
                        }
                    } else {
                        Text(statusText.uppercase(), color = statusColor, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
                if (appointmentItem.appointmentStatus == AppointmentStatus.PENDING) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Text(statusText.uppercase(), color = statusColor, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}