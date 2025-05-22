package com.example.prescription_manag2.repository

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.prescription_manag2.workmanager.PrescriptionSyncWorker
import com.example.prescription_manag2.workmanager.DataSyncWorker
import java.util.concurrent.TimeUnit

class SyncRepository(private val context: Context) {

    companion object {
        private const val TAG = "SyncRepository"
        // Minimum interval not needed here since plus de périodicité
    }

    fun schedulePrescriptionSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<PrescriptionSyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "prescription_sync_work",
                ExistingWorkPolicy.REPLACE,
                syncWorkRequest
            )

        Log.d(TAG, "Prescription sync scheduled as one-time work")
    }

    fun scheduleDataSyncOnceOnNetwork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<DataSyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "data_sync_one_time_work",
                ExistingWorkPolicy.REPLACE,
                syncWorkRequest
            )

        Log.d(TAG, "Data sync scheduled once on network connection")
    }

    fun scheduleAllSyncs() {
        schedulePrescriptionSync()
        scheduleDataSyncOnceOnNetwork()
        Log.d(TAG, "All syncs scheduled once")
    }

//    // Méthode pour déclenchement manuel immédiat
//    fun manualSyncTrigger() {
//        schedulePrescriptionSync()
//        scheduleDataSyncOnceOnNetwork()
//        Log.d(TAG, "Manual sync triggered")
//    }
}
