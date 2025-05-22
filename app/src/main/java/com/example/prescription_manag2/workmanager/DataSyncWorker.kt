package com.example.prescription_manag2.workmanager

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.prescription_manag2.api.RetrofitClient
import com.example.prescription_manag2.entity.Doctor
import com.example.prescription_manag2.entity.HealthInstitution
import com.example.prescription_manag2.entity.Medication
import com.example.prescription_manag2.entity.Patient
import com.example.prescription_manag2.entity.Prescription
import com.example.prescription_manag2.entity.Specialty
import com.example.prescription_manag2.room.AppDatabase

class DataSyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDatabase.buildDatabase(applicationContext)
            val doctorDao = database.doctorDao()
            val patientDao = database.patientDao()
            val specialtyDao = database.specialtyDao()
            val healthInstitutionDao = database.healthInstitutionDao()
            val prescriptionDao = database.prescriptionDao()
            val medicationDao = database.medicationDao()
            // Retrieve the API service
            val apiService = RetrofitClient.prescriptionApiService

            val remotePrescriptions = apiService.getAllPrescriptions()
            val localPrescriptions = prescriptionDao.getAllPrescriptions() ?: emptyList()

            val prescriptionsToInsert = remotePrescriptions.filter { remotePrescription ->
                localPrescriptions.none { it.id == remotePrescription.id }
            }

            for (prescriptionResponse in prescriptionsToInsert) {
                val prescriptionEntity = Prescription(
                    id = prescriptionResponse.id,
                    doctorId = prescriptionResponse.doctorId,
                    patientId = prescriptionResponse.patientId,
                    instructions = prescriptionResponse.instructions,
                    createdAt = prescriptionResponse.createdAt,
                    expiresAt = prescriptionResponse.expiresAt,
                    status = prescriptionResponse.status,
                    syncStatus = prescriptionResponse.syncStatus
                )
                prescriptionDao.insertPrescription(prescriptionEntity)
            }

            val remoteMedications = apiService.getAllMedications()
            val localMedications = medicationDao.getAllMedications() ?: emptyList()

            val medicationsToInsert = remoteMedications.filter { remoteMedication ->
                localMedications.none { it.id == remoteMedication.id }
            }

            for (medicationResponse in medicationsToInsert) {
                val medicationEntity = Medication(
                    id = medicationResponse.id,
                    prescriptionId = medicationResponse.prescriptionId,
                    name = medicationResponse.name,
                    dosage = medicationResponse.dosage,
                    frequency = medicationResponse.frequency,
                    duration = medicationResponse.duration,
                    syncStatus = medicationResponse.syncStatus
                )
                medicationDao.insertMedication(medicationEntity)
            }


            // 1. Sync Patients
            val remotePatients = apiService.getAllPatients()
            val localPatients = patientDao.getAllPatients() ?: emptyList()

            val patientsToInsert = remotePatients.filter { remotePatient ->
                localPatients.none { it.id == remotePatient.id }
            }

            for (patientResponse in patientsToInsert) {
                val patientEntity = Patient(
                    id = patientResponse.id,
                    firstName = patientResponse.firstName,
                    lastName = patientResponse.lastName,
                    address = patientResponse.address,
                    phone = patientResponse.phone,
                    email = patientResponse.email,
                    password = patientResponse.password,
                    age = patientResponse.age,
                    photoUrl = patientResponse.photoUrl,
                    googleId = patientResponse.googleId
                )
                patientDao.insertPatient(patientEntity)
            }


            // 2. Sync Specialties
            val remoteSpecialties = apiService.getAllSpecialties()
            val localSpecialties = specialtyDao.getAllSpecialties() ?: emptyList()

            val specialtiesToInsert = remoteSpecialties.filter { remoteSpecialty ->
                localSpecialties.none { it.id == remoteSpecialty.id }
            }

            for (specialtyResponse in specialtiesToInsert) {
                val specialtyEntity = Specialty(
                    id = specialtyResponse.id,
                    label = specialtyResponse.label
                )
                specialtyDao.insertSpecialty(specialtyEntity)
            }


            // 3. Sync Health Institutions
            val remoteInstitutions = apiService.getAllHealthInstitutions()
            val localInstitutions = healthInstitutionDao.getAllHealthInstitutions() ?: emptyList()

            // Insert institutions that don't exist locally
            val institutionsToInsert = remoteInstitutions.filter { remoteInstitution ->
                localInstitutions.none { it.id == remoteInstitution.id }
            }

            val institutionsEntitiesToInsert = institutionsToInsert.map { remoteInstitution ->
                HealthInstitution(
                    id = remoteInstitution.id,
                    name = remoteInstitution.name,
                    address = remoteInstitution.address,
                    latitude = remoteInstitution.latitude,
                    longitude = remoteInstitution.longitude,
                    type = remoteInstitution.type
                )
            }

            institutionsEntitiesToInsert.forEach { institutionEntity ->
                healthInstitutionDao.insertHealthInstitution(institutionEntity)
            }


            // 4. Sync Doctors
            val remoteDoctors = apiService.getAllDoctors()
            val localDoctors = doctorDao.getAllDoctors() ?: emptyList()

            // Insert doctors that don't exist locally
            val doctorsToInsert = remoteDoctors.filter { remoteDoctor ->
                localDoctors.none { it.id == remoteDoctor.id }
            }
            val doctorsEntitiesToInsert = doctorsToInsert.map { remoteDoctor ->
                Doctor(
                    id = remoteDoctor.id,
                    firstName = remoteDoctor.firstName,
                    lastName = remoteDoctor.lastName,
                    address = remoteDoctor.address,
                    phone = remoteDoctor.phone,
                    email = remoteDoctor.email,
                    password = remoteDoctor.password,
                    photoUrl = remoteDoctor.photoUrl,
                    googleId = remoteDoctor.googleId,
                    contactEmail = remoteDoctor.contactEmail,
                    contactPhone = remoteDoctor.contactPhone,
                    socialLinks = remoteDoctor.socialLinks,
                    specialtyId = remoteDoctor.specialtyId,
                    institutionId = remoteDoctor.institutionId
                )
            }
            doctorsEntitiesToInsert.forEach { doctorEntity ->
                doctorDao.insertDoctor(doctorEntity)
            }


            Log.i("DataSyncWorker", "Sync completed successfully")
            Log.i("DataSyncWorker", "Inserted ${patientsToInsert.size} patients")
            Log.i("DataSyncWorker", "Inserted ${specialtiesToInsert.size} specialties")
            Log.i("DataSyncWorker", "Inserted ${institutionsToInsert.size} institutions")
            Log.i("DataSyncWorker", "Inserted ${doctorsToInsert.size} doctors")

            Result.success()
        } catch (e: Exception) {
            Log.e("DataSyncWorker", "Data sync failed: ${e.message}")
            Result.failure()
        }
    }
}