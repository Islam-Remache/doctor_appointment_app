
package com.example.dzdoc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dzdoc.data.repository.AppointmentRepository
import com.example.dzdoc.ui.model.DoctorScreenAppointmentItem
import com.example.dzdoc.ui.model.toDoctorScreenAppointmentItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed interface DoctorAppointmentsUiState {
    object Loading : DoctorAppointmentsUiState
    data class Success(val appointments: List<DoctorScreenAppointmentItem>) : DoctorAppointmentsUiState
    data class Error(val message: String) : DoctorAppointmentsUiState
}


sealed interface AppointmentUpdateEvent {
    object Idle : AppointmentUpdateEvent
    data class Success(val message: String, val updatedAppointmentId: Int) : AppointmentUpdateEvent
    data class Error(val message: String) : AppointmentUpdateEvent
}

class DoctorAppointmentsViewModel(
    private val repository: AppointmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DoctorAppointmentsUiState>(DoctorAppointmentsUiState.Loading)
    val uiState: StateFlow<DoctorAppointmentsUiState> = _uiState.asStateFlow()


    private val _updateEvent = MutableStateFlow<AppointmentUpdateEvent>(AppointmentUpdateEvent.Idle)
    val updateEvent: StateFlow<AppointmentUpdateEvent> = _updateEvent.asStateFlow()



    private val currentDoctorId = 1

    init {
        fetchDoctorAppointments()
    }

    fun fetchDoctorAppointments(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.value = DoctorAppointmentsUiState.Loading
            }
            repository.getDoctorAppointments(currentDoctorId).onSuccess { dtos ->
                _uiState.value = DoctorAppointmentsUiState.Success(dtos.map { it.toDoctorScreenAppointmentItem() })
            }.onFailure { exception ->
                if (_uiState.value !is DoctorAppointmentsUiState.Success || showLoading) {
                    _uiState.value = DoctorAppointmentsUiState.Error(exception.message ?: "Unknown error")
                }
            }
        }
    }

    fun confirmAppointment(appointmentId: Int) {
        viewModelScope.launch {
            _updateEvent.value = AppointmentUpdateEvent.Idle
            repository.confirmAppointmentByDoctor(appointmentId).onSuccess {
                _updateEvent.value = AppointmentUpdateEvent.Success("Appointment Confirmed!", appointmentId)
                fetchDoctorAppointments(showLoading = false)
            }.onFailure { exception ->
                _updateEvent.value = AppointmentUpdateEvent.Error(exception.message ?: "Failed to confirm")
            }
        }
    }

    fun declineAppointment(appointmentId: Int) {
        viewModelScope.launch {
            _updateEvent.value = AppointmentUpdateEvent.Idle
            repository.declineAppointmentByDoctor(appointmentId).onSuccess {
                _updateEvent.value = AppointmentUpdateEvent.Success("Appointment Declined", appointmentId)
                fetchDoctorAppointments(showLoading = false)
            }.onFailure { exception ->
                _updateEvent.value = AppointmentUpdateEvent.Error(exception.message ?: "Failed to decline")
            }
        }
    }

    fun resetUpdateEvent() {
        _updateEvent.value = AppointmentUpdateEvent.Idle
    }
}

class DoctorAppointmentsViewModelFactory(
    private val repository: AppointmentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorAppointmentsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DoctorAppointmentsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class for DoctorAppointmentsViewModel")
    }
}