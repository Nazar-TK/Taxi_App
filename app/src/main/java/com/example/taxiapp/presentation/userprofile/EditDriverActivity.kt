package com.example.taxiapp.presentation.userprofile

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.taxiapp.R
import com.example.taxiapp.core.Constants
import com.example.taxiapp.core.DriverCallback
import com.example.taxiapp.core.PassengerCallback
import com.example.taxiapp.core.Resource
import com.example.taxiapp.data.DriverRepositoryImpl
import com.example.taxiapp.data.PassengerRepositoryImpl
import com.example.taxiapp.databinding.ActivityEditDriverBinding
import com.example.taxiapp.databinding.ActivityEditPassengerBinding
import com.example.taxiapp.domain.model.Driver
import com.example.taxiapp.domain.model.Passenger
import com.example.taxiapp.domain.repository.DriverRepository
import com.example.taxiapp.domain.repository.PassengerRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class EditDriverActivity : AppCompatActivity() {
    private val TAG: String? = EditDriverActivity::class.simpleName

    private lateinit var binding : ActivityEditDriverBinding

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var storageRef : StorageReference
    private val realtimeDB = FirebaseDatabase.getInstance("https://taxiapp-99fcc-default-rtdb.firebaseio.com")
    private lateinit var repository: DriverRepository

    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDriverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().getReference(Constants.USERS_AVATARS)
        repository = DriverRepositoryImpl(firebaseAuth, realtimeDB, storageRef)

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

                userId?.let { repository.getDriver(it, object : DriverCallback {
                    override fun onDriverFound(user: Resource<Driver>) {
                        val driver = user.data
                        Log.d(TAG, "USER DATA IS: {$user}")
                        binding.etFirstName.setText(driver?.firstName.toString())
                        binding.etLastName.setText(driver?.lastName.toString())
                        binding.etPhoneNumber.setText(driver?.phoneNumber.toString())
                        binding.enterCarMake.setText(driver?.car?.make.toString())
                        binding.enterCarModel.setText(driver?.car?.model.toString())
                        binding.enterCarColor.setText(driver?.car?.color.toString())
                        Picasso.get().load(driver?.imgUri.toString()).into(binding.ivAvatar)

                    }

                    override fun onDriverNotFound(error: Resource<String>) {
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
        binding.enterCarMake.text?.let { updates["car/make"] = it.toString() }
        binding.enterCarModel.text?.let { updates["car/model"] = it.toString() }
        binding.enterCarColor.text?.let { updates["car/color"] = it.toString() }

        repository.updateDriverData(updates, imageUri)
    }
}