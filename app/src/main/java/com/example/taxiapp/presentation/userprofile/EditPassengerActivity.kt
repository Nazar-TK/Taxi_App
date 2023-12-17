package com.example.taxiapp.presentation.userprofile

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.taxiapp.core.Constants
import com.example.taxiapp.core.PassengerCallback
import com.example.taxiapp.core.Resource
import com.example.taxiapp.data.PassengerRepositoryImpl
import com.example.taxiapp.databinding.ActivityEditPassengerBinding
import com.example.taxiapp.domain.model.Passenger
import com.example.taxiapp.domain.repository.PassengerRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class EditPassengerActivity : AppCompatActivity() {

    private val TAG: String? = EditPassengerActivity::class.simpleName

    private lateinit var binding : ActivityEditPassengerBinding

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var storageRef : StorageReference
    private val realtimeDB = FirebaseDatabase.getInstance("https://taxiapp-99fcc-default-rtdb.firebaseio.com")
    private lateinit var repository: PassengerRepository

    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPassengerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().getReference(Constants.USERS_AVATARS)
        repository = PassengerRepositoryImpl(firebaseAuth, realtimeDB, storageRef)

        getUserData()

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            binding.ivAvatar.setImageURI(uri)
            if (uri != null) {
                imageUri = uri
            }
        }
        binding.btnEditAvatar.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnUpdateProfile.setOnClickListener {
            updateUserData()
            finish()
        }
    }

    private fun getUserData() {

        val idResult = repository.getCurrentUserId()

        when(idResult) {
            is Resource.Success -> {
                val userId = idResult.data
                Log.d(TAG, "USER ID IS: {$userId}")

                userId?.let { repository.getPassenger(it, object : PassengerCallback {
                    override fun onPassengerFound(user: Resource<Passenger>) {
                        val passenger = user.data
                        Log.d(TAG, "USER DATA IS: {$user}")
                        binding.etFirstName.setText(passenger?.firstName.toString())
                        binding.etLastName.setText(passenger?.lastName.toString())
                        binding.etPhoneNumber.setText(passenger?.phoneNumber.toString())
                        Picasso.get().load(passenger?.imgUri.toString()).into(binding.ivAvatar)
                    }

                    override fun onPassengerNotFound(error: Resource<String>) {
                        Log.d(TAG, "USER not found. {${error.message.toString()}}")
                    }
                })
                }
            }
            is Resource.Error -> {
                Toast.makeText(this, idResult.message.toString(), Toast.LENGTH_SHORT).show()
                Log.d(TAG, "USER ID ERROR")
            }
        }
    }

    private fun updateUserData() {

        val updates = mutableMapOf<String, Any>()

        binding.etFirstName.text?.let { updates["firstName"] = it.toString() }
        binding.etLastName.text?.let { updates["lastName"] = it.toString() }
        binding.etPhoneNumber.text?.let { updates["phoneNumber"] = it.toString() }

        repository.updatePassengerData(updates, imageUri)
    }
}