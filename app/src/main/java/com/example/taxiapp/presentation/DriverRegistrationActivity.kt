package com.example.taxiapp.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.taxiapp.core.Resource
import com.example.taxiapp.data.DriverRepositoryImpl
import com.example.taxiapp.databinding.ActivityDriverRegistrationBinding
import com.example.taxiapp.domain.model.Car
import com.example.taxiapp.domain.model.Driver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

//class DriverRegistrationActivity @Inject constructor(repository: DriverRepository) : AppCompatActivity() {
class DriverRegistrationActivity : AppCompatActivity() {

    private val TAG: String? = DriverRegistrationActivity::class.simpleName

    lateinit var binding : ActivityDriverRegistrationBinding
    lateinit var firebaseAuth : FirebaseAuth
    private lateinit var firebaseRef : DatabaseReference
   // @Inject
   // lateinit var repository: DriverRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseRef = FirebaseDatabase.getInstance("https://taxiapp-99fcc-default-rtdb.firebaseio.com").getReference("drivers")

        binding.registerButton.setOnClickListener {
            Log.d(TAG, "registerButton")
            val firstName : String  = binding.enterFirstName.text.toString().trim()
            val lastName : String  = binding.enterLastName.text.toString().trim()
            val phoneNumber : String  = binding.enterPhoneNumber.text.toString().trim()
            val email: String = binding.enterEmailAddress.text.toString().trim()
            val password : String = binding.enterPassword.text.toString()
            val confirmationPassword : String = binding.enterPasswordAgain.text.toString()
            val carMake : String = binding.enterCarMake.text.toString().trim()
            val carModel : String = binding.enterCarModel.text.toString().trim()
            val carColor : String = binding.enterCarColor.text.toString().trim()

            // user registration
            if(registrationDataValidation(firstName, lastName, phoneNumber, email, password, confirmationPassword, carMake, carModel)) {
                binding.progressBar.visibility = View.VISIBLE

                val repository = DriverRepositoryImpl(firebaseAuth, firebaseRef)

                val car = Car(make = carMake, model = carModel, color = carColor)
                val driver = Driver(
                    firstName = firstName, lastName = lastName,
                    phoneNumber = phoneNumber, email = email, car = car
                )


                val result = repository.saveDriver(driver, password)

                when(result) {
                    is Resource.Success -> {
                        Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, PassengerActivity::class.java))
                        finish()
                    }
                    is Resource.Error -> {
                        Toast.makeText(this, result.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

                binding.progressBar.visibility = View.INVISIBLE
            }
        }
        binding.logInButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                Log.d(TAG, "LogIN button clicked")
                finish()
            }
        })
    }


    private fun registrationDataValidation(firstName : String, lastName : String, phoneNumber: String,
                                           email : String, password : String, confirmationPassword : String,
                                           carMake : String, carModel : String) : Boolean
    {
        var isDataEnteredCorrectly = true

        binding.apply {
            if(firstName.isEmpty()) {
                enterFirstName.error = "First name is required!"
                isDataEnteredCorrectly = false
            }
            else if(lastName.isEmpty()) {
                enterLastName.error = "Last name is required!"
                isDataEnteredCorrectly = false
            }
            else if(phoneNumber.isEmpty()) {
                enterPhoneNumber.error = "Phone number is required!"
                isDataEnteredCorrectly = false
            }
            else if(email.isEmpty()) {
                enterEmailAddress.error = "Email is required!"
                isDataEnteredCorrectly = false
            }
            else if(password.isEmpty()) {
                enterPassword.error = "Password is required!"
                isDataEnteredCorrectly = false
            }
            else if(password.length < 6) {
                enterPassword.error = "Password should contain 6 or more symbols"
                isDataEnteredCorrectly = false
            }
            else if(confirmationPassword.isEmpty()) {
                enterPasswordAgain.error = "Confirmation password is required!"
                isDataEnteredCorrectly = false
            }
            else if(password != confirmationPassword) {
                enterPasswordAgain.text.clear()
                enterPasswordAgain.error = "Password and confirmation password do not match!"
                isDataEnteredCorrectly = false
            }
            else if(carMake.isEmpty()) {
                enterCarMake.error = "Car make is required!"
                isDataEnteredCorrectly = false
            }
            else if(carModel.isEmpty()) {
                enterCarModel.error = "Car model is required!"
                isDataEnteredCorrectly = false
            }
        }
        return isDataEnteredCorrectly
    }
}