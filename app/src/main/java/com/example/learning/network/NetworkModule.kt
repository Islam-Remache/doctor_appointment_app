package com.example.learning.network
import com.example.learning.network.ApiService
object NetworkModule {
    val apiService: ApiService by lazy {
        ApiService.create()
    }
}