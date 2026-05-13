package com.example.myfresko.common

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfresko.R
import com.example.myfresko.model.FoodItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FoodAdapter(
    private val items: List<FoodItem>,
    private val onItemClick: (FoodItem) -> Unit
) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Changed from TextView to ImageView to support Vector Icons
        val categoryIcon: ImageView = view.findViewById(R.id.ivFoodCategoryIcon)
        val name: TextView          = view.findViewById(R.id.tvFoodName)
        val expiry: TextView        = view.findViewById(R.id.tvExpiryDate)
        val categoryLabel: TextView = view.findViewById(R.id.tvFoodCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.name.text          = item.name
        holder.categoryLabel.text = item.category

        // ── CATEGORY ICON & COLOR LOGIC ───────────────────────────
        // This makes the icon consistent with the Home Screen
        when (item.category.lowercase()) {
            "fridge" -> {
                holder.categoryIcon.setImageResource(R.drawable.ic_fridge)
                holder.categoryIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E8F5E9")) // Light Green
                holder.categoryIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#2E7D32"))      // Dark Green
            }
            "pantry" -> {
                holder.categoryIcon.setImageResource(R.drawable.ic_pantry)
                holder.categoryIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFF3E0")) // Light Orange
                holder.categoryIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#E65100"))      // Dark Orange
            }
            "freezer" -> {
                holder.categoryIcon.setImageResource(R.drawable.ic_freezer)
                holder.categoryIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E3F2FD")) // Light Blue
                holder.categoryIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#1976D2"))      // Dark Blue
            }
            else -> {
                holder.categoryIcon.setImageResource(R.drawable.ic_home)
                holder.categoryIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F5F5F5"))
                holder.categoryIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
            }
        }

        // ── DAYS-LEFT CALCULATION ──────────────────────────────────
        try {
            val sdf      = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = sdf.format(Date())
            val today    = sdf.parse(todayStr)
            val expDate  = sdf.parse(item.expiryDate)

            if (today != null && expDate != null) {
                val daysBetween = (expDate.time - today.time) / (1000L * 60 * 60 * 24)

                when {
                    daysBetween > 2 -> {
                        holder.expiry.text = "$daysBetween days left"
                        holder.expiry.setTextColor(Color.parseColor("#757575")) // Neutral grey for fresh
                    }
                    daysBetween in 0..2 -> {
                        holder.expiry.text = if(daysBetween == 0L) "Expires TODAY" else "$daysBetween day(s) left"
                        holder.expiry.setTextColor(Color.parseColor("#E65100")) // Orange for warning
                    }
                    else -> {
                        holder.expiry.text = "Expired ${-daysBetween} day(s) ago"
                        holder.expiry.setTextColor(Color.parseColor("#C62828")) // Red for expired
                    }
                }
            }
        } catch (e: Exception) {
            holder.expiry.text = "Exp: ${item.expiryDate}"
            holder.expiry.setTextColor(Color.DKGRAY)
        }

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount() = items.size
}