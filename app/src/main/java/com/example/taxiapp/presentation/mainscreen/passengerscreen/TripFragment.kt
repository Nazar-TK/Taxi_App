package com.example.taxiapp.presentation.mainscreen.passengerscreen

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.taxiapp.R
import com.example.taxiapp.core.Constants
import com.example.taxiapp.data.OrderRepositoryImpl
import com.example.taxiapp.databinding.FragmentTripBinding
import com.example.taxiapp.domain.model.Trip
import com.example.taxiapp.domain.repository.OrderRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.random.Random


class TripFragment : Fragment(){

    lateinit var binding: FragmentTripBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var storageRef : StorageReference
    private val realtimeDB = FirebaseDatabase.getInstance("https://taxiapp-99fcc-default-rtdb.firebaseio.com")
    private lateinit var repository: OrderRepository
    private var price : Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTripBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        firebaseAuth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().getReference(Constants.USERS_AVATARS)
        repository = OrderRepositoryImpl(firebaseAuth, realtimeDB)
        binding.btnOrderTaxi.setOnClickListener {
            createConfirmDialog()
        }
    }
    companion object {
        @JvmStatic
        fun newInstance() = TripFragment()
    }

    private fun createConfirmDialog() {
        val tripInfo = "Create trip from ${binding.etStartLocation.text} to ${binding.etDestinationLocation.text}?"
        price = Random.nextInt(90, 301)
        val priceInfo = "\nPrice will be: ${price} UAH"

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Trip confirmation")
        builder.setMessage(tripInfo + priceInfo)
        builder.setNegativeButton("No") { dialog, id -> }
        builder.setPositiveButton("Yes") { dialog, id ->
            (activity as PassengerActivity?)?.openFragment(
                SearchingDriverFragment.newInstance(),
                R.id.infoFramePlaceHolder
            )
            updateUserData()
        }
        builder.show()
    }

    private fun updateUserData() {
        val start = binding.etStartLocation.text.toString()
        val end = binding.etDestinationLocation.text.toString()
        val price = price

        val order = Trip(startPoint = start, destinationPoint = end, price = price)


//        val updates = mutableMapOf<String, Any>()
//        updates["order"] = order

        repository.saveOrder(order)
    }
}