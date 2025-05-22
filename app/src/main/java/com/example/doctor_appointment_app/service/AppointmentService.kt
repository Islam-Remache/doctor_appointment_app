package com.example.doctor_appointment_app.service

import com.example.doctor_appointment_app.model.appointment.Appointment
import com.example.doctor_appointment_app.model.appointment.AppointmentResponse
import com.example.doctor_appointment_app.model.appointment.AppointmentStatus

import retrofit2.Response


import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Body

interface AppointmentService {
    @GET("appointments/{id}")
    suspend fun getAppointmentById(@Path("id") id: Int): Response<AppointmentResponse<Appointment>>

    @GET("appointments")
    suspend fun getAppointments(): Response<AppointmentResponse<List<Appointment>>>

    @PUT("appointments/{appointment_id}/doctor/{doctor_id}/confirm")
    suspend fun confirmAppointment(
        @Path("appointment_id") appointment_id: Int,
        @Path("doctor_id") doctor_id: Int
    ): Response<AppointmentResponse<Appointment>>

    @PUT("appointments/{appointment_id}/doctor/{doctor_id}/cancel")
    suspend fun cancelAppointment(
        @Path("appointment_id") appointment_id: Int,
        @Path("doctor_id") doctor_id: Int
    ): Response<AppointmentResponse<Appointment>>
}
data class UpdateStatusRequest(val status: AppointmentStatus)