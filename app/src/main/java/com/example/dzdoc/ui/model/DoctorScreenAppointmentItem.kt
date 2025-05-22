package com.example.dzdoc.ui.model

import com.example.dzdoc.data.AppointmentStatus

data class DoctorScreenAppointmentItem(
    val appointmentId: Int,
    val patientName: String,
    val patientPhotoUrl: String?,
    val patientId: Int,
    val appointmentDate: String,
    val appointmentTime: String,
    val appointmentStatus: AppointmentStatus,
    val rawStatusString: String,
    val reasonForVisit: String?
)