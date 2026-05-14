package com.example.myfresko.addfood

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myfresko.R
import com.example.myfresko.common.FreskoToast
import com.example.myfresko.model.FoodItem
import java.util.Calendar
import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.ImageView

class AddFoodActivity : AppCompatActivity(), AddFoodContract.View {

    private lateinit var presenter: AddFoodPresenter
    private var selectedDate: String = ""
    private var editItem: FoodItem? = null

    // 1. Track the selected category as a string
    private var selectedCategory: String = "Fridge"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        editItem = intent.getSerializableExtra("EDIT_FOOD") as? FoodItem
        presenter = AddFoodPresenter(this, this)

        setupBackButton()
        setupCategorySelection() // 2. Replaced setupCategorySpinner
        setupDatePicker()
        prefillFieldsIfEditing()
        setupSaveButton()
    }

    private fun prefillFieldsIfEditing() {
        val item = editItem ?: return

        findViewById<EditText>(R.id.etFoodName).setText(item.name)

        // 3. Set the category from the item and update UI
        selectedCategory = item.category
        updateCategoryUI()

        selectedDate = item.expiryDate
        val tvDate = findViewById<TextView>(R.id.tvSelectedDateDisplay)
        tvDate.text = "Selected: $selectedDate"
        tvDate.setTextColor(android.graphics.Color.parseColor("#0B6646"))

        findViewById<Button>(R.id.btnSaveFood).text = "UPDATE ITEM"
    }

    private fun setupBackButton() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }

    // 4. New logic for the 3-button category row
    private fun setupCategorySelection() {
        val btnFridge = findViewById<LinearLayout>(R.id.btnCatFridge)
        val btnPantry = findViewById<LinearLayout>(R.id.btnCatPantry)
        val btnFreezer = findViewById<LinearLayout>(R.id.btnCatFreezer)

        btnFridge.setOnClickListener {
            selectedCategory = "Fridge"
            updateCategoryUI()
        }
        btnPantry.setOnClickListener {
            selectedCategory = "Pantry"
            updateCategoryUI()
        }
        btnFreezer.setOnClickListener {
            selectedCategory = "Freezer"
            updateCategoryUI()
        }

        // Initial state
        updateCategoryUI()
    }

    // 5. Helper function to change button colors
    private fun updateCategoryUI() {
        val btnFridge = findViewById<LinearLayout>(R.id.btnCatFridge)
        val btnPantry = findViewById<LinearLayout>(R.id.btnCatPantry)
        val btnFreezer = findViewById<LinearLayout>(R.id.btnCatFreezer)

        // Helper function to color the buttons dynamically
        fun applyStyle(view: LinearLayout, isSelected: Boolean, darkColor: String, lightColor: String) {
            // Use the same drawable for everyone
            view.setBackgroundResource(R.drawable.bg_rounded_chip)

            // Pick the color based on selection
            val bgColor = if (isSelected) Color.parseColor(darkColor) else Color.parseColor(lightColor)
            val contentColor = if (isSelected) Color.WHITE else Color.parseColor(darkColor)

            // Apply the background color (Tinting)
            view.backgroundTintList = ColorStateList.valueOf(bgColor)

            // Apply colors to the Icon and Text inside the LinearLayout
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                if (child is ImageView) child.setColorFilter(contentColor)
                if (child is TextView) child.setTextColor(contentColor)
            }
        }

        // Apply the logic to all three buttons
        applyStyle(btnFridge, selectedCategory == "Fridge", "#0B6646", "#E8F5E9") // Green
        applyStyle(btnPantry, selectedCategory == "Pantry", "#E65100", "#FFF3E0") // Orange
        applyStyle(btnFreezer, selectedCategory == "Freezer", "#1976D2", "#E3F2FD") // Blue
    }

    private fun setupDatePicker() {
        val btnSelectDate = findViewById<Button>(R.id.btnSelectDate)
        val tvDate = findViewById<TextView>(R.id.tvSelectedDateDisplay)

        btnSelectDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                val m = String.format("%02d", month + 1)
                val d = String.format("%02d", day)
                selectedDate = "$year-$m-$d"
                tvDate.text = "Selected: $selectedDate"
                tvDate.setTextColor(android.graphics.Color.parseColor("#0B6646"))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupSaveButton() {
        val btnSave = findViewById<Button>(R.id.btnSaveFood)
        val etName = findViewById<EditText>(R.id.etFoodName)

        btnSave.setOnClickListener {
            val name = etName.text.toString()

            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Please select an expiry date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 6. Pass the 'selectedCategory' variable instead of spinner value
            presenter.validateAndSave(name, selectedDate, selectedCategory, editItem?.id ?: -1)
        }
    }

    override fun onSaveSuccess() {
        if (editItem != null) {
            FreskoToast.updated(this, "${editItem!!.name} updated!")
        } else {
            FreskoToast.created(this, "Item added to your inventory!")
        }
        finish()
    }

    override fun onSaveError(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}