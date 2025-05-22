package com.example.prescription_manag2.ui.screens.doctor

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import com.example.prescription_manag2.entity.Prescription
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.prescription_manag2.R
import com.example.prescription_manag2.entity.Patient
import com.example.prescription_manag2.viewmodel.PrescriptionViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.navigation.NavController
import com.example.prescription_manag2.entity.Medication
import com.example.prescription_manag2.entity.Prescription.Companion.getCurrentTimestamp
import com.example.prescription_manag2.entity.SyncStatus
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun MedicationForm(
    onDismiss: () -> Unit,
    onAddMedication: (Medication) -> Unit,
    medication: String,
    onMedicationChange: (String) -> Unit,
    dosage: String,
    onDosageChange: (String) -> Unit,
    frequency: String,
    onFrequencyChange: (String) -> Unit,
    duration: String,
    onDurationChange: (String) -> Unit,
    context: Context
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .size(width = 560.dp, height = 520.dp)
                .background(Color(0xFFD2EBE7), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.cancel),
                contentDescription = "Cancel Icon",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(14.dp)
                    .clickable { onDismiss() }
            )
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Add medication",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )

                Text(
                    fontWeight = FontWeight.SemiBold,
                    text = "Medication",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                TextField(
                    value = medication,
                    onValueChange = { onMedicationChange(it) },
                    placeholder = { Text("Medication") },
                    textStyle = TextStyle(fontWeight = FontWeight.SemiBold, color = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    fontWeight = FontWeight.SemiBold,
                    text = "Dosage",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                TextField(
                    value = dosage,
                    onValueChange = { onDosageChange(it)},
                    placeholder = { Text("Dosage") },
                    textStyle = TextStyle(fontWeight = FontWeight.SemiBold, color = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    fontWeight = FontWeight.SemiBold,
                    text = "Frequency",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                TextField(
                    value = frequency,
                    onValueChange = { onFrequencyChange(it) },
                    placeholder = { Text("Frequency") },
                    textStyle = TextStyle(fontWeight = FontWeight.SemiBold, color = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    fontWeight = FontWeight.SemiBold,
                    text = "Duration",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                TextField(
                    value = duration,
                    onValueChange = { onDurationChange(it) },
                    placeholder = { Text("Duration") },
                    textStyle = TextStyle(fontWeight = FontWeight.SemiBold, color = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Button(
                        onClick = {
                            when {
                                medication.isBlank() -> {
                                    Toast.makeText(context, "Please enter the medication name", Toast.LENGTH_SHORT).show()
                                }
                                dosage.isBlank() -> {
                                    Toast.makeText(context, "Please enter the dosage", Toast.LENGTH_SHORT).show()
                                }
                                frequency.isBlank() -> {
                                    Toast.makeText(context, "Please enter the frequency", Toast.LENGTH_SHORT).show()
                                }
                                duration.isBlank() -> {
                                    Toast.makeText(context, "Please enter the duration", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    onAddMedication(
                                        Medication(
                                            prescriptionId = 0L,
                                            name = medication,
                                            dosage = dosage,
                                            frequency = frequency,
                                            duration = duration
                                        )
                                    )
                                    onMedicationChange("")
                                    onDosageChange("")
                                    onFrequencyChange("")
                                    onDurationChange("")
                                    onDismiss()
                                }
                            }

                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B8FAC)),
                        shape = RoundedCornerShape(9.dp)
                    ) {
                        Text("Add", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DigitalPrescription(
    prescriptionViewModel: PrescriptionViewModel,
    context: Context,
    navController: NavController
) {
    val patients = prescriptionViewModel.patients.value
    val doctorId = prescriptionViewModel.doctorId.value
    var selectedPatient by remember { mutableStateOf<Patient?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val medications = remember { mutableStateListOf<Medication>() }
    var instructions by remember { mutableStateOf("") }
    var medication by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var isSaved by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    val onAddMedication: (Medication) -> Unit = { medication ->
        medications.add(medication)
    }

    val onDeleteMedication: (Medication) -> Unit = { medicationToDelete ->
        medications.remove(medicationToDelete)
        // Si c'est un médicament déjà sauvegardé (qui a un ID valide), on appelle la fonction de suppression du ViewModel
        if (medicationToDelete.id > 0) {
            prescriptionViewModel.deleteMedication(medicationToDelete.id)
            Toast.makeText(context, "Medication deleted", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 45.dp, start = 5.dp, end = 5.dp, bottom = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Digital Prescription",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
                color = Color(0xFF0D6783)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 25.dp)
        ) {
            Text(
                text = "Patient:",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 8.dp)
            )

            Box(
                modifier = Modifier
                    .width(265.dp),
            ) {
                Surface(
                    color = Color(0xFFD6EFEA),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .fillMaxWidth()
                    ) {
                        selectedPatient?.photoUrl?.let {
                            // Use a placeholder image since we can't load from URL directly
                            Image(
                                painter = painterResource(id = R.drawable.user),
                                contentDescription = "User Image",
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                            )
                        }

                        Text(
                            text = selectedPatient?.let { "${it.firstName} ${it.lastName}" } ?: "Select a patient",
                            fontSize = 15.sp,
                            color = Color(0xFF2E2E2E),
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.W500
                        )

                        Image(
                            painter = painterResource(id = R.drawable.arrow),
                            contentDescription = "Arrow Image",
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }

                // DropdownMenu
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .padding(start = 0.dp)
                        .width(265.dp)
                        .background(Color(0xFFF9F9F9))
                ) {
                    patients.forEach { patient ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Use a placeholder image
                                    Image(
                                        painter = painterResource(id = R.drawable.user),
                                        contentDescription = "User Image",
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .size(24.dp)
                                            .clip(CircleShape)
                                    )
                                    Text(text = "${patient.firstName} ${patient.lastName}")
                                }
                            },
                            onClick = {
                                selectedPatient = patient
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 25.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Medications:",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Surface(
                color = Color(0xFFF9F9F9),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .clickable { showDialog = true }
                    .width(190.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Add medication",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                    Image(
                        painter = painterResource(id = R.drawable.plus),
                        contentDescription = "Add medication",
                        modifier = Modifier
                            .size(28.dp)
                            .padding(start = 8.dp)
                    )
                }
                if (showDialog) {
                    MedicationForm(
                        onDismiss = { showDialog = false },
                        medication = medication,
                        onMedicationChange = { medication = it },
                        dosage = dosage,
                        onDosageChange = { dosage = it },
                        frequency = frequency,
                        onFrequencyChange = { frequency = it },
                        duration = duration,
                        onDurationChange = { duration = it },
                        onAddMedication = onAddMedication,
                        context = context
                    )
                }
                if (isSaved) {
                    LaunchedEffect(Unit) {
                        snackbarHostState.showSnackbar("Medication has been added successfully!")
                        isSaved = false
                    }
                }
            }
        }

        if (medications.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = Color(0xFFF9F9F9),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Medication",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0B8FAC),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Dosage",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0B8FAC),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Frequency",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0B8FAC),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Duration",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0B8FAC),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    // Colonne supplémentaire pour l'action de suppression
                    Text(
                        text = "Delete",
                        modifier = Modifier.width(48.dp),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0B8FAC),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        medications.forEach { medication ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = Color(0xFFF9F9F9),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = medication.name ?: "",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = medication.dosage ?: "",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = medication.frequency ?: "",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = medication.duration ?: "",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    // Icône de suppression
                    Box(
                        modifier = Modifier.width(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = "Delete medication",
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { onDeleteMedication(medication) }
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Instructions",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 25.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = instructions,
                onValueChange = { instructions = it },
                placeholder = { Text("Enter your instructions here") },
                textStyle = TextStyle(fontWeight = FontWeight.SemiBold, color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(195.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0xFF0B8FAC), RoundedCornerShape(16.dp)),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                when {
                    selectedPatient == null -> {
                        Toast.makeText(context, "Please select a patient", Toast.LENGTH_SHORT).show()
                    }
                    medications.isEmpty() -> {
                        Toast.makeText(context, "Please add at least one medication", Toast.LENGTH_SHORT).show()
                    }
                    instructions.isBlank() -> {
                        Toast.makeText(context, "Please enter instructions", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
                        val calendar = Calendar.getInstance()
                        calendar.add(Calendar.DAY_OF_YEAR, 7)
                        val expirationDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

                        val prescription = Prescription(
                            id = System.currentTimeMillis(),
                            patientId = selectedPatient!!.id,
                            doctorId = doctorId!!,
                            instructions = instructions,
                            createdAt = getCurrentTimestamp(),
                            expiresAt = expirationDate,
                            status = "active",
                            syncStatus = SyncStatus.PENDING_SYNC,
                        )

                        prescriptionViewModel.insertPrescription(prescription) { prescriptionId ->
                            val updatedMedications = medications.map {
                                it.copy(prescriptionId = prescriptionId)
                            }
                            prescriptionViewModel.insertMedications(updatedMedications)

                            Toast.makeText(context, "Prescription saved successfully", Toast.LENGTH_SHORT).show()

                            // Reset form fields
                            medication = ""
                            dosage = ""
                            medications.clear()
                            frequency = ""
                            duration = ""
                            instructions = ""
                            selectedPatient = null
                        }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B8FAC)),
            shape = RoundedCornerShape(9.dp),
            modifier = Modifier
                .height(50.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Save", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 19.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = R.drawable.save),
                contentDescription = "Download Icon",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}