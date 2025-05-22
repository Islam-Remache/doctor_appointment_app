package com.example.learning.repository
import com.example.learning.model.AppointmentCreateRequest
import com.example.learning.model.Doctor
import com.example.learning.model.HealthInstitution
import com.example.learning.model.SpecialtyResponse
import com.example.learning.model.TimeSlot
import com.example.learning.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
class DoctorRepositoryImpl(private val apiService: ApiService) : DoctorRepository {
    override suspend fun getDoctors(): List<Doctor> {
        return withContext(Dispatchers.IO) {
            apiService.getDoctors()
        }
    }

    override suspend fun getSpecialties(): List<SpecialtyResponse> {
        return withContext(Dispatchers.IO) {
            apiService.getSpecialties()
        }
    }

    override suspend fun getDoctorById(doctorId: Int): Doctor? {

        return withContext(Dispatchers.IO) {
            try {
                apiService.getDoctors().find { it.id == doctorId }
            } catch (e: Exception) {
                // Log error
                null
            }
        }
    }

    override suspend fun getDoctorSlots(doctorId: Int, date: String): List<TimeSlot> {
        return withContext(Dispatchers.IO) {
            apiService.getDoctorSlots(doctorId, date)
        }
    }

    override suspend fun bookAppointment(appointmentRequest: AppointmentCreateRequest): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                apiService.bookAppointment(appointmentRequest)
                Result.success(Unit)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Result.failure(IOException("Server error ${e.code()}: ${errorBody ?: e.message()}", e))
            } catch (e: IOException) {
                Result.failure(IOException("Network error. Please check your connection.", e))
            } catch (e: Exception) {
                Result.failure(Exception("Booking failed: ${e.message}", e))
            }
        }
    }

    override suspend fun getHealthInstitution(institutionId: Int): HealthInstitution? {
        return withContext(Dispatchers.IO) {
            try {
                apiService.getHealthInstitution(institutionId)
            } catch (e: Exception) {
                // Log error
                null
            }
        }
    }

}