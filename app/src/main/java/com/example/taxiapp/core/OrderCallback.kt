package com.example.taxiapp.core

import com.example.taxiapp.domain.model.Driver
import com.example.taxiapp.domain.model.Trip

interface OrderCallback {
    fun onOrderFound(order: Resource<Trip>)
    fun onOrderNotFound(error: Resource<String>)
}