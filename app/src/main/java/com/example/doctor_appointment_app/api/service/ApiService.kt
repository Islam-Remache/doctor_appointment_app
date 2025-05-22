package com.example.dzdoc.data.remote

import com.example.dzdoc.data.remote.dto.AppointmentDetailsDto
import com.example.dzdoc.data.remote.dto.DoctorAppointmentViewDto
import com.example.dzdoc.ui.model.Appointment
import com.example.dzdoc.ui.model.AppointmentResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path


interface ApiService {


    @GET("appointments/patient/{patient_id}/details")
    suspend fun getPatientAppointments(
        @Path("patient_id") patientId: Int
    ): List<AppointmentDetailsDto>

    @DELETE("appointments/{appointment_id}")
    suspend fun deleteAppointment(
        @Path("appointment_id") appointmentId: Int
    ): Response<Unit>

    @GET("appointments/{appointment_id}/details_single")
    suspend fun getSingleAppointmentDetails(
        @Path("appointment_id") appointmentId: Int
    ): AppointmentDetailsDto


    @GET("appointments/doctor/{doctor_id}/details")
    suspend fun getDoctorAppointments(
        @Path("doctor_id") doctorId: Int,
    ): List<DoctorAppointmentViewDto>

    @PATCH("appointments/{appointment_id}/confirm")
    suspend fun confirmAppointmentByDoctor(
        @Path("appointment_id") appointmentId: Int
    ): AppointmentDetailsDto

    @PATCH("appointments/{appointment_id}/decline")
    suspend fun declineAppointmentByDoctor(
        @Path("appointment_id") appointmentId: Int
    ): AppointmentDetailsDto

    @GET("appointments/{appointment_id}/doctor_view_details")
    suspend fun getSingleAppointmentDetailsForDoctorView(
        @Path("appointment_id") appointmentId: Int
    ): DoctorAppointmentViewDto

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