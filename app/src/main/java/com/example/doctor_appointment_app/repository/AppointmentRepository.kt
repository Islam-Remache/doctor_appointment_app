package com.example.doctor_appointment_app.repository

import android.util.Log
import com.example.doctor_appointment_app.model.appointment.Appointment
import com.example.doctor_appointment_app.model.appointment.AppointmentStatus
import com.example.doctor_appointment_app.service.AppointmentService
import com.example.doctor_appointment_app.service.UpdateStatusRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.internal.concurrent.TaskRunner.Companion.logger
import okhttp3.internal.http2.Http2Reader.Companion.logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentRepository @Inject constructor(
    private val apiService: AppointmentService
) {
    suspend fun getAppointmentById(id: Int): Result<Appointment> {
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

    suspend fun confirmAppointment(appointment_id: Int, doctor_id: Int): Result<Appointment> {
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


    suspend fun cancelAppointment(appointment_id: Int, doctor_id: Int): Result<Appointment> {
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