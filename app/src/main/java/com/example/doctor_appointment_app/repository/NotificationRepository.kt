package com.example.dzdoc.data.repository


import com.example.dzdoc.ui.model.NotificationItem
import com.example.dzdoc.ui.model.toDomainModel
import com.example.dzdoc.data.remote.NotificationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationService: NotificationService
) {
    /**
     * Fetches all notifications from the API
     */
    suspend fun getNotifications(): Result<List<NotificationItem>> = withContext(Dispatchers.IO) {
        try {
            val response = notificationService.getNotifications()
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                Result.success(dto.notifications.map { it.toDomainModel() })
            } else {
                Result.failure(Exception("Failed to fetch notifications: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    /**
     * Removes a notification from the server
     */
    suspend fun removeNotification(id: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = notificationService.removeNotification(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to remove notification: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Marks a notification as read on the server
     */
    suspend fun markAsRead(id: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = notificationService.markAsRead(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to mark notification as read: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}