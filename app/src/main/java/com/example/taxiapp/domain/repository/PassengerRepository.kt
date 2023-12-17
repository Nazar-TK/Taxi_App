package com.example.taxiapp.domain.repository

import android.net.Uri
import com.example.taxiapp.core.PassengerCallback
import com.example.taxiapp.core.Resource
import com.example.taxiapp.domain.model.Passenger

interface PassengerRepository {

    fun registerPassenger(passenger: Passenger, password: String) : Resource<Boolean>
    fun getPassenger(id: String, callback: PassengerCallback)
    fun getCurrentUserId(): Resource<String>
    fun savePassengerData(passenger: Passenger): Resource<Boolean>
    fun updatePassengerData(updates: MutableMap<String, Any>, imageUrl: Uri? = null): Resource<Boolean>
    fun saveUserAvatar(uri: Uri)
}