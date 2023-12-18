package com.example.taxiapp.presentation.mainscreen.driverscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taxiapp.R
import com.example.taxiapp.core.Constants
import com.example.taxiapp.data.OrderRepositoryImpl
import com.example.taxiapp.databinding.ActivityDriverBinding
import com.example.taxiapp.domain.model.Trip
import com.example.taxiapp.domain.repository.OrderRepository
import com.example.taxiapp.presentation.userprofile.DriverProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DriverActivity : AppCompatActivity() {

    private val TAG: String? = DriverActivity::class.simpleName
    private lateinit var binding: ActivityDriverBinding

    private lateinit var firebaseAuth : FirebaseAuth
    private val realtimeDB = FirebaseDatabase.getInstance("https://taxiapp-99fcc-default-rtdb.firebaseio.com")
    private lateinit var orderItems: ArrayList<Trip>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver)

        firebaseAuth = FirebaseAuth.getInstance()
        orderItems = arrayListOf()

        Log.d(TAG, "HERE")
        binding.rvOrders.setHasFixedSize(true)
        binding.rvOrders.layoutManager = LinearLayoutManager(this)
        Log.d(TAG, "HERE!")
        binding.customToolbar.userProfile.setOnClickListener {
            startActivity(Intent(this, DriverProfileActivity::class.java))
        }

        getData()
    }

    private fun getData() {
        Log.d(TAG, "HERE!!!")
        val databaseRef = realtimeDB.getReference(Constants.ORDERS)
        Log.d(TAG, "HERE!!!1")
        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(orderItems.isNotEmpty()){
                    orderItems.clear()
                }
                Log.d(TAG, "HERE!!!2")
                Log.d(TAG, "HERE!!! ${snapshot.childrenCount}")
                if(snapshot.exists()) {
                    for(orderSnap in snapshot.children) {
                        val orders = orderSnap.getValue(Trip::class.java)
                        Log.d(TAG, "HERE!!! IM")
                        if(orders != null) {
                            orderItems.add(orders)
                        }
                    }
                }
                val rvAdapter = OrderRVAdapter(orderItems){ clickedItem ->
                    // Handle the button click for the specific item
                    // You can perform any action here using the clickedItem data
                    // For example, show a Toast
                    Toast.makeText(baseContext, "Button clicked for $clickedItem", Toast.LENGTH_SHORT).show()
                }
                binding.rvOrders.adapter = rvAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, error.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }
}