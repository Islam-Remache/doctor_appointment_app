//package com.example.prescription_manag2.room
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import com.example.prescription_manag2.entity.Medecin
//import com.example.prescription_manag2.entity.Medication
//import com.example.prescription_manag2.entity.Prescription
//import com.example.prescription_manag2.entity.User
//
//
//@Database(entities = [User::class, Medecin::class, Prescription::class, Medication::class], version = 5)
//abstract class AppDatabase:RoomDatabase() {
//    abstract fun userDao(): UserDao
//    abstract fun medecinDao(): MedecinDao
//    abstract fun prescriptionDao(): PrescriptionDao
//    abstract fun medicationDao(): MedicationDao
//    companion object {
//        private var INSTANCE: AppDatabase? = null
//        fun buildDatabase(context: Context): AppDatabase? {
//            if (INSTANCE == null) { synchronized(this) {
//                INSTANCE = Room.databaseBuilder(
//                    context.applicationContext, AppDatabase::class.java,
//                    "prescription_db").fallbackToDestructiveMigration().build()
//            }
//            }
//            return INSTANCE
//        }
//    }
//
//    }
//


package com.example.prescription_manag2.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.prescription_manag2.entity.Converters
import com.example.prescription_manag2.entity.Doctor
import com.example.prescription_manag2.entity.Medication
import com.example.prescription_manag2.entity.Prescription
import com.example.prescription_manag2.entity.Patient
import com.example.prescription_manag2.entity.Specialty
import com.example.prescription_manag2.entity.HealthInstitution

@Database(
    entities = [
        Patient::class,
        Doctor::class,
        Prescription::class,
        Medication::class,
        Specialty::class,
        HealthInstitution::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun patientDao(): PatientDao
    abstract fun doctorDao(): DoctorDao
    abstract fun prescriptionDao(): PrescriptionDao
    abstract fun medicationDao(): MedicationDao
    abstract fun specialtyDao(): SpecialtyDao
    abstract fun healthInstitutionDao(): HealthInstitutionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun buildDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "prescription_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

