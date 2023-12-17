package com.example.taxiapp.presentation.userprofile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.taxiapp.R
import com.example.taxiapp.core.Constants
import com.example.taxiapp.core.PassengerCallback
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class PassengerProfileActivity : AppCompatActivity() {

    private val TAG: String? = PassengerProfileActivity::class.simpleName

    private lateinit var binding : ActivityPassengerProfileBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var storageRef : StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPassengerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().getReference(Constants.USERS_AVATARS)
        getUserData()

        binding.editProfileButton.setOnClickListener {
            startActivity(Intent(this, EditPassengerActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        getUserData()
    }

    fun logOut(view: View) {
        FirebaseAuth.getInstance().signOut() //logout
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun getUserData() {

        val realtimeDB = FirebaseDatabase.getInstance("https://taxiapp-99fcc-default-rtdb.firebaseio.com")
        val repository = PassengerRepositoryImpl(firebaseAuth, realtimeDB, storageRef)

        val idResult = repository.getCurrentUserId()

        when(idResult) {
            is Resource.Success -> {
                val userId = idResult.data
                Toast.makeText(this, "Current user id = {${userId}}", Toast.LENGTH_LONG).show()

                Log.d(TAG, "USER ID IS: {$userId}")

                userId?.let { repository.getPassenger(it, object : PassengerCallback {
                    override fun onPassengerFound(user: Resource<Passenger>) {
                        val passenger = user.data

                        Log.d(TAG, "USER DATA IS: {$user}")
                        Picasso.get().load(passenger?.imgUri.toString()).placeholder(R.drawable.avatar).into(binding.ivUserAvatar)
                        binding.usernameTV.text = "${passenger?.firstName} ${passenger?.lastName}"
                        binding.emailTV.text = passenger?.email
                        binding.phoneTV.text = passenger?.phoneNumber

                    }

                    override fun onPassengerNotFound(error: Resource<String>) {
                        Log.d(TAG, "USER not found. {${error.message.toString()}}")
                    }
                })
                }
                //getUser(userId!!)
            }
            is Resource.Error -> {
                Toast.makeText(this, idResult.message.toString(), Toast.LENGTH_SHORT).show()
                Log.d(TAG, "USER ID ERROR")
            }
        }
    }
}