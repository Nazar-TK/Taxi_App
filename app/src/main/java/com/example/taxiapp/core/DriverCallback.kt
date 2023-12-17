package com.example.taxiapp.core

import com.example.taxiapp.domain.model.Driver
import com.example.taxiapp.domain.model.Passenger

interface DriverCallback {
    fun onDriverFound(driver: Resource<Driver>)
    fun onDriverNotFound(error: Resource<String>)
}