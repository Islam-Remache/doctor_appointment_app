package com.example.doctor_appointment_app.service


import com.example.doctor_appointment_app.model.notifications.NotificationResponseDto
import retrofit2.Response

import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface NotificationService {
    @GET("notifications")
    suspend fun getNotifications(): Response<NotificationResponseDto>

    @DELETE("notifications/{id}")
    suspend fun removeNotification(@Path("id") id: Int): Response<Unit>

    @PATCH("notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: Int): Response<Unit>
}