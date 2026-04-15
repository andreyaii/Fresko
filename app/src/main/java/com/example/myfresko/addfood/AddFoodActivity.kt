package com.example.myfresko.addfood

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myfresko.R
import java.util.Calendar

class AddFoodActivity : AppCompatActivity(), AddFoodContract.View {

    private lateinit var presenter: AddFoodPresenter

    // This will hold the date the user picks from the calendar
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        presenter = AddFoodPresenter(this, this)

        setupCategorySpinner()
        setupDatePicker()
        setupSaveButton()
    }

    private fun setupCategorySpinner() {
        val spinner = findViewById<Spinner>(R.id.spinnerCategory)
        val categories = arrayOf("Dairy", "Produce", "Meat", "Pantry", "Leftovers")

        // We create a custom adapter to override the default text colors
        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories) {

            // 1. Fixes the color of the currently selected item
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getView(position, convertView, parent) as android.widget.TextView
                view.setTextColor(android.graphics.Color.parseColor("#212121")) // Dark grey/black
                return view
            }
            override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getDropDownView(position, convertView, parent) as android.widget.TextView
                view.setTextColor(android.graphics.Color.parseColor("#212121")) // Dark grey/black
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setupDatePicker() {
        val btnSelectDate = findViewById<Button>(R.id.btnSelectDate)

        btnSelectDate.setOnClickListener {
            // Get today's date to show on the calendar by default
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Open the DatePickerDialog
            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->

                // Format the numbers so they are always 2 digits (e.g., "04" instead of "4")
                val formattedMonth = String.format("%02d", selectedMonth + 1)
                val formattedDay = String.format("%02d", selectedDay)

                // Store the date
                selectedDate = "$selectedYear-$formattedMonth-$formattedDay"

                // Change the button text so the user sees what they picked!
                btnSelectDate.text = "Expires: $selectedDate"

            }, year, month, day)

            datePickerDialog.show()
        }
    }

    private fun setupSaveButton() {
        val btnSave = findViewById<Button>(R.id.btnSaveFood)
        val etName = findViewById<EditText>(R.id.etFoodName)

        btnSave.setOnClickListener {
            val name = etName.text.toString()

            // Check if they actually picked a date
            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Please select an expiry date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Send to presenter to save to database
            presenter.validateAndSave(name, selectedDate)
        }
    }

    override fun onSaveSuccess() {
        Toast.makeText(this, "Item Saved!", Toast.LENGTH_SHORT).show()
        finish() // Closes the screen and goes back home
    }

    override fun onSaveError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}