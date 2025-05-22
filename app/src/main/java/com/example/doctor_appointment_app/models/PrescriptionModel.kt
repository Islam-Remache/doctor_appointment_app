package com.example.prescription_manag2.models

import com.example.prescription_manag2.entity.Prescription.Companion.getCurrentTimestamp
import com.example.prescription_manag2.entity.SocialLinks
import com.example.prescription_manag2.entity.SyncStatus

// Api Data models

data class PrescriptionResponse(
    val id: Long,
    val patientId: Int,
    val doctorId: Int,
    val instructions: String,
    val createdAt: String,
    val expiresAt: String,
    val status: String,
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)

data class MedicationResponse(
    val id: Long,
    val prescriptionId: Long,
    val name: String,
    val dosage: String,
    val frequency: String,
    val duration: String,
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)


data class PatientResponse(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val address: String?,
    val phone: String?,
    val email: String,
    val age: Int?,
    val password: String?,
    val photoUrl: String?,
    val googleId: String?
)

data class DoctorResponse(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val address: String?,
    val phone: String?,
    val email: String,
    val password: String?,
    val photoUrl: String?,
    val googleId: String?,
    val contactEmail: String?,
    val contactPhone: String?,
    val socialLinks: SocialLinks?,
    val specialtyId: Int?,
    val institutionId: Int?
)


data class SpecialtyResponse(
    val id: Int,
    val label: String
)

data class HealthInstitutionResponse(
    val id: Int,
    val name: String,
    val address: String?,
    val latitude: Double,
    val longitude: Double,
    val type: String
)


data class PrescriptionRequest(
    val id: Long,
    val patientId: Int,
    val doctorId: Int,
    val instructions: String,
    val createdAt: String = getCurrentTimestamp(),
    val expiresAt: String,
)

data class MedicationRequest(
    val id: Long,
    val prescriptionId: Long,
    val name: String,
    val dosage: String,
    val frequency: String,
    val duration: String,
)

// Room Data models


data class PrescriptionModel(
    val id: Long,
    val doctor: DoctorModel,
    val patient: PatientModel,
    val instructions: String,
    val createdAt: String,
    val expiresAt: String,
    val medications: List<MedicationModel>,
    val status: String,
    var syncStatus: SyncStatus = SyncStatus.SYNCED
)

data class SpecialtyModel(
    val id: Int,
    val label: String
)

data class HealthInstitutionModel(
    val id: Int,
    val name: String,
    val address: String?
)

data class DoctorModel(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val address: String?,
    val phone: String?,
    val email: String,
    val password: String?,
    val photoUrl: String?,
    val googleId: String?,
    val contactEmail: String?,
    val contactPhone: String?,
    val socialLinks: SocialLinks?,
    val specialty: SpecialtyModel?,
    val healthInstitution: HealthInstitutionModel?
)


data class PatientModel(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val address: String?,
    val phone: String?,
    val email: String,
    val password: String?,
    val age: Int?,
    val photoUrl: String?,
    val googleId: String?
)


data class MedicationModel(
    val id: Long,
    val prescriptionId: Long,
    val name: String,
    val dosage: String?,
    val frequency: String?,
    val duration: String?,
    var syncStatus: SyncStatus = SyncStatus.SYNCED
)






//
//data class PrescriptionModel(
//    val id: Long,
//    val doctor: MedecinModel,
//    val patient: PatientModel,
//    val createdDate: LocalDate,
//    val expiryDate: LocalDate,
//    val medications: List<MedicationModel>,
//    val instructions: String,
//    val status: String,
//    var syncStatus: SyncStatus = SyncStatus.SYNCED
//)
//
//data class MedecinModel(
//    val id: Int,
//    val name: String,
//    val specialty: String,
//    val image: Int,
//    val nameClinic: String,
//    val addressClinic: String
//)
//
//data class PatientModel(
//    val id: Int,
//    val name: String,
//    val age: Int,
//    val imageRes: Int
//)
//
//data class MedicationModel(
//    val id: Long,
//    val name: String,
//    val dosage: String,
//    val frequency: String,
//    val duration: String
//)
