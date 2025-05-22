package com.example.prescription_manag2.api.service


import com.example.prescription_manag2.entity.Doctor
import com.example.prescription_manag2.entity.HealthInstitution
import com.example.prescription_manag2.entity.Patient
import com.example.prescription_manag2.entity.Specialty
import com.example.prescription_manag2.models.DoctorResponse
import com.example.prescription_manag2.models.HealthInstitutionResponse
import com.example.prescription_manag2.models.MedicationRequest
import com.example.prescription_manag2.models.MedicationResponse
import com.example.prescription_manag2.models.PatientResponse
import com.example.prescription_manag2.models.PrescriptionRequest
import com.example.prescription_manag2.models.PrescriptionResponse
import com.example.prescription_manag2.models.SpecialtyResponse
import retrofit2.http.*

interface PrescriptionApiService {
//    @GET("users")
//    suspend fun getPatients(): List<PatientResponse>
//
//    @GET("medecins/first")
//    suspend fun getFirstMedecin(): DoctorResponse

    @POST("prescriptions")
    suspend fun insertPrescription(@Body prescription: PrescriptionRequest): Map<String, Long>


    @POST("medications")
    suspend fun insertMedications(@Body medications: List<MedicationRequest>): Map<String, List<Long>>

    @GET("patients")
    suspend fun getAllPatients(): List<PatientResponse>

    @GET("doctors")
    suspend fun getAllDoctors(): List<DoctorResponse>

    @GET("health_institutions")
    suspend fun getAllHealthInstitutions(): List<HealthInstitutionResponse>

    @GET("specialties")
    suspend fun getAllSpecialties(): List<SpecialtyResponse>

    @GET("prescriptions")
    suspend fun getAllPrescriptions(): List<PrescriptionResponse>

    @GET("medications")
    suspend fun getAllMedications(): List<MedicationResponse>
}