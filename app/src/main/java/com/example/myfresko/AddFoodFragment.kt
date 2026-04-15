package com.example.myfresko

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker

class AddFoodFragment : Fragment(R.layout.fragment_add_food) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Setup the Category Dropdown (Exposed Dropdown Menu)
        val categories = arrayOf("Fridge", "Pantry", "Freezer",)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categories)
        val autoComplete = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteCategory)
        autoComplete.setAdapter(adapter)

        // 2. Setup the Date Picker
        val btnPickDate = view.findViewById<Button>(R.id.btnPickDate)
        btnPickDate.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select expiry date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            picker.show(parentFragmentManager, "expiry_picker")

            picker.addOnPositiveButtonClickListener { selection ->
                // This updates the button text with the chosen date (e.g., Apr 15, 2026)
                btnPickDate.text = picker.headerText
            }
        }

        // 3. Save logic
        val saveButton = view.findViewById<Button>(R.id.btnSave)
        saveButton.setOnClickListener {
            // TODO: Collect data from etFoodName, autoCompleteCategory, and the date selection
            // and save it to your MockData or Database.

            findNavController().popBackStack()
        }
    }
}