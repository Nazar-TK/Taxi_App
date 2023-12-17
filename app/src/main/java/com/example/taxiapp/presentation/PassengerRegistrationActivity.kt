package com.example.taxiapp.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.taxiapp.core.Resource
import com.example.taxiapp.data.PassengerRepositoryImpl
import com.example.taxiapp.databinding.ActivityPassengerRegistrationBinding
import com.example.taxiapp.domain.model.Passenger
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

//@AndroidEntryPoint
//class PassengerRegistrationActivity @Inject constructor(private val repository: PassengerRepository) : AppCompatActivity() {

class PassengerRegistrationActivity: AppCompatActivity() {

    private val TAG: String? = PassengerRegistrationActivity::class.simpleName

    private lateinit var binding : ActivityPassengerRegistrationBinding
    private lateinit var firebaseAuth : FirebaseAuth
    lateinit var firebaseDB : FirebaseDatabase
//    @Inject
//    lateinit var repository: PassengerRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPassengerRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        //firebaseRef = FirebaseDatabase.getInstance("https://taxiapp-99fcc-default-rtdb.firebaseio.com").getReference("passengers")
        firebaseDB = FirebaseDatabase.getInstance("https://taxiapp-99fcc-default-rtdb.firebaseio.com")

        binding.registerButton.setOnClickListener {
            Log.d(TAG, "registerButton")
            val firstName : String  = binding.enterFirstName.text.toString().trim()
            val lastName : String  = binding.enterLastName.text.toString().trim()
            val phoneNumber : String  = binding.enterPhoneNumber.text.toString().trim()
            val email: String = binding.enterEmailAddress.text.toString().trim()
            val password : String = binding.enterPassword.text.toString()
            val confirmationPassword : String = binding.enterPasswordAgain.text.toString()

            // user registration
            if(registrationDataValidation(firstName, lastName, phoneNumber, email, password, confirmationPassword)) {
                binding.progressBar.visibility = View.VISIBLE

                {
                    TODO ("REMOVE REPOSITORY TO DI")
                }
                //
                val repository = PassengerRepositoryImpl(firebaseAuth, firebaseDB)
                val passenger = Passenger(
                    firstName = firstName, lastName = lastName,
                    phoneNumber = phoneNumber, email = email
                )

                val result = repository.savePassenger(passenger, password)

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

    private fun registrationDataValidation(firstName : String, lastName : String, phoneNumber: String, email : String, password : String, confirmationPassword : String) : Boolean
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
        }
        return isDataEnteredCorrectly
    }
}