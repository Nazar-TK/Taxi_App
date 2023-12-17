package com.example.taxiapp.domain.model

data class Driver(
    val id: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val car: Car? = null
)
