// file: com/example/dzdoc/data/remote/dto/DoctorAppointmentViewDto.kt (NEW)
package com.example.dzdoc.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DoctorAppointmentViewDto(
    @SerializedName("appointment_id")
    val appointmentId: Int,
    @SerializedName("appointment_status")
    val appointmentStatus: String,
    @SerializedName("qr_code_url")
    val qrCodeUrl: String?,
    val date: String?, // ISO format e.g., "2023-10-27"
    @SerializedName("start_time")
    val startTime: String?, // ISO format e.g., "09:00:00"
    @SerializedName("end_time")
    val endTime: String?, // ISO format e.g., "09:30:00"
    val patient: PatientDetailsDto, // Embedded patient details
    @SerializedName("health_institution_address")
    val healthInstitutionAddress: String?,
    @SerializedName("health_institution_latitude")
    val healthInstitutionLatitude: Double?,
    @SerializedName("health_institution_longitude")
    val healthInstitutionLongitude: Double?
)