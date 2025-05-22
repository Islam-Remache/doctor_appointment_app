package com.example.dzdoc.ui.viewmodel

// Removed: import androidx.lifecycle.LiveData
// Removed: import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dzdoc.data.repository.AppointmentRepository
import com.example.dzdoc.ui.model.Appointment // Ensure this import is correct
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppointmentViewModel(
    private val repository: AppointmentRepository
) : ViewModel() {

    // UI State sealed class (or interface)
    sealed interface UiState { // Changed to interface for modern Kotlin style, class is also fine
        object Initial : UiState
        object Loading : UiState
        data class Success(val appointment: Appointment) : UiState
        data class Error(val message: String) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Converted to StateFlow
    private val _actionSuccessEvent = MutableStateFlow<Boolean?>(null)
    val actionSuccessEvent: StateFlow<Boolean?> = _actionSuccessEvent.asStateFlow()

    // Converted to StateFlow
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun getAppointmentDetails(appointmentId: Int) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            repository.getAppointmentById(appointmentId)
                .onSuccess { appointment ->
                    _uiState.value = UiState.Success(appointment)
                }
                .onFailure { error ->
                    val errorMessageText = error.message ?: "Unknown error loading details"
                    _uiState.value = UiState.Error(errorMessageText)
                    _errorMessage.value = errorMessageText // Also set the general error message
                }
        }
    }

    fun confirmAppointment(appointmentId: Int, doctorId: Int) {
        val previousState = _uiState.value // Store previous state in case of failure
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            repository.confirmAppointment(appointmentId, doctorId)
                .onSuccess { updatedAppointment ->
                    _uiState.value = UiState.Success(updatedAppointment)
                    _actionSuccessEvent.value = true
                }
                .onFailure { error ->
                    val errorMessageText = error.message ?: "Failed to confirm appointment"
                    _errorMessage.value = errorMessageText
                    // Restore previous state or set to specific error for appointment details
                    if (previousState is UiState.Success) {
                        _uiState.value = previousState // Keep showing current details, error will be displayed via _errorMessage
                    } else {
                        _uiState.value = UiState.Error(errorMessageText) // Fallback if no previous success state
                    }
                }
        }
    }

    fun cancelAppointment(appointmentId: Int, doctorId: Int) {
        val previousState = _uiState.value // Store previous state in case of failure
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            repository.cancelAppointment(appointmentId, doctorId)
                .onSuccess { updatedAppointment ->
                    _uiState.value = UiState.Success(updatedAppointment)
                    _actionSuccessEvent.value = true
                }
                .onFailure { error ->
                    val errorMessageText = error.message ?: "Failed to cancel appointment"
                    _errorMessage.value = errorMessageText
                    // Restore previous state or set to specific error for appointment details
                    if (previousState is UiState.Success) {
                        _uiState.value = previousState // Keep showing current details, error will be displayed via _errorMessage
                    } else {
                        _uiState.value = UiState.Error(errorMessageText) // Fallback if no previous success state
                    }
                }
        }
    }

    // Call this from UI after the event has been handled
    fun consumeActionSuccessEvent() {
        _actionSuccessEvent.value = null
    }

    // Call this from UI after the error message has been shown
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}

// Factory to create AppointmentViewModel instances (remains the same)
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