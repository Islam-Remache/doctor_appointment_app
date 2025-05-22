package com.example.dzdoc.ui.model

import com.example.dzdoc.data.AppointmentStatus

data class PatientScreenAppointmentItem(
    val id: Int,
    val doctorName: String,
    val doctorSpecialty: String?,
    val doctorPhotoUrl: String?,
    val appointmentDate: String,
    val appointmentTime: String,
    val status: AppointmentStatus,
    val rawStatusString: String,
    val qrCodeUrl: String?,
    val healthInstitutionAddress: String?,
    val healthInstitutionLatitude: Double?,
    val healthInstitutionLongitude: Double?

)