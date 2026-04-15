package com.example.myfresko.home

import com.example.myfresko.model.FoodItem

interface HomeContract {
    interface View {
        fun displayFoodList(list: List<FoodItem>)
        fun showEmptyState()
    }

    interface Presenter {
        fun loadFoodItems()
        fun deleteFood(id: Int) // Add this
    }
}