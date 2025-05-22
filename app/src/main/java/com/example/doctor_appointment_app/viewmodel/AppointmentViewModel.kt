package com.example.doctor_appointment_app.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.doctor_appointment_app.model.appointment.Appointment
import com.example.doctor_appointment_app.model.appointment.AppointmentStatus
import com.example.doctor_appointment_app.repository.AppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class AppointmentViewModel(
    private val repository: AppointmentRepository
) : ViewModel() {

    // UI State
    sealed class UiState {
        object Loading : UiState()
        data class Success(val appointment: Appointment) : UiState()
        data class Error(val message: String) : UiState()
        object Initial : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _actionSuccessEvent = MutableLiveData<Boolean>()
    val actionSuccessEvent: LiveData<Boolean> = _actionSuccessEvent

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getAppointmentDetails(appointmentId: Int) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            repository.getAppointmentById(appointmentId)
                .onSuccess { appointment ->
                    _uiState.value = UiState.Success(appointment)
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.message ?: "Unknown error")
                    _errorMessage.value = error.message ?: "Failed to load appointment details"
                }
        }
    }

    fun confirmAppointment(appointmentId: Int, doctorId: Int) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            repository.confirmAppointment(appointmentId, doctorId)
                .onSuccess { updatedAppointment ->
                    _uiState.value = UiState.Success(updatedAppointment)
                    _actionSuccessEvent.value = true
                }
                .onFailure { error ->
                    _errorMessage.value = error.message ?: "Failed to update appointment status"
                    // Keep the current appointment data but show error
                    if (_uiState.value is UiState.Success) {
                        // No change to the UI state, just show error
                    } else {
                        _uiState.value = UiState.Error(error.message ?: "Unknown error")
                    }
                }
        }
    }

    fun cancelAppointment(appointmentId: Int, doctorId: Int) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            repository.cancelAppointment(appointmentId, doctorId)
                .onSuccess { updatedAppointment ->
                    _uiState.value = UiState.Success(updatedAppointment)
                    _actionSuccessEvent.value = true
                }
                .onFailure { error ->
                    _errorMessage.value = error.message ?: "Failed to update appointment status"
                    // Keep the current appointment data but show error
                    if (_uiState.value is UiState.Success) {
                        // No change to the UI state, just show error
                    } else {
                        _uiState.value = UiState.Error(error.message ?: "Unknown error")
                    }
                }
        }
    }
}

// Factory to create AppointmentViewModel instances
class AppointmentViewModelFactory(
    private val repository: AppointmentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppointmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppointmentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}