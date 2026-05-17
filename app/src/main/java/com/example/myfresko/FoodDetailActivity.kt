package com.example.myfresko.home

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myfresko.R
import com.example.myfresko.addfood.AddFoodActivity
import com.example.myfresko.common.FreskoToast          // ← custom toast helper
import com.example.myfresko.data.DatabaseHelper
import com.example.myfresko.data.DeletedItemsStore
import com.example.myfresko.model.FoodItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FoodDetailActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var foodItem: FoodItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_detail)

        db = DatabaseHelper(this)

        val item = intent.getSerializableExtra("FOOD_ITEM") as? FoodItem
        if (item == null) { finish(); return }
        foodItem = item

        setupUI()
        setupButtons()
    }

    private fun setupUI() {
        val tvName         = findViewById<TextView>(R.id.tvDetailName)
        val tvCategory     = findViewById<TextView>(R.id.tvDetailCategory)
        val tvDaysLeft     = findViewById<TextView>(R.id.tvDetailDaysLeft)
        val tvExactDate    = findViewById<TextView>(R.id.tvDetailExactDate)
        val tvLogged       = findViewById<TextView>(R.id.tvDetailLogged)
        val layoutExpiryBg = findViewById<LinearLayout>(R.id.layoutExpiryBg)
        val ivCalendarIcon = findViewById<ImageView>(R.id.ivCalendarIcon)

        tvName.text      = foodItem.name
        tvCategory.text  = foodItem.category.uppercase()
        tvExactDate.text = "Expires on: ${foodItem.expiryDate}"
        tvLogged.text    = "Added to FresKo on: ${foodItem.date}"

        try {
            val sdf     = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today   = sdf.parse(sdf.format(Date()))
            val expDate = sdf.parse(foodItem.expiryDate)

            if (today != null && expDate != null) {
                val daysBetween = (expDate.time - today.time) / (1000L * 60 * 60 * 24)

                when {
                    daysBetween > 2 -> {
                        tvDaysLeft.text = "Expires in $daysBetween day(s)"
                        tvDaysLeft.setTextColor(Color.parseColor("#2E7D32"))
                        layoutExpiryBg.setBackgroundColor(Color.parseColor("#E8F5E9"))
                        ivCalendarIcon.imageTintList =
                            ColorStateList.valueOf(Color.parseColor("#2E7D32"))
                    }
                    daysBetween in 0..2 -> {
                        tvDaysLeft.text = if (daysBetween == 0L) "Expires TODAY!" else "Expires in $daysBetween day(s)"
                        tvDaysLeft.setTextColor(Color.parseColor("#E65100"))
                        layoutExpiryBg.setBackgroundColor(Color.parseColor("#FFF3E0"))
                        ivCalendarIcon.imageTintList =
                            ColorStateList.valueOf(Color.parseColor("#E65100"))
                    }
                    else -> {
                        tvDaysLeft.text = "Expired ${-daysBetween} day(s) ago"
                        tvDaysLeft.setTextColor(Color.parseColor("#C62828"))
                        layoutExpiryBg.setBackgroundColor(Color.parseColor("#FFEBEE"))
                        ivCalendarIcon.imageTintList =
                            ColorStateList.valueOf(Color.parseColor("#C62828"))
                    }
                }
            }
        } catch (e: Exception) {
            tvDaysLeft.text = "Status Unknown"
        }
    }

    private fun setupButtons() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        // ── Consumed button ────────────────────────────────────────
        findViewById<Button>(R.id.btnDetailConsumed).setOnClickListener {
            performConsume()
        }

        // ── Edit button — unchanged ────────────────────────────────
        findViewById<Button>(R.id.btnDetailEdit).setOnClickListener {
            startActivity(
                Intent(this, AddFoodActivity::class.java).putExtra("EDIT_FOOD", foodItem)
            )
            finish()
        }

        // ── Delete button — now shows a confirmation dialog ────────
        findViewById<Button>(R.id.btnDetailDelete).setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun performConsume() {
        DeletedItemsStore.add(foodItem, "consumed")
        db.deleteFood(foodItem.id)

        FreskoToast.success(this, "${foodItem.name} marked as consumed")

        startActivity(Intent(this, com.example.myfresko.history.HistoryActivity::class.java))
        finish()
    }

    // ────────────────────────────────────────────────────────────────
    // Confirmation dialog — MaterialAlertDialogBuilder
    // ────────────────────────────────────────────────────────────────

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            // Use a custom style that inherits your app theme so button
            // colors automatically reflect the primary green.
            .setTitle("Delete \"${foodItem.name}\"?")
            .setMessage("This item will be moved to your History log and removed from your active inventory.")
            .setIcon(R.drawable.ic_delete)

            // ── Positive: destructive action ───────────────────────
            .setPositiveButton("DELETE") { dialog, _ ->
                performDelete()
                dialog.dismiss()
            }

            // ── Negative: safe exit ────────────────────────────────
            .setNegativeButton("KEEP IT") { dialog, _ ->
                dialog.dismiss()   // do nothing; user changed their mind
            }

            .create()
            .also { alertDialog ->
                alertDialog.show()

                // ── Style buttons after show() so they exist in the view tree ──
                // Positive button → red text (danger signal)
                alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)?.apply {
                    setTextColor(Color.parseColor("#D32F2F"))
                    isAllCaps = true
                }
                // Negative button → primary green (safe action)
                alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)?.apply {
                    setTextColor(Color.parseColor("#0B6646"))
                    isAllCaps = true
                }
            }
    }

    // ── Extracted delete logic so the dialog callback stays clean ──

    private fun performDelete() {
        // Soft-delete: push to in-memory History store, then wipe from DB
        DeletedItemsStore.add(foodItem)
        db.deleteFood(foodItem.id)

        // Custom red toast with trash icon
        FreskoToast.deleted(this, "${foodItem.name} moved to History")

        finish()
    }
}