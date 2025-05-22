package com.example.learning.viewmodel
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learning.model.AppointmentCreateRequest
import com.example.learning.model.Doctor
import com.example.learning.model.HealthInstitution
import com.example.learning.model.SpecialtyResponse
import com.example.learning.model.TimeSlot
import com.example.learning.repository.DoctorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
data class DoctorDetailUiState(
    val doctor: Doctor? = null,
    val specialtyName: String = "General",
    val allSpecialties: List<SpecialtyResponse> = emptyList(),
    val healthInstitution: HealthInstitution? = null,
    val timeSlots: List<TimeSlot> = emptyList(),
    val selectedDate: LocalDate? = null,
    val selectedTimeSlot: TimeSlot? = null,
    val isLoadingInitialData: Boolean = true,
    val isLoadingSlots: Boolean = false,
    val isLoadingClinic: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val isBooking: Boolean = false,
    val bookingMessage: String? = null
)
class DoctorDetailViewModel(
    private val repository: DoctorRepository,
    savedStateHandle: SavedStateHandle // This is correctly injected by the factory
) : ViewModel() {
    private val doctorId: Int = checkNotNull(savedStateHandle["doctorId"]) {
        "Doctor ID not found in SavedStateHandle. Ensure it's passed as a NavArgument."
    }

    private val _uiState = MutableStateFlow(DoctorDetailUiState())
    val uiState: StateFlow<DoctorDetailUiState> = _uiState.asStateFlow()

    init {
        loadInitialDoctorAndSpecialties()
    }

    private fun addErrorMessage(message: String) {
        _uiState.update { it.copy(errorMessages = it.errorMessages + message) }
    }

    fun clearErrorMessages() {
        _uiState.update { it.copy(errorMessages = emptyList()) }
    }

    fun retryInitialLoad() {
        _uiState.update { it.copy(errorMessages = emptyList()) }
        loadInitialDoctorAndSpecialties()
    }

    private fun loadInitialDoctorAndSpecialties() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingInitialData = true, errorMessages = emptyList()) }
            try {
                val doctorResult = repository.getDoctorById(doctorId)
                val specialtiesResult = repository.getSpecialties()

                if (doctorResult != null) {
                    val specialtyName = specialtiesResult.find { it.id == doctorResult.specialty_id }?.label ?: "General"
                    _uiState.update {
                        it.copy(
                            doctor = doctorResult,
                            specialtyName = specialtyName,
                            allSpecialties = specialtiesResult,
                            isLoadingInitialData = false
                        )
                    }
                    doctorResult.health_institution_id?.let { institutionId ->
                        if (institutionId > 0) {
                            loadHealthInstitution(institutionId)
                        } else {
                            _uiState.update { it.copy(healthInstitution = null) }
                        }
                    } ?: _uiState.update { it.copy(healthInstitution = null) }
                } else {
                    addErrorMessage("Doctor not found.")
                    _uiState.update { it.copy(isLoadingInitialData = false) }
                }
            } catch (e: Exception) {
                addErrorMessage(e.message ?: "Failed to load doctor details.")
                _uiState.update { it.copy(isLoadingInitialData = false) }
            }
        }
    }

    private fun loadHealthInstitution(institutionId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingClinic = true) }
            try {
                val institution = repository.getHealthInstitution(institutionId)
                _uiState.update { it.copy(healthInstitution = institution, isLoadingClinic = false) }
            } catch (e: Exception) {
                addErrorMessage(e.message ?: "Failed to load clinic details.")
                _uiState.update { it.copy(isLoadingClinic = false, healthInstitution = null) }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun selectDate(date: LocalDate) {
        _uiState.update {
            it.copy(
                selectedDate = date,
                selectedTimeSlot = null,
                timeSlots = emptyList(),
                errorMessages = it.errorMessages.filterNot { msg -> msg.contains("slots", ignoreCase = true) }
            )
        }
        loadTimeSlotsForSelectedDate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadTimeSlotsForSelectedDate() {
        val currentDate = _uiState.value.selectedDate
        // Use the class property doctorId directly
        if (currentDate != null) { // doctorId is guaranteed by checkNotNull
            viewModelScope.launch {
                _uiState.update { it.copy(isLoadingSlots = true) }
                try {
                    val dateString = currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    val slots = repository.getDoctorSlots(this@DoctorDetailViewModel.doctorId, dateString)
                    _uiState.update { it.copy(timeSlots = slots, isLoadingSlots = false) }
                } catch (e: Exception) {
                    addErrorMessage(e.message ?: "Failed to load time slots.")
                    _uiState.update { it.copy(isLoadingSlots = false, timeSlots = emptyList()) }
                }
            }
        }
    }

    fun selectTimeSlot(slot: TimeSlot) {
        _uiState.update { it.copy(selectedTimeSlot = slot) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun bookAppointment() {
        val currentDoctor = _uiState.value.doctor
        val currentTimeSlot = _uiState.value.selectedTimeSlot

        if (currentDoctor != null && currentTimeSlot != null) {
            viewModelScope.launch {
                _uiState.update { it.copy(isBooking = true, bookingMessage = null) }
                val patientId = 1 // Placeholder: Replace with actual logged-in patient ID
                val request = AppointmentCreateRequest(
                    patient_id = patientId,
                    doctor_id = currentDoctor.id,
                    time_slot_id = currentTimeSlot.id
                )
                val result = repository.bookAppointment(request)
                result.fold(
                    onSuccess = {
                        _uiState.update {
                            it.copy(
                                isBooking = false,
                                bookingMessage = "Appointment booked successfully!",
                                selectedTimeSlot = null
                            )
                        }
                        loadTimeSlotsForSelectedDate()
                    },
                    onFailure = { exception ->
                        Log.e("BookingError", "VM Booking failed: ${exception.message}", exception)
                        _uiState.update {
                            it.copy(
                                isBooking = false,
                                bookingMessage = "Booking failed: ${exception.message}"
                            )
                        }
                    }
                )
            }
        } else {
            _uiState.update { it.copy(bookingMessage = "Please select a doctor and time slot first.") }
        }
    }

    fun clearBookingMessage() {
        _uiState.update { it.copy(bookingMessage = null) }
    }

}