package com.example.prescription_manag2

import android.app.Application
import android.util.Log // Optionnel, pour le log
import com.example.prescription_manag2.repository.PrescriptionRepository
import com.example.prescription_manag2.repository.SyncRepository
import com.example.prescription_manag2.room.AppDatabase

class MyApplication: Application() {
    private val database by lazy { AppDatabase.buildDatabase(this) }
    private val prescriptionDao by lazy { database.prescriptionDao() }
    private val medicationDao by lazy { database.medicationDao() }
    private val patientDao by lazy { database.patientDao() }
    private val doctorDao by lazy { database.doctorDao() }
    private val specialtyDao by lazy { database.specialtyDao() }
    private val healthInstitutionDao by lazy { database.healthInstitutionDao() }

    // Créer l'instance du SyncRepository
    val syncRepository by lazy { SyncRepository(this) }

    // Injecter le SyncRepository dans le PrescriptionRepository
    val prescriptionRepository by lazy {
        PrescriptionRepository(
            prescriptionDao = prescriptionDao,
            medicationDao = medicationDao,
            patientDao = patientDao,
            doctorDao = doctorDao,
            syncRepository = syncRepository,
            specialtyDao = specialtyDao,
            healthInstitutionDao = healthInstitutionDao
        )
    }
}