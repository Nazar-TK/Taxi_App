package com.example.taxiapp.data


import android.net.Uri
import android.util.Log
import com.example.taxiapp.core.Constants
import com.example.taxiapp.core.PassengerCallback
import com.example.taxiapp.core.Resource
import com.example.taxiapp.domain.model.Passenger
import com.example.taxiapp.domain.repository.PassengerRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference


class PassengerRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val realtimeDB : FirebaseDatabase,
    private val storageRef : StorageReference
) : PassengerRepository {

    private val TAG = "PassengerRepositoryImpl"
    private val databaseRef = realtimeDB.getReference(Constants.PASSENGERS)
    override fun registerPassenger(passenger: Passenger, password: String): Resource<Boolean> {

        var resource : Resource<Boolean> = Resource.Success(true)
        firebaseAuth.createUserWithEmailAndPassword(passenger.email!!, password).addOnCompleteListener { result ->
            if(result.isSuccessful) {
                resource = savePassengerData(passenger)
            } else {
                resource = Resource.Error(result.exception?.message.toString(), false)
            }
        }
        return resource
    }


    override fun savePassengerData(passenger: Passenger): Resource<Boolean> {
        var resource : Resource<Boolean> = Resource.Success(true)
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
            resource = Resource.Error("Could not find the user!", false)
        }
        return resource
    }

    override fun saveUserAvatar(uri: Uri) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            uri?.let {
                Log.d(TAG, "Avatar saving!!!")
                storageRef.child(userId).putFile(it)
                    .addOnSuccessListener { task ->
                        Log.d(TAG, "Avatar saved")
                        task.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener { url ->
                                val imgUrl = url.toString()

                                val updates = mapOf<String, Any>("imgUri" to imgUrl)
                                databaseRef.child(userId!!)
                                    .updateChildren(updates)
                                    .addOnSuccessListener {
                                        Log.d(TAG, "Avatar updated")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.d(TAG, "Avatar not updated. {${e.message.toString()}} ")
                                    }
                            }
                    }
                    .addOnFailureListener{
                        Log.d(TAG, "Avatar not saved. {${it.message.toString()}} ")
                    }
            }
        }
    }

    override fun updatePassengerData(updates: MutableMap<String, Any>, imageUrl: Uri?): Resource<Boolean> {
        val userId = firebaseAuth.currentUser?.uid
        Log.d(TAG, "User Updating! updates is not empty = ${updates.isNotEmpty()}")

        Log.d(TAG, "Image url = ${imageUrl.toString()}")
        if( userId != null && imageUrl != null) {

            saveUserAvatar(uri = imageUrl)
        }
        var resource : Resource<Boolean> = Resource.Success(true)
        if (updates.isNotEmpty() && userId != null) {
            // Update specific fields in the database
            Log.d(TAG, "User Updating!!!")
            databaseRef.child(userId)
                .updateChildren(updates)
                .addOnSuccessListener {
                    Log.d(TAG, "User Updated")
                    resource = Resource.Success(true)
                }
                .addOnFailureListener { e ->
                    resource = Resource.Error(e.message.toString(), false)
                    Log.d(TAG, "User not updated. {${e.message.toString()}}")
                }
        }



        // Update the specific passenger node
//        databaseRef.child(userId!!).updateChildren(updates)
//            .addOnSuccessListener {
//                // Handle success, if needed
//                Log.d(TAG, "User Updated")
//            }
//            .addOnFailureListener { e ->
//                // Handle failure, if needed
//                Log.d(TAG, "User NOT Updated")
//            }

        return resource
    }

    override fun getPassenger(id: String, callback: PassengerCallback){

        databaseRef.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Check if the data snapshot exists
                if (dataSnapshot.exists()) {
                    // The passenger exists, you can retrieve the data
                    Log.d("HERE!", "retrieving")
                    val passenger = dataSnapshot.getValue(Passenger::class.java)
                    Log.d("HERE!!", passenger.toString())
                    if (passenger != null) {
                        callback.onPassengerFound(Resource.Success(passenger))
                    } else {
                        callback.onPassengerNotFound(Resource.Error("User is not found.", null))
                    }
                } else {
                    callback.onPassengerNotFound(Resource.Error("User not found.", null))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback.onPassengerNotFound(Resource.Error(databaseError.message.toString(), null))
            }
        })
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