package com.example.taxiapp.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.taxiapp.R
import com.example.taxiapp.databinding.ActivityLoginBinding

import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding

    lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()

        checkAuthState()

        binding.logInButton.setOnClickListener {
            onLogIn()
        }
    }


    private fun checkAuthState()
    {
        if(firebaseAuth.currentUser != null)
        {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    fun onClickSignUp(view: View)
    {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }

    private fun logInDataValidation(email : String, password : String) : Boolean {
        var isDataEnteredCorrectly = true

        binding.apply {
            if (email.isEmpty()) {
                enterEmailAddress.error = "Email is required!"
                isDataEnteredCorrectly = false
            } else if (password.isEmpty()) {
                enterPassword.error = "Password is required!"
                isDataEnteredCorrectly = false
            }
        }
        return isDataEnteredCorrectly
    }

    private fun onLogIn()
    {
        val email: String = binding.enterEmailAddress.text.toString().trim()
        val password : String = binding.enterPassword.text.toString()

        if(logInDataValidation(email, password)) {
            binding.progressBar.visibility = View.VISIBLE

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { result ->
                    if (result.isSuccessful) {
                        Toast.makeText(this, getString(R.string.sign_in_success), Toast.LENGTH_SHORT).show()
                        checkAuthState()
                    } else {
                        Toast.makeText(this, result.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                        binding.enterPassword.text.clear()
                        binding.progressBar.visibility = View.INVISIBLE
                    }
                }
        }
    }
}

