package com.example.taxiapp.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.taxiapp.core.Resource
import com.example.taxiapp.data.PassengerRepositoryImpl
import com.example.taxiapp.databinding.ActivityPassengerProfileBinding
import com.example.taxiapp.domain.model.Passenger
import com.example.taxiapp.presentation.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PassengerProfileActivity : AppCompatActivity() {

    private val TAG: String? = PassengerProfileActivity::class.simpleName

    private lateinit var binding : ActivityPassengerProfileBinding
    private lateinit var firebaseAuth : FirebaseAuth
    lateinit var firebaseRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPassengerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseRef = FirebaseDatabase.getInstance("https://taxiapp-99fcc-default-rtdb.firebaseio.com").reference.child("passengers")

        getUserData()
    }

    fun logOut(view: View) {
        FirebaseAuth.getInstance().signOut() //logout
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun getUserData() {

        val realtimeDB = FirebaseDatabase.getInstance("https://taxiapp-99fcc-default-rtdb.firebaseio.com")
        val repository = PassengerRepositoryImpl(firebaseAuth, realtimeDB)

        val idResult = repository.getCurrentUserId()

        when(idResult) {
            is Resource.Success -> {
                val userId = idResult.data
                Toast.makeText(this, "Current user id = {${userId}}", Toast.LENGTH_LONG).show()

                Log.d(TAG, "USER ID IS: {$userId}")

                getUser(userId!!)
            }


//                val userResult = userId?.let { repository.getPassenger(it) }
//                Log.d("THERE!", userResult?.data.toString())
//                when(userResult) {
//                    is Resource.Success -> {
//                        val user = userResult.data
//
//                        Log.d(TAG, "USER DATA IS: {$user}")
//
//                        Toast.makeText(this, "Current user is {${user}}", Toast.LENGTH_LONG).show()
//                        binding.usernameTV.text = "${user?.firstName} ${user?.lastName}"
//                        binding.emailTV.text = user?.email
//                    }
//
//                    is Resource.Error -> {
//                        Toast.makeText(this, userResult.message.toString(), Toast.LENGTH_SHORT).show()
//                        Log.d(TAG, "USER ERROR")
//                    }
//                    null -> {
//                        Toast.makeText(this, "USER IS NULL!", Toast.LENGTH_LONG).show()
//                    }
//                }
            is Resource.Error -> {
                Toast.makeText(this, idResult.message.toString(), Toast.LENGTH_SHORT).show()
                Log.d(TAG, "USER ID ERROR")
            }
        }
    }


    fun getUser(id: String){
        firebaseRef.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // The passenger with the specified ID exists in the database
                    val user = dataSnapshot.getValue(Passenger::class.java)
                    if (user != null) {
                        // Access the properties of the passenger
                        binding.usernameTV.text = "${user?.firstName} ${user?.lastName}"
                        binding.emailTV.text = user?.email
                        // Now you can use the passenger data as needed
                    }
                } else {
                    // The passenger with the specified ID does not exist
                    // Handle the case where the user is not found
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors, such as network issues or database access issues
            }
        })
    }
}