package com.udacity.asteroidradar.main

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.R


class AsteroidViewHolder(
    itemView: View,
    private val itemClick: (pos: Int) -> Unit
) : RecyclerView.ViewHolder(itemView) {
    val name: TextView = itemView.findViewById(R.id.asteroid_item_name)
    val date: TextView = itemView.findViewById(R.id.asteroid_item_date)
    val icon: ImageView = itemView.findViewById(R.id.asteroid_item_hazard_icon)

    init {
        itemView.setOnClickListener { itemClick.invoke(adapterPosition) }
    }
}