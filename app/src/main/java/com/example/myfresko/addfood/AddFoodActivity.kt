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
import com.example.myfresko.common.FreskoToast //
import com.example.myfresko.model.FoodItem
import java.util.Calendar

class AddFoodActivity : AppCompatActivity(), AddFoodContract.View {

    private lateinit var presenter: AddFoodPresenter
    private var selectedDate: String = ""
    private var editItem: FoodItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        editItem = intent.getSerializableExtra("EDIT_FOOD") as? FoodItem
        presenter = AddFoodPresenter(this, this)

        setupBackButton()
        setupCategorySpinner()
        setupDatePicker()
        prefillFieldsIfEditing()
        setupSaveButton()
    }

    private fun prefillFieldsIfEditing() {
        val item = editItem ?: return

        findViewById<EditText>(R.id.etFoodName).setText(item.name)

        val spinner = findViewById<Spinner>(R.id.spinnerCategory)
        val categories = listOf("Fridge", "Pantry", "Freezer")
        val idx = categories.indexOfFirst { it.equals(item.category, ignoreCase = true) }
        if (idx >= 0) spinner.setSelection(idx)

        selectedDate = item.expiryDate
        val tvDate = findViewById<TextView>(R.id.tvSelectedDateDisplay)
        tvDate.text = "Selected: $selectedDate"
        tvDate.setTextColor(android.graphics.Color.parseColor("#0B6646"))

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

            presenter.validateAndSave(name, selectedDate, category, editItem?.id ?: -1)
        }
    }

    // --- UPDATED CALLBACKS ---

    override fun onSaveSuccess() {
        if (editItem != null) {
            // Branded update feedback with refresh icon
            FreskoToast.updated(this, "${editItem!!.name} updated!")
        } else {
            // Branded creation feedback with check icon
            FreskoToast.created(this, "Item added to your inventory!")
        }
        finish()
    }

    override fun onSaveError(message: String) {
        // Plain system Toast remains appropriate for error messages
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}