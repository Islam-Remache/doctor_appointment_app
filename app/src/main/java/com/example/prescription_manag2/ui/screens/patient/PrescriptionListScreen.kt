package com.example.prescription_manag2.ui.screens.patient

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.prescription_manag2.R
import com.example.prescription_manag2.models.PrescriptionModel
import com.example.prescription_manag2.ui.navigation.Destination
import com.example.prescription_manag2.viewmodel.PrescriptionViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

val OriginalPrimaryBlue = Color(0xFF0D6783)
val OriginalSearchBackground = Color(0xFFF2F9F8)
val OriginalTextGray = Color.Gray
val OriginalTextBlack = Color.Black


@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(date: String): String {
    val onlyDate = date.take(10)
    val localDate = LocalDate.parse(onlyDate)
    val formatter = DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.ENGLISH)
    return localDate.format(formatter)
}

@Composable
fun LoadingScreen(message: String = "Loading prescriptions...") {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = OriginalPrimaryBlue)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                fontSize = 18.sp,
                color = OriginalTextBlack,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PrescriptionListScreen(
    prescriptionViewModel: PrescriptionViewModel,
    context: Context,
    navController: NavController
) {
    val patients = prescriptionViewModel.patients.value
    val patient = patients.firstOrNull()

    var isLoadingData by remember { mutableStateOf(true) }
    var showEmptyStateContent by remember { mutableStateOf(false) }

    LaunchedEffect(patient) {
        showEmptyStateContent = false
        if (patient != null) {
            delay(1000)
            prescriptionViewModel.getCompletePrescriptionDataRoom(patient.id)
            isLoadingData = false
        }
    }

    val prescriptions = prescriptionViewModel.prescriptions.value
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var applyFilter by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedPeriod by remember { mutableStateOf("All") }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val filteredPrescriptions = prescriptions.filter { prescription ->
        val matchesSearch = searchQuery.isEmpty() ||
                "${prescription.doctor.firstName} ${prescription.doctor.lastName}".contains(searchQuery, ignoreCase = true) ||
                (prescription.doctor.specialty?.label?.contains(searchQuery, ignoreCase = true) ?: false)

        val onlyDate = prescription.createdAt.substring(0, 10)
        val prescriptionDate = LocalDate.parse(onlyDate, formatter)
        val matchesDate = if (applyFilter && startDate != null && endDate != null) {
            prescriptionDate in startDate!!..endDate!!
        } else {
            true
        }
        matchesSearch && matchesDate
    }

    LaunchedEffect(isLoadingData, filteredPrescriptions.isEmpty()) {
        if (!isLoadingData) {
            if (filteredPrescriptions.isEmpty()) {
                delay(600)
                if (!isLoadingData && filteredPrescriptions.isEmpty()) {
                    showEmptyStateContent = true
                }
            } else {
                showEmptyStateContent = false
            }
        } else {
            showEmptyStateContent = false
        }
    }

    if (isLoadingData && patient != null) {
        LoadingScreen(message = "Loading prescriptions...")
    } else if (isLoadingData && patient == null) {
        LoadingScreen(message = "Loading prescriptions...")
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(top = 45.dp, start = 2.dp, end = 2.dp, bottom = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Prescription history",
                    fontSize = 26.sp,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    color = OriginalPrimaryBlue
                )
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                placeholder = { Text("Search doctor or specialty", color = OriginalTextGray, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon", tint = OriginalPrimaryBlue) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OriginalPrimaryBlue,
                    unfocusedBorderColor = Color(0xFFE0E8F2),
                    focusedContainerColor = OriginalSearchBackground,
                    unfocusedContainerColor = OriginalSearchBackground,
                    cursorColor = OriginalPrimaryBlue,
                    focusedTextColor = OriginalTextBlack,
                    unfocusedTextColor = OriginalTextBlack
                ),
                singleLine = true,
                textStyle = TextStyle(fontWeight = FontWeight.SemiBold, color = OriginalTextBlack, fontSize = 15.sp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val periods = listOf("All", "Today", "This week", "This month", "Previous month", "This year")
                items(periods) { period ->
                    Card(
                        modifier = Modifier
                            .clickable {
                                selectedPeriod = period
                                if (period == "All") {
                                    applyFilter = false; startDate = null; endDate = null
                                } else {
                                    applyFilter = true
                                    when (period) {
                                        "Today" -> { startDate = LocalDate.now(); endDate = LocalDate.now() }
                                        "This week" -> { startDate = LocalDate.now().with(java.time.DayOfWeek.MONDAY); endDate = LocalDate.now().with(java.time.DayOfWeek.SUNDAY) }
                                        "This month" -> { startDate = LocalDate.now().withDayOfMonth(1); endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()) }
                                        "Previous month" -> { startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1); endDate = LocalDate.now().minusMonths(1).withDayOfMonth(LocalDate.now().minusMonths(1).lengthOfMonth()) }
                                        "This year" -> { startDate = LocalDate.now().withDayOfYear(1); endDate = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear()) }
                                    }
                                }
                            }
                            .padding(vertical = 8.dp)
                            .border(width = 2.dp, color = if (selectedPeriod == period) OriginalPrimaryBlue else Color(0xFFE0E8F2), shape = RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = if (selectedPeriod == period) OriginalPrimaryBlue else Color.White)
                    ) {
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
                            Text(text = period, color = if (selectedPeriod == period) Color.White else OriginalTextBlack, fontWeight = FontWeight.Normal)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (showEmptyStateContent) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.empty_box),
                        contentDescription = "Empty box illustration",
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty() || applyFilter) "No prescriptions match your criteria."
                        else if (patient == null) "No patient data available."
                        else "No prescriptions yet.",
                        fontSize = 16.sp,
                        color = OriginalTextGray,
                        textAlign = TextAlign.Center
                    )
                }
            } else if (filteredPrescriptions.isNotEmpty()) {
                LazyColumn( modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredPrescriptions, key = { it.id }) { prescription ->
                        PrescriptionCard(prescription) {
                            navController.navigate(Destination.PrescriptionDetailsApp.createRoute(prescription.id))
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {}
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PrescriptionCard(prescription: PrescriptionModel, onPrescriptionSelected: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onPrescriptionSelected() }
    ) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${prescription.doctor.firstName} ${prescription.doctor.lastName}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OriginalPrimaryBlue
                )
                prescription.doctor.specialty?.let { specialty ->
                    Text(
                        text = specialty.label,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 5.dp),
                        color = Color(0xFF7BC1B7)
                    )
                }
                Text(
                    text = "${prescription.medications.size} medicines",
                    fontSize = 14.sp,
                    color = OriginalPrimaryBlue
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF2F9F8))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.calendar),
                        contentDescription = "Calendar Icon",
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = formatDate(prescription.createdAt),
                        fontSize = 14.sp,
                        color = OriginalPrimaryBlue,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.doctor5),
                    contentDescription = "Doctor Image",
                    modifier = Modifier
                        .size(78.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )

                val isActive = prescription.status == "active"
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isActive) Color(0xFFF2F9F8) else Color(0xFFF7E4D9))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isActive) "Active" else "Expired",
                        color = if (isActive) OriginalPrimaryBlue else Color(0xFFCC4900),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}