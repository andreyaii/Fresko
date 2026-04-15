package com.example.myfresko.home

import android.content.Context // Don't forget this import
import com.example.myfresko.data.DatabaseHelper

class HomePresenter(
    private val view: HomeContract.View,
    private val context: Context // Add this
) : HomeContract.Presenter {

    // Now 'db' won't be red anymore
    private val db = DatabaseHelper(context)

    override fun loadFoodItems() {
        val list = db.getAllFood()
        if (list.isEmpty()) {
            view.showEmptyState()
        } else {
            view.displayFoodList(list)
        }
    }

    override fun deleteFood(id: Int) {
        db.deleteFood(id)
        loadFoodItems() // Refresh the list
    }
}