package com.example.learning.network
import com.example.learning.model.AppointmentCreateRequest
import com.example.learning.model.Doctor
import com.example.learning.model.HealthInstitution
import com.example.learning.model.SpecialtyResponse
import com.example.learning.model.TimeSlot
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
interface ApiService {
    @GET("doctors/")
    suspend fun getDoctors(): List<Doctor>
    @GET("specialties/")
    suspend fun getSpecialties(): List<SpecialtyResponse>

    @GET("doctors/{doctorId}/slots/")
    suspend fun getDoctorSlots(
        @Path("doctorId") doctorId: Int,
        @Query("date") date: String
    ): List<TimeSlot>

    @POST("appointments/")
    suspend fun bookAppointment(@Body appointmentRequest: AppointmentCreateRequest): Unit

    @GET("health-institutions/{institutionId}") // <<< ADD THIS FUNCTION
    suspend fun getHealthInstitution(@Path("institutionId") institutionId: Int): HealthInstitution

    companion object {

        private const val BASE_URL = "http://10.0.2.2:8000/"

        fun create(): ApiService {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(ApiService::class.java)
        }
    }
}