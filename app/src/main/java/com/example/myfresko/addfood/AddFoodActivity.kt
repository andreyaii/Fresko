package com.example.myfresko.addfood

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myfresko.R
import com.example.myfresko.model.FoodItem
import java.util.Calendar

class AddFoodActivity : AppCompatActivity(), AddFoodContract.View {

    private lateinit var presenter: AddFoodPresenter
    private var selectedDate: String = ""

    // Holds the item being edited; null when adding a new item
    private var editItem: FoodItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        // Check whether we were launched from FoodDetailActivity with an item to edit
        editItem = intent.getSerializableExtra("EDIT_FOOD") as? FoodItem

        presenter = AddFoodPresenter(this, this)

        setupBackButton()
        setupCategorySpinner()
        setupDatePicker()
        prefillFieldsIfEditing()   // ← NEW
        setupSaveButton()
    }

    // ---------------------------------------------------------------
    // NEW: pre-populate all fields when in edit mode
    // ---------------------------------------------------------------
    private fun prefillFieldsIfEditing() {
        val item = editItem ?: return   // nothing to do for a new item

        // Pre-fill name
        findViewById<EditText>(R.id.etFoodName).setText(item.name)

        // Pre-select category in spinner
        val spinner = findViewById<Spinner>(R.id.spinnerCategory)
        val categories = listOf("Fridge", "Pantry", "Freezer")
        val idx = categories.indexOfFirst { it.equals(item.category, ignoreCase = true) }
        if (idx >= 0) spinner.setSelection(idx)

        // Pre-fill date
        selectedDate = item.expiryDate
        val tvDate = findViewById<TextView>(R.id.tvSelectedDateDisplay)
        tvDate.text = "Selected: $selectedDate"
        tvDate.setTextColor(android.graphics.Color.parseColor("#0B6646"))

        // Update the screen title to say "Edit Item"
        // (The header TextView doesn't have an ID in the current layout,
        //  so we just change the save button label instead)
        findViewById<Button>(R.id.btnSaveFood).text = "UPDATE ITEM"
    }

    private fun setupBackButton() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun setupCategorySpinner() {
        val spinner = findViewById<Spinner>(R.id.spinnerCategory)
        val categories = arrayOf("Fridge", "Pantry", "Freezer")

        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getView(position, convertView, parent) as android.widget.TextView
                view.setTextColor(android.graphics.Color.parseColor("#212121"))
                return view
            }
            override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getDropDownView(position, convertView, parent) as android.widget.TextView
                view.setTextColor(android.graphics.Color.parseColor("#212121"))
                return view
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
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
        val spinner = findViewById<Spinner>(R.id.spinnerCategory)

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val category = spinner.selectedItem.toString()

            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Please select an expiry date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Pass -1 for a new item, or the real ID for an edit
            presenter.validateAndSave(name, selectedDate, category, editItem?.id ?: -1)
        }
    }

    override fun onSaveSuccess() {
        val msg = if (editItem != null) "Item Updated!" else "Item Saved!"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onSaveError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}