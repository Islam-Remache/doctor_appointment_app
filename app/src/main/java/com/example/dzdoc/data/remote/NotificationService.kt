package com.example.dzdoc.data.remote



import com.example.dzdoc.ui.model.NotificationResponseDto
import retrofit2.Response

import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotificationService {
    @GET("notifications")
    suspend fun getNotifications(): Response<NotificationResponseDto>

    @DELETE("notifications/{id}")
    suspend fun removeNotification(@Path("id") id: Int): Response<Unit>

    @PUT("notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: Int): Response<Unit>
}