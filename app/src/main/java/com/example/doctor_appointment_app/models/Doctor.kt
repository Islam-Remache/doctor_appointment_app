package com.example.learning.model
data class Doctor(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val email: String,
    val specialty_id: Int?,
    val photo_url: String?,
    val social_links: Map<String, String>? = null,
    val health_institution_id: Int?
) {
    val fullName: String
        get() = "$first_name $last_name"
}