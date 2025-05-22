package com.example.prescription_manag2.repository

import com.example.prescription_manag2.api.RetrofitClient
import com.example.prescription_manag2.entity.Doctor
import com.example.prescription_manag2.entity.HealthInstitution
import com.example.prescription_manag2.entity.Medication
import com.example.prescription_manag2.entity.Patient
import com.example.prescription_manag2.entity.Prescription
import com.example.prescription_manag2.entity.Specialty
import com.example.prescription_manag2.models.MedicationRequest
import com.example.prescription_manag2.models.PrescriptionRequest
import com.example.prescription_manag2.room.DoctorDao
import com.example.prescription_manag2.room.HealthInstitutionDao
import com.example.prescription_manag2.room.MedicationDao
import com.example.prescription_manag2.room.PatientDao
import com.example.prescription_manag2.room.PrescriptionDao
import com.example.prescription_manag2.room.SpecialtyDao


class PrescriptionRepository(
    private val prescriptionDao: PrescriptionDao,
    private val medicationDao: MedicationDao,
    private val patientDao: PatientDao,
    private val doctorDao: DoctorDao,
    private val specialtyDao: SpecialtyDao,
    private val healthInstitutionDao: HealthInstitutionDao,
    private val syncRepository: SyncRepository
) {
    private val apiService = RetrofitClient.prescriptionApiService

//    suspend fun getPatientsApi(): List<Patient> {
//        val response = apiService.getPatients()
//        return response.map { patientResponse ->
//            Patient(
//                id = patientResponse.id,
//                firstName = patientResponse.firstName,
//                lastName = patientResponse.lastName,
//                email = patientResponse.email,
//                age = patientResponse.age,
//                photoUrl = patientResponse.photoUrl,
//                address = patientResponse.address,
//                phone = patientResponse.phone,
//                password = patientResponse.password,
//                googleId = patientResponse.googleId
//            )
//        }
//    }
//
//    suspend fun getFirstDoctorApi(): Doctor {
//        val response = apiService.getFirstDoctor()
//        return Doctor(
//            id = response.id,
//            firstName = response.firstName,
//            lastName = response.lastName,
//            address = response.address,
//            phone = response.phone,
//            email = response.email,
//            password = response.password,
//            photoUrl = response.photoUrl,
//            googleId = response.googleId,
//            contactEmail = response.contactEmail,
//            contactPhone = response.contactPhone,
//            socialLinks = response.socialLinks,
//            specialtyId = response.specialtyId,
//            institutionId = response.institutionId
//        )
//    }

    suspend fun insertPrescriptionApi(prescriptionRequest: PrescriptionRequest): Long {
        val response = apiService.insertPrescription(prescriptionRequest)
        return response["id"] ?: -1
    }

    suspend fun insertMedicationsApi(medicationRequests: List<MedicationRequest>): List<Long> {
        val response = apiService.insertMedications(medicationRequests)
        return response["ids"] ?: emptyList()
    }

    suspend fun insertPrescription(prescription: Prescription): Long {
        val id = prescriptionDao.insertPrescription(prescription)
        syncRepository.schedulePrescriptionSync()
        return id
    }

    suspend fun insertMedications(medications: List<Medication>) {
        medicationDao.insertMedications(medications)
    }

    suspend fun insertPatient(patient: Patient) = patientDao.insertPatient(patient)
    suspend fun getAllPatients(): List<Patient> = patientDao.getAllPatients()

    suspend fun insertDoctor(doctor: Doctor): Long = doctorDao.insertDoctor(doctor)
    suspend fun getAllDoctors(): List<Doctor> = doctorDao.getAllDoctors()
    suspend fun getDoctorById(id: Int): Doctor = doctorDao.getDoctorById(id)

    suspend fun getPrescriptionsByPatientId(patientId: Int): List<Prescription> {
        return prescriptionDao.getPrescriptionsByPatientId(patientId)
    }

    suspend fun deleteMedicationById(medicationId: Long) {
        medicationDao.deleteMedicationById(medicationId)
    }

    suspend fun getMedicationsForPrescription(prescriptionId: Long): List<Medication> {
        return medicationDao.getMedicationsForPrescription(prescriptionId)
    }

    suspend fun getPatientById(patientId: Int): Patient {
        return patientDao.getPatientById(patientId)
    }

    suspend fun insertSpecialty(specialty: Specialty): Long {
        return specialtyDao.insertSpecialty(specialty)
    }

    suspend fun insertHealthInstitution(institution: HealthInstitution): Long {
        return healthInstitutionDao.insertHealthInstitution(institution)
    }

    suspend fun getHealthInstitutionForDoctor(doctorId: Int): HealthInstitution? {
        return healthInstitutionDao.getHealthInstitutionForDoctor(doctorId)
    }

    suspend fun getSpecialtyForDoctor(doctorId: Int): Specialty? {
        return specialtyDao.getSpecialtyForDoctor(doctorId)
    }


}
