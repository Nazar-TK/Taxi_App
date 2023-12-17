package com.example.taxiapp.core

import com.example.taxiapp.domain.model.Passenger

interface PassengerCallback {

    fun onPassengerFound(user: Resource<Passenger>)
    fun onPassengerNotFound(error: Resource<String>)
}