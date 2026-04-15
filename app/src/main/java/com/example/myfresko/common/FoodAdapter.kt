package com.example.myfresko.common

import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeUnit
import android.net.ParseException
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeParseException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfresko.R
import com.example.myfresko.model.FoodItem
import java.sql.Date
import java.util.Locale

class FoodAdapter(
    private val items: List<FoodItem>,
    private val onItemClick: (FoodItem) -> Unit
) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvFoodName)
        // CHANGED: Now looks for the Expiry Date text view
        val expiry: TextView = view.findViewById(R.id.tvExpiryDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name

        // The Smart "Days Left" Calculation
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            // Format today's date to zero-out the hours/minutes for accurate math
            val todayStr = sdf.format(java.util.Date())
            val today = sdf.parse(todayStr)
            val expDate = sdf.parse(item.expiryDate)

            if (today != null && expDate != null) {
                // Calculate difference in milliseconds, then convert to days
                // Calculate difference in milliseconds
                val diffInMillies = expDate.time - today.time

// Convert to days manually: (1000ms * 60sec * 60min * 24hr)
                val daysBetween = diffInMillies / (1000 * 60 * 60 * 24)

                when {
                    daysBetween > 0 -> {
                        holder.expiry.text = "$daysBetween day(s) left"
                        holder.expiry.setTextColor(Color.parseColor("#4CAF50")) // Green
                    }
                    daysBetween == 0L -> {
                        holder.expiry.text = "Expires TODAY"
                        holder.expiry.setTextColor(Color.parseColor("#FF9800")) // Orange
                    }
                    else -> {
                        val daysAgo = -daysBetween
                        holder.expiry.text = "Expired $daysAgo day(s) ago"
                        holder.expiry.setTextColor(Color.parseColor("#F44336")) // Red
                    }
                }
            }
        } catch (e: ParseException) {
            // Fallback if the date text is formatted incorrectly
            holder.expiry.text = "Exp: ${item.expiryDate}"
            holder.expiry.setTextColor(Color.DKGRAY)
        }

        // Handle Long Click for Deletion
        holder.itemView.setOnClickListener {
            onItemClick(item) // Pass the whole item back!
        }
    }

    override fun getItemCount() = items.size
}