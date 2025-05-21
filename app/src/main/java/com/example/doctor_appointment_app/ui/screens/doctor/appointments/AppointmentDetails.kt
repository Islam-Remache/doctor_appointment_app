package com.example.doctor_appointment_app.ui.screens.doctor.appointments

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.doctor_appointment_app.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.text.NumberFormat
import java.util.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


@Composable
fun AppointmentApp() {
    val navController = rememberNavController()
    val scrollState = rememberScrollState()
    Scaffold(

    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .verticalScroll(scrollState)
        ) {

            AppointmentDetailsScreen(navController)
        }
    }
}


data class AppointmentData(
    val status: AppointmentStatus = AppointmentStatus.PENDING,
    val date: String = "Monday 23 Mar 2025",
    val time: String = "08:00 AM",
    val location: String = "Imaginary street, number 25",
    val area: String = "Oued Smar, Alger",
    val patientName: String = "Mekhloufi Sami",
    val patientAge: Int = 26,
    val patientImage: String = "", // URL to image
    val sessionPrice: Double = 2500.00,
    val currency: String = "DA"
)

enum class AppointmentStatus {
    CONFIRMED, PENDING, CANCELLED
}


@Composable
fun AppointmentDetailsScreen(navController: NavController) {
    var appointmentData by remember { mutableStateOf(AppointmentData()) }
    var showCancellationDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
            val (backgroundColor, textColor) = when (appointmentData.status) {
                AppointmentStatus.CONFIRMED -> Color(0xFF4CAF50) to Color.White
                AppointmentStatus.PENDING -> Color(0xFF14A4C0) to Color.White
                AppointmentStatus.CANCELLED -> Color.Red to Color.White
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = backgroundColor,
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = appointmentData.status.name.lowercase()
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
                            text = appointmentData.date,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = appointmentData.time,
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
                            text = appointmentData.location,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = appointmentData.area,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }

                    // Edit button for Image 2 (pending status)
                    if (appointmentData.status == AppointmentStatus.PENDING) {
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
                    model = appointmentData.patientImage
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
                    text = appointmentData.patientName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "${appointmentData.patientAge} yo",
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

            Row(verticalAlignment = Alignment.CenterVertically) {



                Text(
                    text = NumberFormat
                        .getCurrencyInstance(Locale("ar", "DZ"))
                        .apply {
                            currency = Currency.getInstance("DZD")
                        }
                        .format(appointmentData.sessionPrice)
                        .replace("DZD", "DA"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF14A4C0)
                )
            }
        }

        // Bottom actions based on status
        when (appointmentData.status) {
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
                        onClick = {
                            appointmentData = appointmentData.copy(status = AppointmentStatus.CONFIRMED)
                        },
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
                        onClick = { showCancellationDialog = true },
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
                        appointmentData = appointmentData.copy(status = AppointmentStatus.CANCELLED)
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


