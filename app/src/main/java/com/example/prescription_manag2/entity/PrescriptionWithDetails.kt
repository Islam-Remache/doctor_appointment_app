//package com.example.prescription_manag2.entity
//
//import androidx.room.Embedded
//import androidx.room.Relation
//
//data class PrescriptionWithDetails(
//    @Embedded val prescription: Prescription,
//
//    @Relation(
//        parentColumn = "medecinId",
//        entityColumn = "id"
//    )
//    val medecin: Medecin,
//    @Relation(
//        parentColumn = "id",
//        entityColumn = "prescriptionId"
//    )
//    val user: User,
//    @Relation(
//        parentColumn = "id",
//        entityColumn = "prescriptionId"
//    )
//    val medications: List<Medication>
//)


package com.example.prescription_manag2.entity

import androidx.room.Embedded
import androidx.room.Relation

data class PrescriptionWithDetails(
    @Embedded val prescription: Prescription,

    @Relation(
        parentColumn = "doctorId",
        entityColumn = "id"
    )
    val doctor: Doctor,

    @Relation(
        parentColumn = "patientId",
        entityColumn = "id"
    )
    val patient: Patient,

    @Relation(
        parentColumn = "id",
        entityColumn = "prescriptionId"
    )
    val medications: List<Medication>
)
