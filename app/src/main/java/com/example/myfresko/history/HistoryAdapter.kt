package com.example.myfresko.history

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfresko.R
import com.example.myfresko.model.FoodItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private val items: MutableList<FoodItem>,
    private val onPermanentDelete: (FoodItem, Int) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIcon: TextView       = view.findViewById(R.id.tvHistoryIcon)
        val tvName: TextView       = view.findViewById(R.id.tvHistoryName)
        val tvStatus: TextView     = view.findViewById(R.id.tvHistoryStatus)
        val tvDate: TextView       = view.findViewById(R.id.tvHistoryDate)
        val tvCategory: TextView   = view.findViewById(R.id.tvHistoryCategory)
        val btnDelete: ImageButton = view.findViewById(R.id.btnHistoryDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tvName.text     = item.name
        holder.tvDate.text     = "Exp: ${item.expiryDate}"
        holder.tvCategory.text = "📍 ${item.category}"
        holder.tvIcon.text     = emojiForCategory(item.category)

        // ── Icon circle bg ────────────────────────────────────────
        val iconBg = when (item.category.lowercase()) {
            "fridge"  -> "#E3F2FD"
            "pantry"  -> "#FBE9E7"
            "freezer" -> "#E0F7FA"
            else      -> "#F3E5F5"
        }
        holder.tvIcon.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(iconBg))

        // ── Status chip ───────────────────────────────────────────
        if (item.status == "deleted") {
            // Manually deleted by the user
            applyChip(holder, label = "🗑 Deleted", text = "#6D4C41", bg = "#EFEBE9")
        } else {
            // Derive expired/fresh from date
            try {
                val sdf      = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val todayStr = sdf.format(Date())
                val today    = sdf.parse(todayStr)
                val expDate  = sdf.parse(item.expiryDate)
                if (today != null && expDate != null) {
                    val daysLeft = (expDate.time - today.time) / (1000L * 60 * 60 * 24)
                    when {
                        daysLeft < 0  -> applyChip(holder, "⏰ Expired",         "#C62828", "#FFEBEE")
                        daysLeft == 0L -> applyChip(holder, "⚠ Expires Today",   "#E65100", "#FFF3E0")
                        else           -> applyChip(holder, "✅ Fresh",           "#2E7D32", "#E8F5E9")
                    }
                }
            } catch (e: Exception) {
                applyChip(holder, "Unknown", "#757575", "#F5F5F5")
            }
        }

        holder.btnDelete.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) onPermanentDelete(item, pos)
        }
    }

    override fun getItemCount() = items.size

    fun removeAt(position: Int) {
        if (position in 0 until items.size) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun applyChip(holder: ViewHolder, label: String, text: String, bg: String) {
        holder.tvStatus.text = label
        holder.tvStatus.setTextColor(Color.parseColor(text))
        holder.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor(bg))
    }

    private fun emojiForCategory(category: String) = when (category.lowercase()) {
        "fridge"  -> "🥦"
        "pantry"  -> "🥫"
        "freezer" -> "❄️"
        else      -> "🍽️"
    }
}