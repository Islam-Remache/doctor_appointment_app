package com.example.dzdoc.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AppointmentDetailsDto(
    @SerializedName("appointment_id")
    val appointmentId: Int,
    @SerializedName("appointment_status")
    val appointmentStatus: String,
    @SerializedName("qr_code_url")
    val qrCodeUrl: String?,
    val date: String?,
    @SerializedName("start_time")
    val startTime: String?,
    @SerializedName("end_time")
    val endTime: String?,
    val doctor: DoctorDetailsDto,
    @SerializedName("health_institution_address")
    val healthInstitutionAddress: String?,
    @SerializedName("health_institution_latitude")
    val healthInstitutionLatitude: Double?,
    @SerializedName("health_institution_longitude")
    val healthInstitutionLongitude: Double?
)