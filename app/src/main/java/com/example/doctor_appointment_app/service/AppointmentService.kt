package com.example.doctor_appointment_app.service

import com.example.doctor_appointment_app.model.appointment.Appointment
import com.example.doctor_appointment_app.model.appointment.AppointmentResponse
import retrofit2.Response


import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Body

interface AppoiService {
    @GET("appointments/{id}")
    suspend fun getAppointmentById(@Path("id") id: Int): Response<AppointmentResponse<Appointment>>

    @GET("appointments")
    suspend fun getAppointments(): Response<AppointmentResponse<List<Appointment>>>

    @PUT("appointments/{id}/status")
    suspend fun updateAppointmentStatus(
        @Path("id") id: Int,
        @Body status: AppointmentStatus
    ): Response<AppointmentResponse<Appointment>>
}