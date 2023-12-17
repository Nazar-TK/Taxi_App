package com.example.taxiapp.presentation.userprofile

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.taxiapp.R
import com.example.taxiapp.core.Constants
import com.example.taxiapp.core.DriverCallback
import com.example.taxiapp.core.PassengerCallback
import com.example.taxiapp.core.Resource
import com.example.taxiapp.data.DriverRepositoryImpl
import com.example.taxiapp.data.PassengerRepositoryImpl
import com.example.taxiapp.databinding.ActivityDriverProfileBinding
import com.example.taxiapp.domain.model.Driver
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

class DriverProfileActivity : AppCompatActivity() {

    private val TAG: String? = DriverProfileActivity::class.simpleName

    private lateinit var binding : ActivityDriverProfileBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var storageRef : StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().getReference(Constants.USERS_AVATARS)

        binding.editProfileButton.setOnClickListener {
            startActivity(Intent(this, EditDriverActivity::class.java))
        }

        getUserData()
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
        val repository = DriverRepositoryImpl(firebaseAuth, realtimeDB, storageRef)

        val idResult = repository.getCurrentUserId()

        when(idResult) {
            is Resource.Success -> {
                val userId = idResult.data
                Toast.makeText(this, "Current user id = {${userId}}", Toast.LENGTH_LONG).show()

                Log.d(TAG, "USER ID IS: {$userId}")

                userId?.let { repository.getDriver(it, object : DriverCallback {
                    @SuppressLint("SetTextI18n")
                    override fun onDriverFound(user: Resource<Driver>) {
                        val driver = user.data

                        Log.d(TAG, "USER DATA IS: {$user}")

                        Toast.makeText(this@DriverProfileActivity, "Current user is {${driver}}", Toast.LENGTH_LONG).show()
                        Picasso.get().load(driver?.imgUri.toString()).placeholder(R.drawable.avatar).into(binding.ivUserAvatar)
                        binding.usernameTV.text = "${driver?.firstName} ${driver?.lastName}"
                        binding.emailTV.text = driver?.email
                        binding.phoneTV.text = driver?.phoneNumber.toString()
                        binding.carTV.text = "${driver?.car?.color} ${driver?.car?.make} ${driver?.car?.model}"
                    }

                    override fun onDriverNotFound(error: Resource<String>) {
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