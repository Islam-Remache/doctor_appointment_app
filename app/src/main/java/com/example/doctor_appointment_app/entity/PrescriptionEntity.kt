package com.example.prescription_manag2.entity


import androidx.room.*
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
data class SocialLinks(
    val facebook: String? = null,
    val twitter: String? = null,
    val linkedin: String? = null,
    val instagram: String? = null
)


class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromSocialLinks(value: SocialLinks?): String? {
        return if (value == null) null else gson.toJson(value)
    }

    @TypeConverter
    fun toSocialLinks(value: String?): SocialLinks? {
        return if (value == null) null else gson.fromJson(value, SocialLinks::class.java)
    }
}


// Model for doctors

@Entity(tableName = "health_institutions")
data class HealthInstitution(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val type: String
)


@Entity(
    tableName = "doctors",
    foreignKeys = [
        ForeignKey(
            entity = Specialty::class,
            parentColumns = ["id"],
            childColumns = ["specialty_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = HealthInstitution::class,
            parentColumns = ["id"],
            childColumns = ["institution_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("specialty_id"), Index("institution_id")]
)
data class Doctor(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    val address: String?,
    val phone: String?,
    val email: String,
    val password: String?,
    @ColumnInfo(name = "photo_url") val photoUrl: String?,
    @ColumnInfo(name = "google_id") val googleId: String?,
    @ColumnInfo(name = "contact_email") val contactEmail: String?,
    @ColumnInfo(name = "contact_phone") val contactPhone: String?,
    @ColumnInfo(name = "social_links") val socialLinks: SocialLinks?,
    @ColumnInfo(name = "specialty_id") val specialtyId: Int?,
    @ColumnInfo(name = "institution_id") val institutionId: Int?
)

@Entity(tableName = "specialties")
data class Specialty(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val label: String
)


//@Entity(tableName = "medecins")
//data class Medecin(
//    @PrimaryKey(autoGenerate = true) val id: Int = 0,
//    val name: String,
//    val specialty: String,
//    val image: Int?,
//    val nameClinic: String,
//    val addressClinic: String
//)

// Model for  patients
@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    val address: String?,
    val phone: String?,
    val email: String,
    val password: String?,
    val age: Int?,
    @ColumnInfo(name = "photo_url") val photoUrl: String?,
    @ColumnInfo(name = "google_id") val googleId: String?
)


//@Entity(tableName = "users")
//data class User(
//    @PrimaryKey(autoGenerate = true) val id: Int = 0,
//    val name: String,
//    val email: String,
//    val age: Int,
//    val image: Int?
//)

// Model for prescriptions

@Entity(
    tableName = "prescriptions",
    foreignKeys = [
        ForeignKey(entity = Patient::class, parentColumns = ["id"], childColumns = ["patient_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Doctor::class, parentColumns = ["id"], childColumns = ["doctor_id"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["patient_id"]), Index(value = ["doctor_id"])]
)
data class Prescription(
    @PrimaryKey val id: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "patient_id") val patientId: Int,
    @ColumnInfo(name = "doctor_id") val doctorId: Int,
    val instructions: String,
    @ColumnInfo(name = "created_at") val createdAt: String = getCurrentTimestamp(),
    @ColumnInfo(name = "expires_at") val expiresAt: String,
    var syncStatus: SyncStatus = SyncStatus.PENDING_SYNC,
    var status: String = "active"
) {
    private fun calculateStatus(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.parse(getCurrentTimestamp())!!
        val expiration = sdf.parse(expiresAt)!!
        return if (expiration.before(today)) "expired" else "active"
    }
    fun updateStatus() {
        status = calculateStatus()
    }
    companion object {
        fun getCurrentTimestamp(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return sdf.format(Date())
        }
    }
}


//
//@Entity(
//    tableName = "prescriptions",
//    foreignKeys = [
//        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE),
//        ForeignKey(entity = Medecin::class, parentColumns = ["id"], childColumns = ["medecinId"], onDelete = ForeignKey.CASCADE)
//    ],
//    indices = [Index(value = ["userId"]), Index(value = ["medecinId"])]
//)
//
//
//data class Prescription(
//    @PrimaryKey val id: Long = System.currentTimeMillis(),
//    val userId: Int,
//    val medecinId: Int,
//    val instructions: String,
//    val dateCreation: String = getCurrentDate(),
//    val dateExpiration: String,
//    var syncStatus: SyncStatus = SyncStatus.PENDING_SYNC,
//    var status: String = "active"
//) {
//    fun calculateStatus(): String {
//        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val today = sdf.parse(getCurrentDate())!!
//        val expiration = sdf.parse(dateExpiration)!!
//        return if (expiration.before(today)) "expired" else "active"
//    }
//
//    companion object {
//        fun getCurrentDate(): String {
//            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//            return sdf.format(Date())
//        }
//    }
//}

// Model for medications

@Entity(
    tableName = "medications",
    foreignKeys = [
        ForeignKey(entity = Prescription::class, parentColumns = ["id"], childColumns = ["prescription_id"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["prescription_id"])]
)
data class Medication(
    @PrimaryKey val id: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "prescription_id") val prescriptionId: Long,
    val name: String,
    val dosage: String?,
    val frequency: String?,
    val duration: String?,
    var syncStatus: SyncStatus = SyncStatus.PENDING_SYNC
)


//@Entity(
//    tableName = "medications",
//    foreignKeys = [
//        ForeignKey(entity = Prescription::class, parentColumns = ["id"], childColumns = ["prescriptionId"], onDelete = ForeignKey.CASCADE)
//    ],
//    indices = [Index(value = ["prescriptionId"])]
//)
//data class Medication(
//    @PrimaryKey(autoGenerate = true) val id: Long = 0,
//    val prescriptionId: Long,
//    val name: String,
//    val dosage: String,
//    val frequency: String,
//    val duration: String,
//    var syncStatus: SyncStatus = SyncStatus.PENDING_SYNC,
//    )

// Create a SyncStatus enum
enum class SyncStatus {
    SYNCED,      // Already synced with server
    PENDING_SYNC, // Created/modified offline, needs to be synced when online
}
