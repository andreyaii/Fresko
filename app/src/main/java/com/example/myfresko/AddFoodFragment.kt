package com.example.myfresko

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker

class AddFoodFragment : Fragment(R.layout.fragment_add_food) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Find your views
        val saveButton = view.findViewById<Button>(R.id.btnSave)
        // Inside onViewCreated in AddFoodFragment.kt

        val btnPickDate = view.findViewById<Button>(R.id.btnPickDate)

        btnPickDate.setOnClickListener {
            // We removed the .setTheme line because the theme is now
            // handled automatically by your themes.xml
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select expiry date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            picker.show(parentFragmentManager, "expiry_picker")

            picker.addOnPositiveButtonClickListener { selection ->
                // This updates the button text with the chosen date
                btnPickDate.text = picker.headerText
            }
        }

        // 3. Save logic
        saveButton.setOnClickListener {
            // Add your logic to save the food item to your list/database here

            // This takes the user back to the Home Dashboard
            findNavController().popBackStack()
        }
    }
}