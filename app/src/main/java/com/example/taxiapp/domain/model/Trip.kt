package com.example.taxiapp.domain.model

data class Trip(
    val startPoint: String? = null,
    val destinationPoint: String? = null,
    val price: Int? = null,
    val driverId: String? = null
)
