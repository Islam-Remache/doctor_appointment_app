package com.example.learning.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learning.model.Doctor
import com.example.learning.model.SpecialtyResponse
import com.example.learning.repository.DoctorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
data class DoctorsListUiState(
    val specialties: List<SpecialtyResponse> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
class DoctorsListViewModel(private val repository: DoctorRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(DoctorsListUiState())
    val uiState: StateFlow<DoctorsListUiState> = _uiState.asStateFlow()

    private val _allDoctors = MutableStateFlow<List<Doctor>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedSpecialtyId = MutableStateFlow<Int?>(null)
    val selectedSpecialtyId: StateFlow<Int?> = _selectedSpecialtyId.asStateFlow()

    val filteredDoctors: StateFlow<List<Doctor>> = combine(
        _allDoctors,
        _searchQuery,
        _selectedSpecialtyId
    ) { doctors, query, specialtyId ->
        doctors.filter { doctor ->
            val matchesSpecialty = specialtyId == null || doctor.specialty_id == specialtyId
            val matchesQuery = query.isBlank() ||
                    doctor.fullName.contains(query, ignoreCase = true) ||
                    doctor.email.contains(query, ignoreCase = true)
            matchesSpecialty && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val doctorsData = repository.getDoctors()
                val specialtiesData = repository.getSpecialties()
                _allDoctors.value = doctorsData
                _uiState.update {
                    it.copy(
                        specialties = specialtiesData,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "An unknown error occurred") }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSpecialtySelected(specialtyId: Int?) {
        _selectedSpecialtyId.value = if (_selectedSpecialtyId.value == specialtyId) null else specialtyId
    }

    fun retryLoad() {
        loadInitialData()
    }
}