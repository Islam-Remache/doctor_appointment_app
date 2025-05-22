package com.example.dzdoc.ui.screens

import android.widget.Toast // For simple feedback, consider Snackbar
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
// Removed: import androidx.compose.runtime.livedata.observeAsState // No longer needed
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.dzdoc.ui.model.Appointment
import com.example.dzdoc.ui.model.AppointmentStatus
import com.example.dzdoc.ui.viewmodel.AppointmentViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun AppointmentDetailsScreen(
    navController: NavController,
    appointmentId: Int,
    viewModel: AppointmentViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val actionSuccess: Boolean? by viewModel.actionSuccessEvent.collectAsState()
    val errorMessage: String? by viewModel.errorMessage.collectAsState()

    var showCancellationDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current // For Toasts or Snackbars

    // Load appointment data when screen is created
    LaunchedEffect(appointmentId) {
        viewModel.getAppointmentDetails(appointmentId)
    }

    // Handle action success
    LaunchedEffect(actionSuccess) {
        if (actionSuccess == true) {
            Toast.makeText(context, "Action successful!", Toast.LENGTH_SHORT).show()
            // Optionally navigate back or refresh
            // navController.popBackStack()
            viewModel.consumeActionSuccessEvent() // Reset the event in ViewModel
        }
    }

    // Show error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            Toast.makeText(context, "Error: $msg", Toast.LENGTH_LONG).show()
            // Consider using a SnackbarHost for better UX
            viewModel.clearErrorMessage() // Clear the error in ViewModel after showing
        }
    }

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
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
                    Text("Error: ${state.message}")
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
                val appointment = state.appointment
                AppointmentDetailsContent(
                    appointment = appointment,
                    navController = navController,
                    scrollState = scrollState,
                    onAcceptClick = {
                        viewModel.confirmAppointment(appointmentId, 1) // Assuming '1' is doctorId or similar
                    },
                    onCancelClick = {
                        showCancellationDialog = true
                    }
                )

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
                                    viewModel.cancelAppointment(appointmentId, 1) // Assuming '1' is doctorId
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
                // You can show a placeholder or just let it be blank until Loading or Success/Error
                // Text("Initializing...", modifier = Modifier.align(Alignment.Center))
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

            val (backgroundColor, textColor) = when (appointment.status) {
                AppointmentStatus.CONFIRMED -> Color(0xFF4CAF50) to Color.White
                AppointmentStatus.PENDING -> Color(0xFF14A4C0) to Color.White
                AppointmentStatus.DECLINED -> Color.Red to Color.White
                AppointmentStatus.COMPLETED -> Color.DarkGray to Color.White // Adjusted completed color
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = backgroundColor,
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = appointment.status.name.lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
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
            Image(
                painter = rememberAsyncImagePainter(model = appointment.patientImage),
                contentDescription = "Patient Photo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
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
                    .apply { currency = Currency.getInstance("DZD") }
                    .format(appointment.sessionPrice)
                    .replace("DZD", "DA "), // Added space
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF14A4C0)
            )
        }

        when (appointment.status) {
            AppointmentStatus.CONFIRMED -> {
                Button(
                    onClick = { navController.navigate("qr_code") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14A4C0)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = "QR Code",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Scan QR Code", Modifier.padding(vertical = 8.dp))
                }
            }
            AppointmentStatus.PENDING -> {
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
                       /* border = ButtonDefaults.outlinedBorder.copy( // Corrected to outlinedBorder
                            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF14A4C0))
                        ) */
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
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
            AppointmentStatus.DECLINED, AppointmentStatus.COMPLETED -> {
                // No specific actions for these states by default, can add a message
                // Text("This appointment is ${appointment.status.name.lowercase()}.",
                //    modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
                //    textAlign = TextAlign.Center
                // )
            }
        }
    }
}