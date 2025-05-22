package com.example.dzdoc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dzdoc.data.remote.dto.AppointmentDetailsDto
import com.example.dzdoc.data.repository.AppointmentRepository
import com.example.dzdoc.data.AppointmentStatus
import com.example.dzdoc.ui.model.PatientScreenAppointmentItem
import com.example.dzdoc.ui.model.toPatientScreenAppointmentItem // Correct import for mapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed interface PatientAppointmentsUiState {
    object Loading : PatientAppointmentsUiState
    data class Success(val appointments: List<PatientScreenAppointmentItem>) : PatientAppointmentsUiState
    data class Error(val message: String) : PatientAppointmentsUiState
}

class PatientAppointmentsViewModel(
    private val repository: AppointmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PatientAppointmentsUiState>(PatientAppointmentsUiState.Loading)
    val uiState: StateFlow<PatientAppointmentsUiState> = _uiState.asStateFlow()
    private val currentPatientId = 1

    init {
        fetchPatientAppointments()
    }

    fun fetchPatientAppointments() {
        viewModelScope.launch {
            _uiState.value = PatientAppointmentsUiState.Loading
            repository.getPatientAppointments(currentPatientId).onSuccess { dtos ->
                _uiState.value = PatientAppointmentsUiState.Success(dtos.map { it.toPatientScreenAppointmentItem() })
            }.onFailure { exception ->
                _uiState.value = PatientAppointmentsUiState.Error(exception.message ?: "Unknown error fetching appointments")
            }
        }
    }


    fun refreshAppointments() {
        fetchPatientAppointments()
    }

    fun cancelAppointmentFromList(appointmentId: Int) {
        viewModelScope.launch {


            repository.deleteAppointment(appointmentId).onSuccess {

                fetchPatientAppointments()

            }.onFailure { exception ->

            }
        }
    }
}

class PatientAppointmentsViewModelFactory(
    private val repository: AppointmentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatientAppointmentsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PatientAppointmentsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class for PatientAppointmentsViewModel")
    }
}