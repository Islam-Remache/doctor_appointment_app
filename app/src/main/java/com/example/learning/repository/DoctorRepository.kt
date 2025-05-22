package com.example.learning.repository
import com.example.learning.model.AppointmentCreateRequest
import com.example.learning.model.Doctor
import com.example.learning.model.HealthInstitution
import com.example.learning.model.SpecialtyResponse
import com.example.learning.model.TimeSlot
interface DoctorRepository {
    suspend fun getDoctors(): List<Doctor>
    suspend fun getSpecialties(): List<SpecialtyResponse>
    suspend fun getDoctorById(doctorId: Int): Doctor?
    suspend fun getDoctorSlots(doctorId: Int, date: String): List<TimeSlot>
    suspend fun bookAppointment(appointmentRequest: AppointmentCreateRequest): Result<Unit>
    suspend fun getHealthInstitution(institutionId: Int): HealthInstitution?
}