package com.example.myfresko.common

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.myfresko.R

/**
 * FreskoToast
 *
 * A dead-simple helper for showing branded, icon-bearing feedback toasts.
 * All three CRUD operations (Create, Update, Delete) have a dedicated
 * entry point so call-sites stay readable.
 *
 * Usage examples
 * ──────────────
 *   // In AddFoodActivity.onSaveSuccess() — new item:
 *   FreskoToast.created(this, "Milk added to Fridge")
 *
 *   // In AddFoodActivity.onSaveSuccess() — edited item:
 *   FreskoToast.updated(this, "Milk updated")
 *
 *   // In FoodDetailActivity delete button handler:
 *   FreskoToast.deleted(this, "Milk moved to History")
 */
object FreskoToast {

    // ── Public API ─────────────────────────────────────────────────

    /** Green toast with a ✓ check icon — item was CREATED. */
    fun created(context: Context, message: String = "Item saved!") {
        show(
            context  = context,
            message  = message,
            icon     = "✓",
            bgColor  = "#0B6646"   // primary Fresko green
        )
    }

    /** Green toast with a ↺ refresh icon — item was UPDATED. */
    fun updated(context: Context, message: String = "Item updated!") {
        show(
            context  = context,
            message  = message,
            icon     = "↺",
            bgColor  = "#0B6646"   // same green; different icon signals the operation
        )
    }

    /** Red toast with a 🗑 trash icon — item was DELETED. */
    fun deleted(context: Context, message: String = "Item deleted.") {
        show(
            context  = context,
            message  = message,
            icon     = "🗑",
            bgColor  = "#D32F2F"   // Material red — danger/destructive action
        )
    }

    // ── Private builder ────────────────────────────────────────────

    private fun show(
        context : Context,
        message : String,
        icon    : String,
        bgColor : String
    ) {
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.layout_custom_toast, null)

        // Tint the root background with the action-specific color
        view.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(bgColor))

        // Set icon character and message text
        view.findViewById<TextView>(R.id.tvToastIcon).text    = icon
        view.findViewById<TextView>(R.id.tvToastMessage).text = message

        with(Toast(context)) {
            duration = Toast.LENGTH_SHORT
            this.view = view           // deprecated in API 30 but still functional
            show()
        }
    }
}