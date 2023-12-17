package com.example.taxiapp.presentation.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.example.taxiapp.core.UserType
import com.example.taxiapp.core.UserTypeCallback
import com.example.taxiapp.databinding.ActivityLoginBinding
import com.example.taxiapp.presentation.DriverActivity
import com.example.taxiapp.presentation.PassengerActivity
import com.google.android.material.tabs.TabLayout

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity: AppCompatActivity() {

    private val TAG: String? = LoginActivity::class.simpleName

    lateinit var binding : ActivityLoginBinding
    //private lateinit var firebaseAuth : FirebaseAuth
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //firebaseAuth = FirebaseAuth.getInstance()

        checkAuthState()

        val adapter = ViewPagerAdapter(this, firebaseAuth)
        binding.viewPager.adapter = adapter

        //TabLayoutMediator(binding.tabLayout, binding.viewPager)

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewPager.setCurrentItem(tab.position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) { }

            override fun onTabReselected(tab: TabLayout.Tab?) { }
        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.getTabAt(position)?.select()
            }
        })
    }


    private fun checkAuthState()
    {
        if(firebaseAuth.currentUser != null)
        {
            val userId = firebaseAuth.currentUser?.uid
            checkUserType(userId!!, object : UserTypeCallback {
                override fun onUserTypeFound(userType: UserType?) {
                    when (userType) {
                        UserType.PASSENGER -> {
                            startActivity(Intent(this@LoginActivity, PassengerActivity::class.java))
                            finish()
                        }

                        UserType.DRIVER -> {
                            startActivity(Intent(this@LoginActivity, DriverActivity::class.java))
                            finish()
                        }

                        else -> { Log.d(TAG, "User not found in any node") }
                    }
                }
                override fun onUserTypeNotFound() {
                    Log.d(TAG, "User not found in any node")
                }
            })
        }
    }

    fun checkUserType(userId: String, callback: UserTypeCallback) {
        val driversReference = FirebaseDatabase.getInstance().getReference("drivers")

        driversReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // The user ID belongs to the "drivers" node
                    callback.onUserTypeFound(UserType.DRIVER)
                } else {
                    // The user ID is not in the "drivers" node
                    // Check the "passengers" node
                    checkPassengersNode(userId, callback)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                callback.onUserTypeNotFound()
            }
        })
    }

    fun checkPassengersNode(userId: String, callback: UserTypeCallback) {
        val passengersReference = FirebaseDatabase.getInstance().getReference("passengers")

        passengersReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // The user ID belongs to the "passengers" node
                    callback.onUserTypeFound(UserType.PASSENGER)
                } else {
                    // The user ID is not in the "passengers" node either
                    // Handle the case where the user ID is not found in either node
                    callback.onUserTypeNotFound()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                callback.onUserTypeNotFound()
            }
        })
    }
}

