package com.example.taxiapp.presentation.mainscreen.passengerscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.taxiapp.R



class TripInformationFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trip_information, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() = TripInformationFragment()
    }
}