package com.example.taxiapp.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.taxiapp.R
import com.example.taxiapp.databinding.ActivityDriverBinding
import com.example.taxiapp.databinding.ActivityPassengerBinding

class DriverActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDriverBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver)


        binding.customToolbar.userProfile.setOnClickListener {
            startActivity(Intent(this, DriverProfileActivity::class.java))
        }
    }
}