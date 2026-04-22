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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myfresko.R
import com.example.myfresko.addfood.AddFoodActivity
import com.example.myfresko.data.DatabaseHelper
import com.example.myfresko.model.FoodItem
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
        if (item == null) {
            finish()
            return
        }
        foodItem = item

        setupUI()
        setupButtons()
    }

    private fun setupUI() {
        val tvName = findViewById<TextView>(R.id.tvDetailName)
        val tvCategory = findViewById<TextView>(R.id.tvDetailCategory)
        val tvDaysLeft = findViewById<TextView>(R.id.tvDetailDaysLeft)
        val tvExactDate = findViewById<TextView>(R.id.tvDetailExactDate)
        val tvLogged = findViewById<TextView>(R.id.tvDetailLogged)

        // New references for dynamic styling
        val layoutExpiryBg = findViewById<LinearLayout>(R.id.layoutExpiryBg)
        val ivCalendarIcon = findViewById<ImageView>(R.id.ivCalendarIcon)

        tvName.text = foodItem.name
        // Make category uppercase for a cleaner look
        tvCategory.text = foodItem.category.uppercase()
        tvExactDate.text = "Expires on: ${foodItem.expiryDate}"

        // Cleaned up the injected string
        tvLogged.text = "Added to FresKo on: ${foodItem.date}"

        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = sdf.parse(sdf.format(Date()))
            val expDate = sdf.parse(foodItem.expiryDate)

            if (today != null && expDate != null) {
                val diffInMillies = expDate.time - today.time
                val daysBetween = diffInMillies / (1000 * 60 * 60 * 24)

                when {
                    daysBetween > 2 -> { // Fresh
                        tvDaysLeft.text = "Expires in $daysBetween day(s)"
                        tvDaysLeft.setTextColor(Color.parseColor("#2E7D32")) // Dark Green text
                        layoutExpiryBg.setBackgroundColor(Color.parseColor("#E8F5E9")) // Light Green Box
                        ivCalendarIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#2E7D32"))
                    }
                    daysBetween in 0..2 -> { // Expiring Soon / Today
                        tvDaysLeft.text = if (daysBetween == 0L) "Expires TODAY!" else "Expires in $daysBetween day(s)"
                        tvDaysLeft.setTextColor(Color.parseColor("#E65100")) // Dark Orange text
                        layoutExpiryBg.setBackgroundColor(Color.parseColor("#FFF3E0")) // Light Orange Box
                        ivCalendarIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#E65100"))
                    }
                    else -> { // Expired
                        tvDaysLeft.text = "Expired ${-daysBetween} day(s) ago"
                        tvDaysLeft.setTextColor(Color.parseColor("#C62828")) // Dark Red text
                        layoutExpiryBg.setBackgroundColor(Color.parseColor("#FFEBEE")) // Light Red Box
                        ivCalendarIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#C62828"))
                    }
                }
            }
        } catch (e: Exception) {
            tvDaysLeft.text = "Status Unknown"
        }
    }

    private fun setupButtons() {
        // Handle Back Button
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnDetailEdit).setOnClickListener {
            val intent = Intent(this, AddFoodActivity::class.java)
            intent.putExtra("EDIT_FOOD", foodItem)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.btnDetailDelete).setOnClickListener {
            db.deleteFood(foodItem.id)
            Toast.makeText(this, "${foodItem.name} removed from fridge", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}