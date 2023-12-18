package com.example.taxiapp.data

import android.util.Log
import com.example.taxiapp.core.Constants
import com.example.taxiapp.core.DriverCallback
import com.example.taxiapp.core.OrderCallback
import com.example.taxiapp.core.Resource
import com.example.taxiapp.domain.model.Passenger
import com.example.taxiapp.domain.model.Trip
import com.example.taxiapp.domain.repository.OrderRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference

class OrderRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val realtimeDB : FirebaseDatabase
): OrderRepository {

    private val TAG = "OrderRepositoryImpl"
    private val databaseRef = realtimeDB.getReference(Constants.ORDERS)
    override fun saveOrder(order: Trip) {
        val userId = firebaseAuth.currentUser?.uid

        if (userId != null) {
            databaseRef.child(userId).setValue(order)
                .addOnCompleteListener {
                    Log.d(TAG, "Trip added")
                }
                .addOnFailureListener {
                    Log.d(TAG, "Trip not added")
                }
        }
    }

    override fun getOrder(id: String, callback: OrderCallback) {
        databaseRef.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Check if the data snapshot exists
                if (dataSnapshot.exists()) {
                    // The passenger exists, you can retrieve the data
                    Log.d("HERE!", "retrieving")
                    val order = dataSnapshot.getValue(Trip::class.java)
                    Log.d("HERE!!", order.toString())
                    if (order != null) {
                        callback.onOrderFound(Resource.Success(order))
                    } else {
                        callback.onOrderNotFound(Resource.Error("Order is not found.", null))
                    }
                } else {
                    callback.onOrderNotFound(Resource.Error("Order not found.", null))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback.onOrderNotFound(Resource.Error(databaseError.message.toString(), null))
            }
        })
    }


}