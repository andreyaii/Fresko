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

    override fun validateAndSave(name: String, expiryDate: String, category: String, editId: Int) {
        if (name.isBlank() || expiryDate.isBlank()) {
            view.onSaveError("Please fill in all fields!")
            return
        }

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (editId != -1) {
            // --- EDIT MODE: update the existing row ---
            val updatedItem = FoodItem(
                id = editId,
                name = name,
                expiryDate = expiryDate,
                date = currentDate,
                category = category
            )
            val success = db.updateFood(updatedItem)
            if (success) view.onSaveSuccess() else view.onSaveError("Failed to update item.")
        } else {
            // --- ADD MODE: insert a new row ---
            val newItem = FoodItem(
                name = name,
                expiryDate = expiryDate,
                date = currentDate,
                category = category
            )
            val success = db.addFood(newItem)
            if (success) view.onSaveSuccess() else view.onSaveError("Failed to save to database.")
        }
    }
}