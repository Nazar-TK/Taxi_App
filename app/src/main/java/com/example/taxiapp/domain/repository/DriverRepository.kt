package com.example.taxiapp.domain.repository

import android.net.Uri
import com.example.taxiapp.core.DriverCallback
import com.example.taxiapp.core.PassengerCallback
import com.example.taxiapp.core.Resource
import com.example.taxiapp.domain.model.Driver
import com.example.taxiapp.domain.model.Passenger

interface DriverRepository {

    fun registerDriver(driver: Driver, password: String) : Resource<Boolean>
    fun saveDriverData(driver: Driver) : Resource<Boolean>
    fun getDriver(id: String, callback: DriverCallback)
    fun getCurrentUserId(): Resource<String>
    fun updateDriverData(updates: MutableMap<String, Any>, imageUrl: Uri? = null): Resource<Boolean>
    fun saveUserAvatar(uri: Uri)
}