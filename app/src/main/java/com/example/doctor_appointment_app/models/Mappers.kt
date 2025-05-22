package com.example.dzdoc.ui.model

import com.example.dzdoc.data.AppointmentStatus
import com.example.dzdoc.data.remote.dto.AppointmentDetailsDto
import com.example.dzdoc.data.remote.dto.DoctorAppointmentViewDto
import java.text.SimpleDateFormat
import java.util.Locale

fun AppointmentDetailsDto.toPatientScreenAppointmentItem(): PatientScreenAppointmentItem {
    val statusEnum = when (this.appointmentStatus.lowercase(Locale.ROOT)) {
        "pending" -> AppointmentStatus.PENDING
        "confirmed" -> AppointmentStatus.CONFIRMED
        "declined" -> AppointmentStatus.DECLINED
        "completed" -> AppointmentStatus.COMPLETED
        else -> AppointmentStatus.PENDING
    }
    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputDateFormat = SimpleDateFormat("EEE MMM dd", Locale.getDefault())
    val formattedDate = this.date?.let { try { inputDateFormat.parse(it)?.let { pd -> outputDateFormat.format(pd) } } catch (e: Exception) { it } } ?: "N/A"
    val inputTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val outputTimeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val formattedTime = this.startTime?.let { try { inputTimeFormat.parse(it)?.let { pt -> outputTimeFormat.format(pt) } } catch (e: Exception) { it } } ?: "N/A"

    return PatientScreenAppointmentItem(
        id = this.appointmentId,
        doctorName = "Dr. ${this.doctor.firstName} ${this.doctor.lastName}",
        doctorSpecialty = this.doctor.specialtyLabel,
        doctorPhotoUrl = this.doctor.photoUrl,
        appointmentDate = formattedDate,
        appointmentTime = formattedTime,
        status = statusEnum,
        rawStatusString = this.appointmentStatus,
        qrCodeUrl = this.qrCodeUrl,
        healthInstitutionAddress = this.healthInstitutionAddress,
        healthInstitutionLatitude = this.healthInstitutionLatitude,
        healthInstitutionLongitude = this.healthInstitutionLongitude
    )
}

fun DoctorAppointmentViewDto.toDoctorScreenAppointmentItem(): DoctorScreenAppointmentItem {
    val statusEnum = when (this.appointmentStatus.lowercase(Locale.ROOT)) {
        "pending" -> AppointmentStatus.PENDING
        "confirmed" -> AppointmentStatus.CONFIRMED
        "declined" -> AppointmentStatus.DECLINED
        "completed" -> AppointmentStatus.COMPLETED
        else -> AppointmentStatus.PENDING
    }
    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputDateFormat = SimpleDateFormat("EEE MMM dd", Locale.getDefault())
    val formattedDate = this.date?.let { try { inputDateFormat.parse(it)?.let { pd -> outputDateFormat.format(pd) } } catch (e: Exception) { it } } ?: "N/A"
    val inputTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val outputTimeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val formattedTime = this.startTime?.let { try { inputTimeFormat.parse(it)?.let { pt -> outputTimeFormat.format(pt) } } catch (e: Exception) { it } } ?: "N/A"

    return DoctorScreenAppointmentItem(
        appointmentId = this.appointmentId,
        patientName = "${this.patient.firstName} ${this.patient.lastName}",
        patientPhotoUrl = this.patient.photoUrl,
        patientId = this.patient.patientId,
        appointmentDate = formattedDate,
        appointmentTime = formattedTime,
        appointmentStatus = statusEnum,
        rawStatusString = this.appointmentStatus,
        reasonForVisit = "Consultation"
    )
}