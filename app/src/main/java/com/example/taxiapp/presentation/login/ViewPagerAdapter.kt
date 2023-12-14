package com.example.taxiapp.presentation.login

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.firebase.auth.FirebaseAuth

const val NUM_OF_TABS = 2

class ViewPagerAdapter(fragmentActivity: FragmentActivity, firebaseAuth: FirebaseAuth) :
    FragmentStateAdapter(fragmentActivity) {

    private val firebaseAuth = firebaseAuth

    override fun getItemCount(): Int = NUM_OF_TABS

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> return PassengerLoginFragment(firebaseAuth)
            1 -> return DriverLoginFragment(firebaseAuth)
            else -> return PassengerLoginFragment(firebaseAuth)
        }
    }

}