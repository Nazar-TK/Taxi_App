package com.example.taxiapp.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.taxiapp.R
import com.example.taxiapp.databinding.ActivityDriverRegistrationBinding
import com.example.taxiapp.databinding.ActivityPassengerRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DriverRegistrationActivity : AppCompatActivity() {private val TAG: String? = PassengerRegistrationActivity::class.simpleName

    lateinit var binding : ActivityDriverRegistrationBinding
    lateinit var firebaseAuth : FirebaseAuth
    lateinit var firebaseDB : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

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

            // user registration
            if(registrationDataValidation(firstName, lastName, phoneNumber, email, password, confirmationPassword, carMake, carModel)) {
                binding.progressBar.visibility = View.VISIBLE
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { result ->
                    if(result.isSuccessful) {
                        Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, result.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.INVISIBLE
                    }
                }
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