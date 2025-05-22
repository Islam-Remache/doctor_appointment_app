
package com.example.dzdoc.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dzdoc.data.repository.AppointmentRepository
import com.example.dzdoc.ui.model.PatientScreenAppointmentItem
import com.example.dzdoc.ui.model.toPatientScreenAppointmentItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed interface AppointmentDetailsUiState {
    object Loading : AppointmentDetailsUiState
    data class Success(val appointment: PatientScreenAppointmentItem) : AppointmentDetailsUiState
    data class Error(val message: String) : AppointmentDetailsUiState
}


sealed interface AppointmentCancellationEvent {
    object Idle : AppointmentCancellationEvent
    object Loading : AppointmentCancellationEvent
    object Success : AppointmentCancellationEvent
    data class Error(val message: String) : AppointmentCancellationEvent
}

class PatientAppointmentDetailsViewModel(
    private val repository: AppointmentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppointmentDetailsUiState>(AppointmentDetailsUiState.Loading)
    val uiState: StateFlow<AppointmentDetailsUiState> = _uiState.asStateFlow()


    private val _cancellationEvent = MutableSharedFlow<AppointmentCancellationEvent>()
    val cancellationEvent = _cancellationEvent.asSharedFlow()

    private val appointmentId: Int = checkNotNull(savedStateHandle["appointmentId"]) {
        "Appointment ID not found in SavedStateHandle for PatientAppointmentDetailsViewModel"
    }

    init {
        if (appointmentId != -1) {
            fetchAppointmentDetailsById(appointmentId)
        } else {
            _uiState.value = AppointmentDetailsUiState.Error("Invalid Appointment ID provided.")
            viewModelScope.launch {
                _cancellationEvent.emit(AppointmentCancellationEvent.Error("Invalid Appointment ID"))
            }
        }
    }

    fun fetchAppointmentDetailsById(id: Int) {
        viewModelScope.launch {
            _uiState.value = AppointmentDetailsUiState.Loading
            repository.getSingleAppointmentDetails(id).onSuccess { dto ->
                _uiState.value = AppointmentDetailsUiState.Success(dto.toPatientScreenAppointmentItem())
            }.onFailure { exception ->
                _uiState.value = AppointmentDetailsUiState.Error(exception.message ?: "Unknown error fetching details")
            }
        }
    }

    fun cancelAppointment() {
        if (appointmentId == -1) {
            viewModelScope.launch {
                _cancellationEvent.emit(AppointmentCancellationEvent.Error("Cannot cancel: Invalid Appointment ID"))
            }
            return
        }

        viewModelScope.launch {
            _cancellationEvent.emit(AppointmentCancellationEvent.Loading)
            repository.deleteAppointment(appointmentId).onSuccess {
                _cancellationEvent.emit(AppointmentCancellationEvent.Success)
            }.onFailure { exception ->
                _cancellationEvent.emit(AppointmentCancellationEvent.Error(exception.message ?: "Failed to cancel appointment"))
            }
        }
    }
}

class PatientAppointmentDetailsViewModelFactory(
    private val repository: AppointmentRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatientAppointmentDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PatientAppointmentDetailsViewModel(repository, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class for PatientAppointmentDetails: ${modelClass.name}")
    }
}