package com.example.prescription_manag2.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prescription_manag2.entity.Doctor
import com.example.prescription_manag2.entity.HealthInstitution
import com.example.prescription_manag2.entity.Medication
import com.example.prescription_manag2.entity.Patient
import com.example.prescription_manag2.entity.Prescription
import com.example.prescription_manag2.entity.SocialLinks
import com.example.prescription_manag2.entity.Specialty
import com.example.prescription_manag2.models.DoctorModel
import com.example.prescription_manag2.models.HealthInstitutionModel
import com.example.prescription_manag2.repository.PrescriptionRepository
import com.example.prescription_manag2.models.PrescriptionModel
import com.example.prescription_manag2.models.PatientModel
import com.example.prescription_manag2.models.MedicationModel
import com.example.prescription_manag2.models.MedicationRequest
import com.example.prescription_manag2.models.PrescriptionRequest
import com.example.prescription_manag2.models.SpecialtyModel
import kotlinx.coroutines.launch


class PrescriptionViewModel(private val prescriptionRepository: PrescriptionRepository, ):ViewModel()  {

    val patients = mutableStateOf(emptyList<Patient>())
    val doctorId = mutableStateOf<Int?>(null)
    val prescriptions = mutableStateOf<List<PrescriptionModel>>(emptyList())
    val patient = mutableStateOf<Patient?>(null)
    private val isLoading = mutableStateOf(false)
    private val errorMessage = mutableStateOf<String?>(null)

    fun insertMedicationsApi(medications: List<Medication>) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val medicationRequests = medications.map { medication ->
                    MedicationRequest(
                        id = medication.id,
                        prescriptionId = medication.prescriptionId,
                        name = medication.name ?: "",
                        dosage = medication.dosage ?: "",
                        frequency = medication.frequency ?: "",
                        duration = medication.duration ?: ""
                    )
                }

                prescriptionRepository.insertMedicationsApi(medicationRequests)
                errorMessage.value = null
            } catch (e: Exception) {
                errorMessage.value = "Error when registering medications: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun insertPrescriptionApi(prescription: Prescription, onResult: (Long) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val prescriptionRequest = PrescriptionRequest(
                    id = prescription.id,
                    patientId = prescription.patientId,
                    doctorId = prescription.doctorId,
                    instructions = prescription.instructions,
                    createdAt = prescription.createdAt,
                    expiresAt = prescription.expiresAt
                )
                val prescriptionId = prescriptionRepository.insertPrescriptionApi(prescriptionRequest)
                errorMessage.value = null
                onResult(prescriptionId)
            } catch (e: Exception) {
                errorMessage.value = "Erreur lors de l'enregistrement de la prescription: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

//    init {
//        viewModelScope.launch {
//            handleUserMockData()
//            handleMedecinMockData()
//        }
//    }
    init {
        viewModelScope.launch {
//            loadUsers()
//            loadMedecin()
            handlePatientMockData()
            handleDoctorMockData()

        }
    }

//    private suspend fun loadUsers() {
//        isLoading.value = true
//        try {
//            users.value = prescriptionRepository.getUsersApi()
//            errorMessage.value = null
//        } catch (e: Exception) {
//            errorMessage.value = "Erreur lors du chargement des utilisateurs: ${e.message}"
//        } finally {
//            isLoading.value = false
//        }
//    }
//
//    private suspend fun loadMedecin() {
//        isLoading.value = true
//        try {
//            val medecin = prescriptionRepository.getFirstMedecinApi()
//            medecinId.value = medecin.id
//            errorMessage.value = null
//        } catch (e: Exception) {
//            errorMessage.value = "Erreur lors du chargement du médecin: ${e.message}"
//        } finally {
//            isLoading.value = false
//        }
//    }
//

private suspend fun handlePatientMockData() {
    val existingPatients = prescriptionRepository.getAllPatients()
    if (existingPatients.isEmpty()) {
        val mockPatients = listOf(
            Patient(
                id = 1,
                firstName = "Sara",
                lastName = "Khelifi",
                address = "20 rue Mohamed Belouizdad, Alger",
                phone = "+213662334455",
                email = "sara.khelifi@example.com",
                password = "hashed_password_123",
                age = 28,
                photoUrl = "https://example.com/photo_sara.jpg",
                googleId = "google-id-sara-789"
            )
        )
        mockPatients.forEach { patient ->
            prescriptionRepository.insertPatient(patient)
        }
        patients.value = prescriptionRepository.getAllPatients()
    } else {
        patients.value = existingPatients
    }
}

    private suspend fun handleDoctorMockData() {
        val doctors = prescriptionRepository.getAllDoctors()
        if (doctors.isEmpty()) {
            val specialty = Specialty(
                id = 1,
                label = "Cardiologue"
            )
            val specialtyId = prescriptionRepository.insertSpecialty(specialty)

            val institution = HealthInstitution(
                id = 1,
                name = "El Hayat Clinic",
                address = "Djasr Kasentina, Algiers, Algeria",
                latitude = 36.7361,
                longitude = 3.0903,
                type = "clinic"
            )
            val institutionId = prescriptionRepository.insertHealthInstitution(institution)

            val doctor = Doctor(
                id = 1,
                firstName = "Merabet",
                lastName = "Amine",
                address = "El Hayat Clinic, Djasr Kasentina, Algiers, Algeria",
                phone = "+213 555 123 456",
                email = "amine.merabet@example.com",
                password = "securePassword123",
                photoUrl = "https://example.com/photos/merabet.jpg",
                googleId = "google-id-example",
                contactEmail = "contact@elhayatclinic.com",
                contactPhone = "+213 555 654 321",
                socialLinks = SocialLinks(
                    linkedin = "https://linkedin.com/in/merabetamine",
                    twitter  = "https://twitter.com/merabetamine"
                ),
                specialtyId = specialtyId.toInt(),
                institutionId = institutionId.toInt()
            )
            val id = prescriptionRepository.insertDoctor(doctor)
            doctorId.value = id.toInt()
        } else {
            doctorId.value = doctors.first().id
        }
    }


    fun insertPrescription(prescription: Prescription, onResult: (Long) -> Unit) {
        viewModelScope.launch {
            val prescriptionId = prescriptionRepository.insertPrescription(prescription)
            onResult(prescriptionId)
        }
    }

    fun insertMedications(medications: List<Medication>) {
        viewModelScope.launch {
            prescriptionRepository.insertMedications(medications)
        }
    }

//    fun getPrescriptionsForUser(userId: Int) {
//        viewModelScope.launch {
//            prescriptions.value = prescriptionRepository.getPrescriptionsByUserId(userId)
//        }
//    }




    @RequiresApi(Build.VERSION_CODES.O)
    fun getCompletePrescriptionDataRoom(patientId: Int) {
        viewModelScope.launch {
            val prescriptionListRoom = prescriptionRepository.getPrescriptionsByPatientId(patientId)
            val currentPatient = prescriptionRepository.getPatientById(patientId)

            prescriptions.value = prescriptionListRoom.map { prescription ->
                val medications = prescriptionRepository.getMedicationsForPrescription(prescription.id)
                val modelMedications = medications.map { medicationEntity ->
                    MedicationModel(
                        id = medicationEntity.id,
                        name = medicationEntity.name,
                        dosage = medicationEntity.dosage,
                        frequency = medicationEntity.frequency,
                        duration = medicationEntity.duration,
                        prescriptionId = prescription.id,
                        syncStatus = medicationEntity.syncStatus
                    )
                }

                val doctor = prescriptionRepository.getDoctorById(prescription.doctorId)
                val specialty = prescriptionRepository.getSpecialtyForDoctor(doctor.id)
                val healthInstitution = prescriptionRepository.getHealthInstitutionForDoctor(doctor.id)

                val specialtyModel = specialty?.let { SpecialtyModel(id = it.id, label = it.label) }
                val institutionModel = healthInstitution?.let { HealthInstitutionModel(id = it.id, name = it.name, address = it.address) }

                val doctorModel = DoctorModel(
                    id = doctor.id,
                    firstName = doctor.firstName,
                    lastName = doctor.lastName,
                    address = doctor.address,
                    phone = doctor.phone,
                    email = doctor.email,
                    password = doctor.password,
                    photoUrl = doctor.photoUrl ?: "",
                    googleId = doctor.googleId,
                    contactEmail = doctor.contactEmail,
                    contactPhone = doctor.contactPhone,
                    socialLinks = doctor.socialLinks,
                    specialty = specialtyModel,
                    healthInstitution = institutionModel
                )

                val patientModel = PatientModel(
                    id = currentPatient.id,
                    firstName = currentPatient.firstName,
                    lastName = currentPatient.lastName,
                    age = currentPatient.age,
                    photoUrl = currentPatient.photoUrl ?: "",
                    address = currentPatient.address,
                    phone = currentPatient.phone,
                    email = currentPatient.email,
                    password = currentPatient.password,
                    googleId = currentPatient.googleId
                )

                PrescriptionModel(
                    id = prescription.id,
                    doctor = doctorModel,
                    patient = patientModel,
                    instructions = prescription.instructions,
                    createdAt = prescription.createdAt,
                    expiresAt = prescription.expiresAt,
                    medications = modelMedications,
                    status = prescription.status,
                    syncStatus = prescription.syncStatus
                )
            }

            patient.value = currentPatient
        }
    }

    fun deleteMedication(medicationId: Long) {
        viewModelScope.launch {
            prescriptionRepository.deleteMedicationById(medicationId)
        }
    }

}

