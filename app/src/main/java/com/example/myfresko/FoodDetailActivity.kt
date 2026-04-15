package com.example.myfresko.home // Update this to match where you save it

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
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

        // Grab the item from the Intent
        val item = intent.getSerializableExtra("FOOD_ITEM") as? FoodItem
        if (item == null) {
            finish() // Close page if no data was found
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

        tvName.text = foodItem.name
        tvCategory.text = foodItem.category
        tvExactDate.text = "Expires on: ${foodItem.expiryDate}"
        tvLogged.text = "Added to inventory on: ${foodItem.date}"

        // Calculate days left for the big text
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = sdf.parse(sdf.format(Date()))
            val expDate = sdf.parse(foodItem.expiryDate)

            if (today != null && expDate != null) {
                val diffInMillies = expDate.time - today.time
                val daysBetween = diffInMillies / (1000 * 60 * 60 * 24)

                when {
                    daysBetween > 0 -> {
                        tvDaysLeft.text = "Expires in $daysBetween day(s)"
                        tvDaysLeft.setTextColor(Color.parseColor("#4CAF50"))
                    }
                    daysBetween == 0L -> {
                        tvDaysLeft.text = "Expires TODAY!"
                        tvDaysLeft.setTextColor(Color.parseColor("#FF9800"))
                    }
                    else -> {
                        tvDaysLeft.text = "Expired ${-daysBetween} day(s) ago"
                        tvDaysLeft.setTextColor(Color.parseColor("#F44336"))
                    }
                }
            }
        } catch (e: Exception) {
            tvDaysLeft.text = "Status Unknown"
        }
    }

    private fun setupButtons() {
        val btnEdit = findViewById<Button>(R.id.btnDetailEdit)
        val btnDelete = findViewById<Button>(R.id.btnDetailDelete)

        btnEdit.setOnClickListener {
            val intent = Intent(this, AddFoodActivity::class.java)
            intent.putExtra("EDIT_FOOD", foodItem)
            startActivity(intent)
            finish() // Close this page so it doesn't stay open in the background
        }

        btnDelete.setOnClickListener {
            db.deleteFood(foodItem.id)
            Toast.makeText(this, "${foodItem.name} deleted", Toast.LENGTH_SHORT).show()
            finish() // Automatically goes back to the previous screen
        }
    }
}


