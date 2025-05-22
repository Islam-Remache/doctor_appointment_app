package com.example.learning.model
import com.squareup.moshi.Json
data class HealthInstitution(
    val id: Int,
    val name: String,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    @Json(name = "type") val institutionType: String?
)