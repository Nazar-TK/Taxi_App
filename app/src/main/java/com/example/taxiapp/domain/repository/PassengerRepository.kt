package com.example.taxiapp.domain.repository

import com.example.taxiapp.core.Resource
import com.example.taxiapp.domain.model.Passenger

interface PassengerRepository {

    fun savePassenger(passenger: Passenger, password: String) : Resource<Boolean>

    fun getPassenger(id: String): Resource<Passenger>

    fun getCurrentUserId(): Resource<String>
}