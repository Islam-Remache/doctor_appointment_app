package com.example.dzdoc.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dzdoc.ui.model.PatientScreenAppointmentItem

@Composable
fun DoctorAppointmentList(
    appointments: List<PatientScreenAppointmentItem>,
    onReschedule: (PatientScreenAppointmentItem) -> Unit,
    onCancel: (PatientScreenAppointmentItem) -> Unit,
    onAppointmentClick: (PatientScreenAppointmentItem) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = appointments,
            key = { appointment -> "patient-appt-item-${appointment.id}" }
        ) { appointmentItem ->
            DoctorAppointmentCard(
                appointment = appointmentItem,
                onReschedule = { onReschedule(appointmentItem) },
                onCancel = { onCancel(appointmentItem) },
                onAppointmentClick = { onAppointmentClick(appointmentItem) }
            )
        }
    }
}