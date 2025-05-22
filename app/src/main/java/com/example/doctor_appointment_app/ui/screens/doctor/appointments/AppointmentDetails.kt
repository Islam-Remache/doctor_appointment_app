package com.example.doctor_appointment_app.ui.screens.doctor.appointments

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.doctor_appointment_app.model.appointment.Appointment
import com.example.doctor_appointment_app.model.appointment.AppointmentStatus
import com.example.doctor_appointment_app.viewmodel.AppointmentViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun AppointmentDetailsScreen(
    navController: NavController,
    appointmentId: Int,
    viewModel: AppointmentViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val actionSuccess by viewModel.actionSuccessEvent.observeAsState()
    val errorMessage by viewModel.errorMessage.observeAsState()
    var showCancellationDialog by remember { mutableStateOf(false) }

    // Load appointment data when screen is created
    LaunchedEffect(appointmentId) {
        viewModel.getAppointmentDetails(appointmentId)
    }

    // Handle action success
    LaunchedEffect(actionSuccess) {
        if (actionSuccess == true) {
            // Show toast or navigate back
        }
    }

    // Show error messages
    errorMessage?.let {
        LaunchedEffect(errorMessage) {
            // Show snackbar or toast with error message
        }
    }

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            is AppointmentViewModel.UiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF14A4C0)
                )
            }

            is AppointmentViewModel.UiState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Error: ${(uiState as AppointmentViewModel.UiState.Error).message}")
                    Button(
                        onClick = { viewModel.getAppointmentDetails(appointmentId) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF14A4C0)
                        )
                    ) {
                        Text("Retry")
                    }
                }
            }

            is AppointmentViewModel.UiState.Success -> {
                val appointment = (uiState as AppointmentViewModel.UiState.Success).appointment
                AppointmentDetailsContent(
                    appointment = appointment,
                    navController = navController,
                    scrollState = scrollState,
                    onAcceptClick = {
                        viewModel.confirmAppointment(appointmentId, 1)
                    },
                    onCancelClick = {
                        showCancellationDialog = true
                    }
                )

                // Cancellation confirmation dialog
                if (showCancellationDialog) {
                    AlertDialog(
                        onDismissRequest = { showCancellationDialog = false },
                        title = {
                            Text(
                                "Are you sure you want to cancel this patient's appointment?",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.cancelAppointment(appointmentId, 1)
                                    showCancellationDialog = false
                                }
                            ) {
                                Text(
                                    "YES",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showCancellationDialog = false }
                            ) {
                                Text(
                                    "NO",
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    )
                }
            }

            is AppointmentViewModel.UiState.Initial -> {
                // Just show empty state or nothing
            }
        }
    }
}

@Composable
fun AppointmentDetailsContent(
    appointment: Appointment,
    navController: NavController,
    scrollState: ScrollState,
    onAcceptClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Schedule",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = { /* Notification action */ },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.3f))
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.Black
                )
            }
        }

        // Back button and title
        Row(
            modifier = Modifier.padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Appointment Details",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF14A4C0)
            )
        }

        // Booking Info Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Booking Info",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // Status chip
            val (backgroundColor, textColor) = when (appointment?.status) {
                AppointmentStatus.CONFIRMED -> Color(0xFF4CAF50) to Color.White
                AppointmentStatus.PENDING -> Color(0xFF14A4C0) to Color.White
                AppointmentStatus.DECLINED -> Color.Red to Color.White
                AppointmentStatus.COMPLETED -> Color.Red to Color.White
                else -> Color.Red to Color.White
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = backgroundColor,
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = appointment.status.name.lowercase()
                        .replaceFirstChar { it.uppercase() },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = textColor
                )
            }
        }

        // Appointment Details Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF2F7F8)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Date & Time
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Calendar",
                            tint = Color(0xFF14A4C0)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Date & Time",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = appointment.date,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = appointment.time,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = Color.LightGray
                )

                // Location
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Location",
                            tint = Color(0xFF14A4C0)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Appointment location",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = appointment.location,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = appointment.area,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }

                    // Edit button for Image 2 (pending status)
                    if (appointment.status == AppointmentStatus.PENDING) {
                        IconButton(onClick = { /* Edit location */ }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color(0xFF14A4C0)
                            )
                        }
                    }
                }

                // Map button
                Button(
                    onClick = { /* Open map */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF14A4C0)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("See the Map", Modifier.padding(vertical = 4.dp))

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Map",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Patient Info Section
        Text(
            text = "Patient Info",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Patient image
            Image(
                painter = rememberAsyncImagePainter(
                    model = appointment.patientImage
                ),
                contentDescription = "Patient Photo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Patient details
            Column {
                Text(
                    text = appointment.patientName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "${appointment.patientAge} yo",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        // Payment Info Section
        Text(
            text = "Payment Info",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Session Price",
                fontSize = 16.sp
            )

            Text(
                text = NumberFormat
                    .getCurrencyInstance(Locale("ar", "DZ"))
                    .apply {
                        currency = Currency.getInstance("DZD")
                    }
                    .format(appointment.sessionPrice)
                    .replace("DZD", "DA"),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF14A4C0)
            )
        }

        // Bottom actions based on status
        when (appointment.status) {
            AppointmentStatus.CONFIRMED -> {
                // QR Code button
                Button(
                    onClick = { navController.navigate("qr_code") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF14A4C0)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = "QR Code",
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        "Scan QR Code",
                        Modifier.padding(vertical = 8.dp)
                    )
                }
            }
            AppointmentStatus.PENDING -> {
                // Accept/Cancel buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    OutlinedButton(
                        onClick = onAcceptClick,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF14A4C0)
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF14A4C0))
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Accept",
                            tint = Color(0xFF14A4C0)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text("Accept")
                    }

                    Button(
                        onClick = onCancelClick,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel"
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text("Cancel")
                    }
                }
            }
            else -> { /* No actions for cancelled status */ }
        }
    }
}