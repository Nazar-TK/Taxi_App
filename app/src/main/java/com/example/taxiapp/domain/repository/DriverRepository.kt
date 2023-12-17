package com.example.taxiapp.domain.repository

import com.example.taxiapp.core.Resource
import com.example.taxiapp.domain.model.Driver

interface DriverRepository {

    fun saveDriver(driver: Driver, password: String) : Resource<Boolean>
}