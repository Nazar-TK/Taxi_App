package com.example.taxiapp.presentation.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.example.taxiapp.R
import com.example.taxiapp.databinding.FragmentPassengerLoginBinding
import com.example.taxiapp.presentation.mainscreen.passengerscreen.PassengerActivity
import com.example.taxiapp.presentation.registration.PassengerRegistrationActivity
import com.google.firebase.auth.FirebaseAuth


class PassengerLoginFragment(private val firebaseAuth: FirebaseAuth) : Fragment() {

    private lateinit var binding: FragmentPassengerLoginBinding
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPassengerLoginBinding.inflate(inflater)
        progressBar = requireActivity().findViewById(R.id.progressBar)

        binding.signUpButton.setOnClickListener {
            val intent = Intent(activity, PassengerRegistrationActivity::class.java)
            startActivity(intent)
        }
        binding.logInButton.setOnClickListener { onLogIn() }

        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = PassengerLoginFragment(FirebaseAuth.getInstance())
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
        Toast.makeText(activity, "LOG IN BUTTON PRESSED!", Toast.LENGTH_SHORT).show()
        val email: String = binding.enterEmailAddress.text.toString().trim()
        val password : String = binding.enterPassword.text.toString()

        if(logInDataValidation(email, password)) {
            progressBar.visibility = View.VISIBLE

            val activity = requireActivity()
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity) { result ->
                    if (result.isSuccessful) {
                        Toast.makeText(activity, getString(R.string.sign_in_success), Toast.LENGTH_SHORT).show()
                        checkAuthState()
                    } else {
                        Toast.makeText(activity, result.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                        binding.enterPassword.text.clear()
                        progressBar.visibility = View.INVISIBLE
                    }
                }
        }
    }

    private fun checkAuthState()
    {
        if(firebaseAuth.currentUser != null)
        {
            startActivity(Intent(activity, PassengerActivity::class.java))
            requireActivity().finish()
        }
    }
}