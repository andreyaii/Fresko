package com.example.myfresko.common

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        val icon: TextView     = view.findViewById(R.id.tvFoodIcon)
        val name: TextView     = view.findViewById(R.id.tvFoodName)
        val expiry: TextView   = view.findViewById(R.id.tvExpiryDate)
        val category: TextView = view.findViewById(R.id.tvFoodCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.name.text     = item.name
        holder.category.text = item.category

        // ── Dynamic emoji icon based on category ──────────────────
        holder.icon.text = emojiForCategory(item.category)

        // ── Icon circle background tint per category ───────────────
        val iconBgColor = when (item.category.lowercase()) {
            "fridge"  -> "#E3F2FD"
            "pantry"  -> "#FFF3E0"
            "freezer" -> "#E0F7FA"
            else      -> "#E8F5E9"
        }
        holder.icon.backgroundTintList =
            android.content.res.ColorStateList.valueOf(Color.parseColor(iconBgColor))

        // ── Days-left calculation ──────────────────────────────────
        try {
            val sdf      = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = sdf.format(Date())
            val today    = sdf.parse(todayStr)
            val expDate  = sdf.parse(item.expiryDate)

            if (today != null && expDate != null) {
                val daysBetween = (expDate.time - today.time) / (1000L * 60 * 60 * 24)
                when {
                    daysBetween > 0 -> {
                        holder.expiry.text = "$daysBetween day(s) left"
                        holder.expiry.setTextColor(Color.parseColor("#2E7D32"))
                    }
                    daysBetween == 0L -> {
                        holder.expiry.text = "Expires TODAY"
                        holder.expiry.setTextColor(Color.parseColor("#E65100"))
                    }
                    else -> {
                        holder.expiry.text = "Expired ${-daysBetween} day(s) ago"
                        holder.expiry.setTextColor(Color.parseColor("#C62828"))
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

    // ── Maps category → a relevant emoji ──────────────────────────
    private fun emojiForCategory(category: String): String = when (category.lowercase()) {
        "fridge"  -> "🥦"
        "pantry"  -> "🥫"
        "freezer" -> "❄️"
        else      -> "🍽️"
    }
}