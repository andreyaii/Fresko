package com.example.myfresko.history

import com.example.myfresko.model.FoodItem

interface HistoryContract {
    interface View {
        fun displayHistory(list: List<FoodItem>)
    }

    interface Presenter {
        fun loadHistory()
        fun deleteFood(id: Int) // Add this line!
    }
}