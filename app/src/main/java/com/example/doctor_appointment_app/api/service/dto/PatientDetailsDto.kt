// file: com/example/dzdoc/data/remote/dto/PatientDetailsDto.kt
package com.example.dzdoc.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PatientDetailsDto(
    @SerializedName("patient_id")
    val patientId: Int, // Make sure backend schema provides this
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("photo_url")
    val photoUrl: String?
)