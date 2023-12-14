package com.example.taxiapp.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.taxiapp.R
import com.example.taxiapp.presentation.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class UserProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
    }

    fun logOut(view: View) {
        FirebaseAuth.getInstance().signOut() //logout
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}