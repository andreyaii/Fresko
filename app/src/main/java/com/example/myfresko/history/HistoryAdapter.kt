package com.example.myfresko.history

import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfresko.R
import com.example.myfresko.model.FoodItem
import java.util.Locale

class HistoryAdapter(
    private var items: MutableList<FoodItem>,
    private val onDelete: (FoodItem, Int) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView         = view.findViewById(R.id.tvHistoryName)
        val status: TextView       = view.findViewById(R.id.tvHistoryStatus)
        val date: TextView         = view.findViewById(R.id.tvHistoryDate)
        val category: TextView     = view.findViewById(R.id.tvHistoryCategory)
        val statusDot: View        = view.findViewById(R.id.viewStatusDot)
        val btnDelete: ImageButton = view.findViewById(R.id.btnHistoryDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text     = item.name
        holder.date.text     = "Exp: ${item.expiryDate}"
        holder.category.text = "📍 ${item.category}"

        // Derive status from expiry date
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = sdf.format(java.util.Date())
            val today    = sdf.parse(todayStr)
            val expDate  = sdf.parse(item.expiryDate)

            if (today != null && expDate != null) {
                val daysLeft = (expDate.time - today.time) / (1000 * 60 * 60 * 24)
                when {
                    daysLeft < 0 -> {
                        // Already expired
                        holder.status.text = "Expired"
                        holder.status.setTextColor(Color.parseColor("#D32F2F"))
                        holder.status.backgroundTintList =
                            android.content.res.ColorStateList.valueOf(Color.parseColor("#FFEBEE"))
                        holder.statusDot.backgroundTintList =
                            android.content.res.ColorStateList.valueOf(Color.parseColor("#D32F2F"))
                    }
                    daysLeft == 0L -> {
                        holder.status.text = "Expires Today"
                        holder.status.setTextColor(Color.parseColor("#E65100"))
                        holder.status.backgroundTintList =
                            android.content.res.ColorStateList.valueOf(Color.parseColor("#FFF3E0"))
                        holder.statusDot.backgroundTintList =
                            android.content.res.ColorStateList.valueOf(Color.parseColor("#E65100"))
                    }
                    else -> {
                        holder.status.text = "Fresh ($daysLeft days left)"
                        holder.status.setTextColor(Color.parseColor("#2E7D32"))
                        holder.status.backgroundTintList =
                            android.content.res.ColorStateList.valueOf(Color.parseColor("#E8F5E9"))
                        holder.statusDot.backgroundTintList =
                            android.content.res.ColorStateList.valueOf(Color.parseColor("#2E7D32"))
                    }
                }
            }
        } catch (e: Exception) {
            holder.status.text = "Unknown"
        }

        holder.btnDelete.setOnClickListener {
            onDelete(item, holder.adapterPosition)
        }
    }

    override fun getItemCount() = items.size

    /** Remove row from local list immediately (optimistic UI) */
    fun removeAt(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}