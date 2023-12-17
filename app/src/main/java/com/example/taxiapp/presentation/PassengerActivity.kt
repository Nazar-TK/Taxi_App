package com.example.taxiapp.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.taxiapp.R
import com.example.taxiapp.databinding.ActivityPassengerBinding
import com.example.taxiapp.presentation.userprofile.PassengerProfileActivity

class PassengerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPassengerBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passenger)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_passenger)


        binding.customToolbar.userProfile.setOnClickListener {
            startActivity(Intent(this, PassengerProfileActivity::class.java))
        }
    }
}