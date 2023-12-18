package com.example.taxiapp.presentation.mainscreen.driverscreen

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taxiapp.R
import com.example.taxiapp.databinding.OrderItemBinding
import com.example.taxiapp.domain.model.Trip

class OrderRVAdapter(private val orderList: ArrayList<Trip>, val listener: (Trip) -> Unit): RecyclerView.Adapter<OrderRVAdapter.OrderHolder>() {
    inner class OrderHolder(val binding: OrderItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = OrderItemBinding.inflate(layoutInflater, parent, false)
        return OrderHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int) {
        holder.binding.apply {
            tvFromPlace.text = "Start location: ${orderList[position].startPoint}"
            tvToPlace.text = "Destination location: ${orderList[position].destinationPoint}"
            tvPrice.text = "Price: ${orderList[position].price.toString()}"
            btnPickOrder.setOnClickListener {
                listener(orderList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        Log.d("DriverActivity", "HERE Count!!! ${orderList.size}")
        return orderList.size
    }
}