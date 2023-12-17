package com.example.taxiapp.data


import android.util.Log
import com.example.taxiapp.core.Resource
import com.example.taxiapp.domain.model.Passenger
import com.example.taxiapp.domain.repository.PassengerRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class PassengerRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val realtimeDB : FirebaseDatabase
) : PassengerRepository {

    val databaseRef = realtimeDB.getReference("passengers")
    override fun savePassenger(passenger: Passenger, password: String): Resource<Boolean> {

        var resource : Resource<Boolean> = Resource.Success(true)
        firebaseAuth.createUserWithEmailAndPassword(passenger.email!!, password).addOnCompleteListener { result ->
            if(result.isSuccessful) {
                val userId = firebaseAuth.currentUser?.uid

                if (userId != null) {
                    databaseRef.child(userId).setValue(passenger.copy(id = userId))
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

    override fun getPassenger(id: String): Resource<Passenger> {

        var resource: Resource<Passenger> = Resource.Success(null)
        var res : Int = 0
        databaseRef.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Check if the data snapshot exists
                if (dataSnapshot.exists()) {
                    // The passenger exists, you can retrieve the data
                    Log.d("HERE!", "retrieving")
                    val passenger = dataSnapshot.getValue(Passenger::class.java)
                    Log.d("HERE!!", passenger.toString())
                    if (passenger != null) {
                        resource =  Resource.Success(passenger)
                        res = 1
                    } else {
                        resource = Resource.Error("User is not found.", null)
                        res = 2
                    }
                } else {
                    resource = Resource.Error("User not found.", null)
                    res = 3
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                resource = Resource.Error(databaseError.message.toString(), null)
                res = 4
            }
        })
        Log.d("HERE!!!", res.toString())
        return resource
    }

    override fun getCurrentUserId(): Resource<String> {
        val userId = firebaseAuth.currentUser?.uid

        return if(userId!=null) {
            Resource.Success(userId)
        } else {
            Resource.Error("User not found", null)
        }
    }
}