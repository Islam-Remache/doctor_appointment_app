package com.example.dzdoc.data.repository

import com.example.dzdoc.data.remote.ApiService
import com.example.dzdoc.data.remote.dto.AppointmentDetailsDto
import com.example.dzdoc.data.remote.dto.DoctorAppointmentViewDto // ⭐ NEW IMPORT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import com.example.dzdoc.ui.model.Appointment

interface AppointmentRepository {
    // Patient methods
    suspend fun getPatientAppointments(patientId: Int): Result<List<AppointmentDetailsDto>>
    suspend fun deleteAppointment(appointmentId: Int): Result<Unit>
    suspend fun getSingleAppointmentDetails(appointmentId: Int): Result<AppointmentDetailsDto>

    // Doctor methods
    suspend fun getDoctorAppointments(doctorId: Int): Result<List<DoctorAppointmentViewDto>>
    suspend fun confirmAppointmentByDoctor(appointmentId: Int): Result<AppointmentDetailsDto>
    suspend fun declineAppointmentByDoctor(appointmentId: Int): Result<AppointmentDetailsDto>
    suspend fun getSingleAppointmentDetailsForDoctorView(appointmentId: Int): Result<DoctorAppointmentViewDto>
    suspend fun getAppointmentById(id: Int): Result<Appointment>
    suspend fun confirmAppointment(appointment_id: Int, doctor_id: Int): Result<Appointment>
    suspend fun cancelAppointment(appointment_id: Int, doctor_id: Int): Result<Appointment>
}


class AppointmentRepositoryImpl(
    private val apiService: ApiService
) : AppointmentRepository {

    override suspend fun getPatientAppointments(patientId: Int): Result<List<AppointmentDetailsDto>> {
        return try { Result.success(apiService.getPatientAppointments(patientId)) }
        catch (e: Exception) {Result.failure(e) }
    }
    override suspend fun deleteAppointment(appointmentId: Int): Result<Unit> {
        return try {
            val response = apiService.deleteAppointment(appointmentId)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Repo: DeleteAppointment failed: ${response.code()}"))
        } catch (e: Exception) {Result.failure(e) }
    }
    override suspend fun getSingleAppointmentDetails(appointmentId: Int): Result<AppointmentDetailsDto> {
        return try { Result.success(apiService.getSingleAppointmentDetails(appointmentId)) }
        catch (e: Exception) {Result.failure(e) }
    }


    override suspend fun getDoctorAppointments(doctorId: Int): Result<List<DoctorAppointmentViewDto>> {
        return try {
            val response = apiService.getDoctorAppointments(doctorId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun confirmAppointmentByDoctor(appointmentId: Int): Result<AppointmentDetailsDto> {
        return try {
            val response = apiService.confirmAppointmentByDoctor(appointmentId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun declineAppointmentByDoctor(appointmentId: Int): Result<AppointmentDetailsDto> {
        return try {
            val response = apiService.declineAppointmentByDoctor(appointmentId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSingleAppointmentDetailsForDoctorView(appointmentId: Int): Result<DoctorAppointmentViewDto> {
        return try {
            val response = apiService.getSingleAppointmentDetailsForDoctorView(appointmentId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAppointmentById(id: Int): Result<Appointment> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAppointmentById(id)
                if (response.body()?.success == true) {
                    response.body()?.data?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Data is null"))
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Unknown error"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun confirmAppointment(appointment_id: Int, doctor_id: Int): Result<Appointment> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.confirmAppointment(appointment_id, doctor_id)
                if (response.body()?.success == true) {
                    response.body()?.data?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Data is null"))
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Unknown error"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    override suspend fun cancelAppointment(appointment_id: Int, doctor_id: Int): Result<Appointment> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.cancelAppointment(appointment_id, doctor_id)
                if (response.body()?.success == true) {
                    response.body()?.data?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Data is null"))
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Unknown error"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}