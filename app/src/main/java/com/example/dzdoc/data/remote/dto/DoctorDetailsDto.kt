// file: com/example/dzdoc/data/remote/dto/DoctorDetailsDto.kt
package com.example.dzdoc.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DoctorDetailsDto(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("specialty_label")
    val specialtyLabel: String?,
    @SerializedName("photo_url")
    val photoUrl: String?
)