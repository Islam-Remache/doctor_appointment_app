package com.example.doctor_appointment_app.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.doctor_appointment_app.repository.NotificationRepository
import com.example.doctor_appointment_app.model.notifications.NotificationItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _notifications = mutableStateListOf<NotificationItem>()
    val notifications: List<NotificationItem> get() = _notifications

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val unreadCount: Int
        get() = notifications.count { !it.isRead }

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            notificationRepository.getNotifications()
                .onSuccess { notificationList: List<NotificationItem> ->
                    _notifications.clear()
                    _notifications.addAll(notificationList)
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to load notifications"
                }

            _isLoading.value = false
        }
    }

    fun removeNotification(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            notificationRepository.removeNotification(id)
                .onSuccess {
                    _notifications.removeIf { it.id == id }
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to remove notification"
                }

            _isLoading.value = false
        }
    }

    fun markAsRead(id: Int) {
        viewModelScope.launch {
            // First update locally for immediate feedback
            val index = _notifications.indexOfFirst { it.id == id }
            if (index != -1) {
                val notification = _notifications[index]
                _notifications[index] = notification.copy(isRead = true)
            }

            // Then update the server
            notificationRepository.markAsRead(id)
                .onFailure { exception ->
                    // Revert the local change if the server update failed
                    _error.value = exception.message ?: "Failed to mark notification as read"
                    loadNotifications() // Refresh from server to ensure consistency
                }
        }
    }

    fun clearError() {
        _error.value = null
    }

    /**
     * Factory for creating NotificationViewModel with dependencies
     */
    class Factory(private val notificationRepository: NotificationRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
                return NotificationViewModel(notificationRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}