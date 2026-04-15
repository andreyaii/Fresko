package com.example.myfresko.addfood

import android.content.Context
import com.example.myfresko.data.DatabaseHelper
import com.example.myfresko.model.FoodItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddFoodPresenter(
    private val view: AddFoodContract.View,
    private val context: Context
) : AddFoodContract.Presenter {

    private val db = DatabaseHelper(context)

    // CHANGED: Accepts expiryDate instead of calories
    override fun validateAndSave(name: String, expiryDate: String) {
        if (name.isBlank() || expiryDate.isBlank()) {
            view.onSaveError("Please fill in all fields!")
            return
        }

        // Generate the exact date the user added the food
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Create the item using the updated model
        val newItem = FoodItem(
            name = name,
            expiryDate = expiryDate,
            date = currentDate
        )

        val success = db.addFood(newItem)

        if (success) {
            view.onSaveSuccess()
        } else {
            view.onSaveError("Failed to save to database.")
        }
    }
}