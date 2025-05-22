package com.example.dzdoc.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dzdoc.data.AppointmentStatus
import com.example.dzdoc.ui.components.AppHeader
import com.example.dzdoc.ui.components.CancellationDialogAppointement // Your OG Dialog
import com.example.dzdoc.ui.viewmodel.AppointmentDetailsUiState
import com.example.dzdoc.ui.viewmodel.AppointmentCancellationEvent
import com.example.dzdoc.ui.viewmodel.PatientAppointmentDetailsViewModel
// ... other imports from your last correct version of this file ...
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.dzdoc.R
import com.example.dzdoc.navigation.AppDestination
import com.example.dzdoc.ui.components.CancellationDialogAppointement
import com.example.dzdoc.ui.components.StatusChip
import java.util.Locale


@Composable
fun PatientAppointmentDetailsScreen(
    navController: NavController,
    viewModel: PatientAppointmentDetailsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val cancellationEvent by viewModel.cancellationEvent.collectAsState(initial = AppointmentCancellationEvent.Idle)

    var showActualCancelDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(cancellationEvent) {
        when (val event = cancellationEvent) {
            is AppointmentCancellationEvent.Success -> {
                Toast.makeText(context, "Appointment cancelled successfully", Toast.LENGTH_SHORT).show()
                navController.previousBackStackEntry?.savedStateHandle?.set("appointment_cancelled", true)
                navController.popBackStack()
            }
            is AppointmentCancellationEvent.Error -> {
                Toast.makeText(context, "Cancellation failed: ${event.message}", Toast.LENGTH_LONG).show()
            }

            else -> {}
        }
    }

    if (showActualCancelDialog && uiState is AppointmentDetailsUiState.Success) {
        val appointmentToCancel = (uiState as AppointmentDetailsUiState.Success).appointment
        CancellationDialogAppointement(
            itemDescription = "appointment with ${appointmentToCancel.doctorName}",
            onDismiss = { showActualCancelDialog = false },
            onConfirm = {
                viewModel.cancelAppointment()
                showActualCancelDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 24.dp, bottom = 20.dp)
    ) {
        AppHeader(title = "Appointment Details", showBackButton = true, onBackClick = { navController.popBackStack() })
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (val state = uiState) {
                is AppointmentDetailsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                }
                is AppointmentDetailsUiState.Success -> {
                    val appointment = state.appointment
                    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Booking Info", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0A0909))
                            StatusChip(status = appointment.rawStatusString.uppercase())
                        }
                        Spacer(modifier = Modifier.height(21.dp))
                        Box(modifier = Modifier.fillMaxWidth().background(color = Color(0xFFE9EFF0), shape = RoundedCornerShape(12.dp))) {
                            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 17.dp, vertical = 15.dp)) {
                                InfoRow(icon = Icons.Default.DateRange, iconDescription = "Date & Time Icon", title = "Date & Time", line1 = appointment.appointmentDate, line2 = appointment.appointmentTime)
                                Spacer(modifier = Modifier.height(21.dp))
                                HorizontalDivider(thickness = 1.dp, color = Color(0xFFCDD5D8))
                                Spacer(modifier = Modifier.height(21.dp))
                                InfoRow(icon = Icons.Default.Place, iconDescription = "Location Icon", title = "Appointment location", line1 = appointment.healthInstitutionAddress ?: "Location not available", showMapButton = true, onMapButtonClick = { /* TODO */ })
                            }
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                        Text("Doctor Info", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0A0909))
                        Spacer(modifier = Modifier.height(21.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(appointment.doctorPhotoUrl ?: R.drawable.doctor1).crossfade(true).build(), placeholder = painterResource(R.drawable.doctor1), error = painterResource(R.drawable.doctor1), contentDescription = "Doctor: ${appointment.doctorName}", modifier = Modifier.size(50.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(appointment.doctorName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0A0909))
                                appointment.doctorSpecialty?.let { Text(it, fontSize = 14.sp, color = Color(0xFF9EA7B5)) }
                            }
                        }
                        Spacer(modifier = Modifier.height(21.dp))
                        Spacer(modifier = Modifier.height(25.dp))

                        ActionButtonsSection(
                            status = appointment.status,
                            navController = navController,
                            appointmentId = appointment.id.toString(),
                            onRescheduleClick = {  },
                            onCancelClick = {
                                if (appointment.status == AppointmentStatus.PENDING || appointment.status == AppointmentStatus.CONFIRMED) {
                                    showActualCancelDialog = true
                                } else {
                                    Toast.makeText(context, "This appointment cannot be cancelled.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                is AppointmentDetailsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { Text("Error: ${state.message}",
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}


@Composable
private fun InfoRow(icon: ImageVector, iconDescription: String, title: String, line1: String, line2: String? = null, showMapButton: Boolean = false, onMapButtonClick: () -> Unit = {}) {
    Row(verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.size(44.dp).background(color = Color.White, shape = CircleShape), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = iconDescription, tint = Color(0xFF0B8FAC), modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(17.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0A0909))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = line1, fontSize = 13.sp, color = Color(0xFF9EA7B5))
            line2?.let { Spacer(modifier = Modifier.height(8.dp)); Text(text = it, fontSize = 13.sp, color = Color(0xFF9EA7B5)) }
            if (showMapButton) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.background(color = Color(0xFF0B8FAC), shape = RoundedCornerShape(4.dp)).clickable { onMapButtonClick() }.padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("See the Map", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    Spacer(modifier = Modifier.width(7.dp))
                    Box(modifier = Modifier.size(20.dp).background(color = Color.White, shape = CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.PlayArrow, "See Map logo", tint = Color(0xFF0B8FAC), modifier = Modifier.size(15.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButtonsSection(status: AppointmentStatus, navController: NavController, appointmentId: String, onRescheduleClick: () -> Unit, onCancelClick: () -> Unit) {
    val showReschedule: Boolean
    val showCancel: Boolean
    val showQRCode: Boolean

    when (status) {
        AppointmentStatus.PENDING -> { showReschedule = true; showCancel = true; showQRCode = false }
        AppointmentStatus.CONFIRMED -> { showReschedule = true; showCancel = true; showQRCode = true }
        AppointmentStatus.DECLINED -> { showReschedule = false; showCancel = false; showQRCode = false }
        AppointmentStatus.COMPLETED -> { showReschedule = false; showCancel = false; showQRCode = true }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (showReschedule || showCancel) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (showReschedule) {
                    ActionButtonItem(text = "Reschedule", icon = Icons.Default.Edit, backgroundColor = Color(0xFFE9EFF0), textColor = Color(0xFF0B8FAC), onClick = onRescheduleClick, modifier = Modifier.weight(1f))
                }
                if (showCancel) {
                    ActionButtonItem(text = "Cancel", icon = Icons.Default.Close, backgroundColor = Color(0xFFFFE0E0), textColor = Color(0xFFD32F2F), onClick = onCancelClick, modifier = Modifier.weight(1f))
                }
            }
        }
        if (showQRCode) {
            if(showReschedule || showCancel) Spacer(modifier = Modifier.height(16.dp))
            ActionButtonItem(text = "View QR Code", icon = Icons.Default.Info, backgroundColor = Color(0xFF0B8FAC), textColor = Color(0xFFFFFFFF), onClick = { navController.navigate(AppDestination.QRCode.createRoute(appointmentId)) }, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun ActionButtonItem(text: String, icon: ImageVector, backgroundColor: Color, textColor: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 14.dp, horizontal = 12.dp),
        modifier = modifier.height(IntrinsicSize.Min)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(imageVector = icon, contentDescription = "$text icon", tint = textColor)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}