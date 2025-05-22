package com.example.prescription_manag2.room

import androidx.room.*
import com.example.prescription_manag2.entity.Doctor
import com.example.prescription_manag2.entity.HealthInstitution
import com.example.prescription_manag2.entity.Medication
import com.example.prescription_manag2.entity.Patient
import com.example.prescription_manag2.entity.Prescription
import com.example.prescription_manag2.entity.Specialty


@Dao
interface DoctorDao {
    @Query("SELECT * FROM doctors WHERE id = :id")
    suspend fun getDoctorById(id: Int): Doctor

    @Query("SELECT * FROM doctors")
    suspend fun getAllDoctors(): List<Doctor>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoctor(doctor: Doctor): Long
}

@Dao
interface SpecialtyDao {

    @Query("SELECT * FROM specialties")
    suspend fun getAllSpecialties(): List<Specialty>

    @Query("SELECT * FROM specialties WHERE id = :specialtyId")
    suspend fun getSpecialtyById(specialtyId: Int): Specialty


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpecialty(specialty: Specialty): Long

    @Query("""
    SELECT s.* FROM specialties s
    INNER JOIN doctors d ON d.specialty_id  = s.id
    WHERE d.id = :doctorId
""")
    suspend fun getSpecialtyForDoctor(doctorId: Int): Specialty?


}

@Dao
interface HealthInstitutionDao {

    @Query("SELECT * FROM health_institutions")
    suspend fun getAllHealthInstitutions(): List<HealthInstitution>


    @Query("SELECT * FROM health_institutions WHERE id = :institutionId")
    suspend fun getHealthInstitutionById(institutionId: Int): HealthInstitution



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthInstitution(institution: HealthInstitution): Long

    @Query("""
    SELECT hi.* FROM health_institutions hi
    INNER JOIN doctors d ON d.institution_id  = hi.id
    WHERE d.id = :doctorId
""")
    suspend fun getHealthInstitutionForDoctor(doctorId: Int): HealthInstitution?

}


@Dao
interface MedicationDao {

    @Query("DELETE FROM medications WHERE id = :medicationId")
    suspend fun deleteMedicationById(medicationId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedications(medications: List<Medication>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: Medication): Long

    @Query("SELECT * FROM medications")
    suspend fun getAllMedications(): List<Medication>

    @Query("SELECT * FROM medications WHERE prescription_id = :prescriptionId")
    suspend fun getMedicationsForPrescription(prescriptionId: Long): List<Medication>

    @Update
    suspend fun updateMedications(medications: List<Medication>)

    @Query("SELECT * FROM medications WHERE syncStatus = 'PENDING_SYNC'")
    suspend fun getMedicationsToSync(): List<Medication>
}

@Dao
interface PrescriptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrescription(prescription: Prescription): Long

    @Query("SELECT * FROM prescriptions")
    suspend fun getAllPrescriptions(): List<Prescription>

    @Transaction
    @Query("SELECT * FROM prescriptions WHERE patient_id  = :patientId")
    suspend fun getPrescriptionsByPatientId(patientId: Int): List<Prescription>

    @Query("SELECT * FROM prescriptions WHERE syncStatus = 'PENDING_SYNC'")
    suspend fun getPrescriptionsToSync(): List<Prescription>

    @Update
    suspend fun updatePrescription(prescription: Prescription)

    @Update
    suspend fun updatePrescriptions(prescriptions: List<Prescription>)
}

@Dao
interface PatientDao {
    @Query("SELECT * FROM patients")
    suspend fun getAllPatients(): List<Patient>

    @Query("SELECT * FROM patients WHERE id = :patientId")
    suspend fun getPatientById(patientId: Int): Patient

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: Patient): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatients(patients: List<Patient>)
}

data class PrescriptionWithDetails(
    @Embedded val prescription: Prescription,
    @Relation(
        parentColumn = "patientId",
        entityColumn = "id"
    )
    val patient: Patient,
    @Relation(
        parentColumn = "doctorId",
        entityColumn = "id"
    )
    val doctor: Doctor,
    @Relation(
        parentColumn = "id",
        entityColumn = "prescriptionId"
    )
    val medications: List<Medication>
)
