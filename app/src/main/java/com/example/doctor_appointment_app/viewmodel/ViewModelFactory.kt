package com.example.learning.viewmodel
import com.example.learning.viewmodel.DoctorsListViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.SavedStateHandle // For the ViewModel constructor
import androidx.lifecycle.createSavedStateHandle // For getting SSH from CreationExtras
import androidx.lifecycle.viewmodel.CreationExtras // For the modern factory approach
import com.example.learning.repository.DoctorRepository
import com.example.learning.repository.DoctorRepositoryImpl
import com.example.learning.network.NetworkModule
// Factory for DoctorsListViewModel
class DoctorsListViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T { // Old create method is fine here
        if (modelClass.isAssignableFrom(DoctorsListViewModel::class.java)) {
            val repository: DoctorRepository = DoctorRepositoryImpl(NetworkModule.apiService)
            return DoctorsListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
// Factory for DoctorDetailViewModel
class DoctorDetailViewModelFactory() : ViewModelProvider.Factory { // <<<--- CONSTRUCTOR IS EMPTY ()
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T { // <<<--- USES CreationExtras
        if (modelClass.isAssignableFrom(DoctorDetailViewModel::class.java)) {
            // Get SavedStateHandle from CreationExtras
            val savedStateHandle = extras.createSavedStateHandle() // <<<--- Gets SavedStateHandle INTERNALLY

            // Manually create other dependencies
            val repository: DoctorRepository = DoctorRepositoryImpl(NetworkModule.apiService)

            // Pass the internally obtained savedStateHandle to the ViewModel
            return DoctorDetailViewModel(repository, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

}