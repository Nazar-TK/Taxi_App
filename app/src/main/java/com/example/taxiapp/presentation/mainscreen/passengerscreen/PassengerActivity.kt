package com.example.taxiapp.presentation.mainscreen.passengerscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.taxiapp.R
import com.example.taxiapp.databinding.ActivityPassengerBinding
import com.example.taxiapp.presentation.userprofile.PassengerProfileActivity

class PassengerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPassengerBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passenger)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_passenger)

        openFragment(TripFragment.newInstance(), R.id.tripFramePlaceHolder)
        //openFragment(SearchingDriverFragment.newInstance(), R.id.infoFramePlaceHolder)
        binding.customToolbar.userProfile.setOnClickListener {
            startActivity(Intent(this, PassengerProfileActivity::class.java))
        }


    }

    fun openFragment(f: Fragment, idHolder: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(idHolder, f)
            .commit()
    }
}