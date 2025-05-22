package com.example.prescription_manag2.workmanager

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.prescription_manag2.api.RetrofitClient
import com.example.prescription_manag2.models.MedicationRequest
import com.example.prescription_manag2.models.PrescriptionRequest
import com.example.prescription_manag2.entity.SyncStatus
import com.example.prescription_manag2.room.AppDatabase

class PrescriptionSyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        try {
            val database = AppDatabase.buildDatabase(applicationContext)
            val prescriptionDao = database?.prescriptionDao()
            val medicationDao = database?.medicationDao()

            if (prescriptionDao == null || medicationDao == null) {
                Log.e("PrescriptionSyncWorker", "Database or DAOs are null")
                return Result.failure()
            }

            // Retrieve prescriptions to synchronize (PENDING_SYNC)
            val pendingPrescriptions = prescriptionDao.getPrescriptionsToSync()
            Log.d("PrescriptionSyncWorker", "Found ${pendingPrescriptions.size} prescriptions to sync")

            if (pendingPrescriptions.isEmpty()) {
                return Result.success()
            }

            // Retrieve the API service
            val apiService = RetrofitClient.prescriptionApiService

            // For each prescription to synchronize
            for (prescription in pendingPrescriptions) {
                Log.d("PrescriptionSyncWorker", "Syncing prescription id: ${prescription.id}")

                try {
                    // Create the request object for the prescription
                    val prescriptionRequest = PrescriptionRequest(
                        id = prescription.id,
                        patientId = prescription.patientId,
                        doctorId = prescription.doctorId,
                        instructions = prescription.instructions,
                        createdAt = prescription.createdAt,
                        expiresAt = prescription.expiresAt
                    )

                    val response = apiService.insertPrescription(prescriptionRequest)
                    val prescriptionId = response["id"] ?: -1L

                    if (prescriptionId > 0) {
                        val medications = medicationDao.getMedicationsForPrescription(prescription.id)
                        Log.d("PrescriptionSyncWorker", "Found ${medications.size} medications for prescription ${prescription.id}")

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

                        // Send the medications to the server
                        if (medicationRequests.isNotEmpty()) {
                            try {
                                val medResponse = apiService.insertMedications(medicationRequests)
                                Log.d("PrescriptionSyncWorker", "Medications sync response: $medResponse")

                                // Update the synchronization status of the medications
                                if (medications.isNotEmpty()) {
                                    medications.forEach { it.syncStatus = SyncStatus.SYNCED }
                                    medicationDao.updateMedications(medications)
                                    Log.d("PrescriptionSyncWorker", "Updated ${medications.size} medications status to SYNCED")
                                }
                            } catch (e: Exception) {
                                Log.e("PrescriptionSyncWorker", "Error syncing medications: ${e.message}")
                                e.printStackTrace()
                                // Continue execution to at least update the prescription status
                            }
                        }

                        // Update the local synchronization status of the prescription
                        prescription.syncStatus = SyncStatus.SYNCED
                        prescriptionDao.updatePrescription(prescription)
                        Log.d("PrescriptionSyncWorker", "Updated prescription ${prescription.id} status to SYNCED")
                    } else {
                        Log.e("PrescriptionSyncWorker", "Failed to sync prescription ${prescription.id}, server returned invalid ID")
                    }
                } catch (e: Exception) {
                    Log.e("PrescriptionSyncWorker", "Error syncing prescription ${prescription.id}: ${e.message}")
                    e.printStackTrace()
                }
            }

            // Verify that everything was updated correctly
            val remainingPrescriptions = prescriptionDao.getPrescriptionsToSync()
            val remainingMedications = medicationDao.getMedicationsToSync()
            Log.d("PrescriptionSyncWorker", "After sync: ${remainingPrescriptions.size} prescriptions and ${remainingMedications.size} medications still need syncing")

            return Result.success()
        } catch (e: Exception) {
            Log.e("PrescriptionSyncWorker", "Sync failed: ${e.message}")
            e.printStackTrace()
            return Result.failure()
        }
    }
}