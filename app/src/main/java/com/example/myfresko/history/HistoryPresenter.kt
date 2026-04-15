package com.example.myfresko.history

import android.content.Context
import com.example.myfresko.data.DatabaseHelper

class HistoryPresenter(
    private val view: HistoryContract.View,
    private val context: Context
) : HistoryContract.Presenter {

    private val db = DatabaseHelper(context)

    override fun loadHistory() {
        val list = db.getAllFood()
        view.displayHistory(list)
    }

    // Add the delete function here
    override fun deleteFood(id: Int) {
        db.deleteFood(id)
    }
}