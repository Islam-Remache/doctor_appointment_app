package com.example.learning.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items // For LazyRow/LazyColumn items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as lazyGridItems
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.learning.R

import com.example.learning.model.TimeSlot // Still needed for TimeSlotChip

import com.example.learning.viewmodel.DoctorDetailViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.format.TextStyle as JavaTextStyle

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDetailScreen(
    viewModel: DoctorDetailViewModel,
    onBackClick: () -> Unit,
    onViewAppointmentsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    if (uiState.isLoadingInitialData) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val doctor = uiState.doctor
    if (doctor == null) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(
                    text = uiState.errorMessages.firstOrNull() ?: "Doctor details not found.",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.retryInitialLoad() }) {
                    Text("Retry")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onBackClick) {
                    Text("Go Back")
                }
            }
        }
        return
    }


    val specialtyName = uiState.specialtyName
    val healthInstitution = uiState.healthInstitution


    LaunchedEffect(uiState.bookingMessage) {
        uiState.bookingMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearBookingMessage() // Acknowledge the message
        }
    }
    LaunchedEffect(key1 = uiState.errorMessages) {
        uiState.errorMessages.firstOrNull { !it.contains("Doctor not found", ignoreCase = true) }?.let { errorMessage ->
            if (snackbarHostState.currentSnackbarData == null) {
                snackbarHostState.showSnackbar(errorMessage)

            }
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValuesFromScaffold ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValuesFromScaffold)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBackClick) { // Use the passed onBackClick
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                    }
                    Text(
                        text = "Appointment",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.width(48.dp)) // Balance the IconButton
                }
            }

            item { // Doctor Info
                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Box(
                        modifier = Modifier.size(120.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(doctor.photo_url)
                                .placeholder(R.drawable.ic_doctor_placeholder)
                                .error(R.drawable.ic_doctor_placeholder)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Doctor Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Column(modifier = Modifier.fillMaxHeight().weight(1f), verticalArrangement = Arrangement.SpaceBetween) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)){
                            Text(doctor.fullName, fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Text(specialtyName, fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Medium)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Fees:", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Text("2500 DZD", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) // Assuming fees are static for now
                        }
                    }
                }
            }

            item { // Clinic Info
                Text("Our Clinic", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.primary)) {
                    when {
                        uiState.isLoadingClinic -> {
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color.White)
                            }
                        }
                        healthInstitution != null -> { // Use healthInstitution from uiState
                            val institution = healthInstitution // Already unwrapped from uiState
                            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.Top) {
                                Box(modifier = Modifier.size(105.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.secondary).padding(20.dp), contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (institution.institutionType?.lowercase() == "hospital") Icons.Default.LocalHospital else Icons.Default.Business,
                                        contentDescription = institution.institutionType ?: "Clinic/Hospital",
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(institution.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    institution.address?.let { addr ->
                                        addr.split(",").map { it.trim() }.take(2).forEach { part ->
                                            Text(part, fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                    institution.institutionType?.let { type ->
                                        Text("Type: ${type.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                                    }
                                }
                                if (institution.latitude != null && institution.longitude != null) {
                                    Box(modifier = Modifier.padding(top = 0.dp, start = 8.dp).size(32.dp).background(color = Color.White.copy(alpha = 0.2f), shape = CircleShape).clickable {
                                        coroutineScope.launch { snackbarHostState.showSnackbar("Map (lat: ${institution.latitude}, lon: ${institution.longitude})") }
                                    }, contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.LocationOn, "Location", tint = Color.White, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                        // Error state for clinic (if doctor.health_institution_id was valid but fetch failed)
                        doctor.health_institution_id != null && doctor.health_institution_id > 0 && !uiState.isLoadingClinic -> {
                            Text(
                                text = "Clinic Info: ${uiState.errorMessages.firstOrNull { it.contains("clinic", ignoreCase = true) } ?: "Failed to load details."}",
                                color = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                        else -> { // No health_institution_id or it's invalid
                            Text("Clinic information not available for this doctor.", color = Color.White.copy(alpha = 0.8f), modifier = Modifier.padding(16.dp).fillMaxWidth(), textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            item { // Date Selection
                Text("Select Date & Time", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                val today = LocalDate.now()
                val dates = remember { (0..13).map { today.plusDays(it.toLong()) } }
                Text("Select Date:", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(horizontal = 2.dp)) {
                    items(dates) { date ->
                        DateChip(
                            date = date,
                            isSelected = date == uiState.selectedDate,
                            onClick = { viewModel.selectDate(date) } // Call ViewModel method
                        )
                    }
                }
            }

            item { // Time Slot Selection
                if (uiState.selectedDate != null) {
                    Text("Available Slots for ${uiState.selectedDate!!.format(DateTimeFormatter.ofPattern("MMM dd, EEE"))}", fontWeight = FontWeight.Medium, fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    if (uiState.isLoadingSlots) {
                        Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
                    } else if (uiState.errorMessages.any { it.contains("slots", ignoreCase = true) } && uiState.timeSlots.isEmpty()) {
                        Text("Could not load slots. Try again.", color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        // Optional: Add a retry button for slots
                        Button(onClick = { viewModel.selectDate(uiState.selectedDate!!) }) { // Reselecting date triggers slot loading
                            Text("Retry Slots")
                        }
                    } else {
                        val availableSlots = uiState.timeSlots.filter { it.status.equals("available", ignoreCase = true) }
                        if (availableSlots.isEmpty()) {
                            Text("No available slots for this day.", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
                        } else {
                            LazyVerticalGrid(columns = GridCells.Fixed(4), contentPadding = PaddingValues(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().heightIn(max = 250.dp)) {
                                lazyGridItems(availableSlots, key = { it.id }) { slot ->
                                    TimeSlotChip(
                                        slot = slot,
                                        isSelected = slot == uiState.selectedTimeSlot,
                                        onClick = { viewModel.selectTimeSlot(slot) } // Call ViewModel method
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text("Please select a date to see available slots.", modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(), textAlign = TextAlign.Center, color = Color.Gray)
                }
            }

            item { // Buttons Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            if (uiState.selectedTimeSlot != null && !uiState.isBooking) {
                                viewModel.bookAppointment() // Call ViewModel method
                            } else if (uiState.selectedTimeSlot == null) {
                                coroutineScope.launch { snackbarHostState.showSnackbar("Please select a time slot first.") }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        enabled = uiState.selectedTimeSlot != null && !uiState.isBooking && !uiState.isLoadingSlots
                    ) {
                        if (uiState.isBooking) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Book Appointment", color = Color.White, fontSize = 16.sp) // Changed text
                        }
                    }

                    Button(
                        onClick = onViewAppointmentsClick,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                    ) {
                        Text("View My Appointments", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

// DateChip, TimeSlotChip composables remain the same (ensure they are defined or imported)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateChip(date: LocalDate, isSelected: Boolean, onClick: () -> Unit) {
    val dayName = date.dayOfWeek.getDisplayName(JavaTextStyle.SHORT, Locale.getDefault())
    val dayOfMonth = date.dayOfMonth.toString()

    Card(
        modifier = Modifier.width(64.dp).height(72.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(vertical = 8.dp, horizontal = 4.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = dayName, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = dayOfMonth, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeSlotChip(slot: TimeSlot, isSelected: Boolean, onClick: () -> Unit) {
    val startTimeFormatted = try { LocalTime.parse(slot.start_time).format(DateTimeFormatter.ofPattern("HH:mm")) } catch (e: Exception) { slot.start_time }
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent, contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.primary),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        modifier = Modifier.wrapContentWidth() // .padding(horizontal = 4.dp) // Adjust padding if needed
    ) { Text(startTimeFormatted, fontSize = 14.sp) }
}