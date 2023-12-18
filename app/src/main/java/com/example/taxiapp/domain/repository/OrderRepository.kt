package com.example.taxiapp.domain.repository

import com.example.taxiapp.core.OrderCallback
import com.example.taxiapp.domain.model.Trip

interface OrderRepository {

    fun saveOrder(order: Trip)
    fun getOrder(id: String, callback: OrderCallback)
}