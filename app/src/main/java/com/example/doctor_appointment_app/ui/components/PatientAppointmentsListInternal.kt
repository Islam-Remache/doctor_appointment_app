package com.example.dzdoc.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dzdoc.ui.model.DoctorScreenAppointmentItem // ⭐ USE NEW UI MODEL ⭐

@Composable
fun PatientAppointmentsListInternal(
    appointments: List<DoctorScreenAppointmentItem>,
    onAccept: (DoctorScreenAppointmentItem) -> Unit,
    onDecline: (DoctorScreenAppointmentItem) -> Unit,
    onAppointmentClick: (DoctorScreenAppointmentItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(all = 0.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(
            items = appointments,
            key = { appointment -> "doctor-view-appt-${appointment.appointmentId}" }
        ) { appointmentItem ->
            PatientAppointmentFromPatientCard(
                appointmentItem = appointmentItem,
                onAccept = onAccept,
                onDecline = onDecline,
                onCardClick = { onAppointmentClick(appointmentItem) }
            )
        }
    }
}