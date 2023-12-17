package com.example.taxiapp.data

import com.example.taxiapp.core.Resource
import com.example.taxiapp.domain.model.Driver
import com.example.taxiapp.domain.model.Passenger
import com.example.taxiapp.domain.repository.DriverRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class DriverRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseRef : DatabaseReference
): DriverRepository {
    override fun saveDriver(driver: Driver, password: String): Resource<Boolean> {
        var resource : Resource<Boolean> = Resource.Success(true)

        firebaseAuth.createUserWithEmailAndPassword(driver.email!!, password).addOnCompleteListener { result ->
            if(result.isSuccessful) {
                val userId = firebaseAuth.currentUser?.uid

                if (userId != null) {
                    firebaseRef.child(userId).setValue(driver.copy(id = userId))
                        .addOnCompleteListener {
                            resource = Resource.Success(true)
                        }
                        .addOnFailureListener {
                            resource = Resource.Error(it.message.toString(), null)
                        }
                } else {
                    resource = Resource.Error("Could not find the user!", null)
                }
            } else {
                resource = Resource.Error(result.exception?.message.toString(), null)
            }
        }
        return resource
    }
}