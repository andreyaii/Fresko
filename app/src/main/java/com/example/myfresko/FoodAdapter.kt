package com.example.myfresko

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class FoodAdapter(private val items: List<FoodItem>) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvFoodName)
        val expiry: TextView = view.findViewById(R.id.tvExpiry)
        val days: TextView = view.findViewById(R.id.tvDaysLeft)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        // 1. Set basic text info
        holder.name.text = item.name
        holder.expiry.text = "Expires: ${item.expiryDate}"

        // 2. Dynamic Color Logic using your specific color names
        val (bgColor, textColor) = when {
            item.daysLeft <= 2 -> Pair(R.color.colorExpiryRedBg, R.color.colorExpiryRed)
            item.daysLeft <= 5 -> Pair(R.color.colorExpiryAmberBg, R.color.colorExpiryAmber)
            else -> Pair(R.color.colorExpiryGreenBg, R.color.colorExpiryGreen)
        }

        // 3. Apply colors to the "Days Left" badge
        // Note: holder.days must have android:background="@drawable/bg_badge" in item_food.xml
        holder.days.backgroundTintList = ContextCompat.getColorStateList(context, bgColor)
        holder.days.setTextColor(ContextCompat.getColor(context, textColor))

        // 4. Set the badge text (e.g., "Expired!", "Today!", or "5d left")
        holder.days.text = when {
            item.daysLeft < 0 -> "Expired!"
            item.daysLeft == 0 -> "Today!"
            else -> "${item.daysLeft}d left"
        }

        // 5. Smooth Entry Animation
        holder.itemView.alpha = 0f
        holder.itemView.translationY = 40f
        holder.itemView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(400)
            .setStartDelay(position * 40L)
            .start()
    }

    override fun getItemCount() = items.size
}