package com.example.myfresko.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.myfresko.R
import com.example.myfresko.common.FoodAdapter
import com.example.myfresko.home.FoodDetailActivity
import com.example.myfresko.model.FoodItem

class HistoryActivity : AppCompatActivity(), HistoryContract.View {

    private lateinit var presenter: HistoryPresenter
    private lateinit var rvHistory: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        presenter = HistoryPresenter(this, this)

        // Initialization of rvHistory
        rvHistory = findViewById(R.id.rvHistoryList)
        rvHistory.layoutManager = LinearLayoutManager(this)

    }

    override fun onResume() {
        super.onResume()
        // Refresh the history list automatically when coming back from the Details page
        presenter.loadHistory()
    }

    override fun displayHistory(list: List<FoodItem>) {
        rvHistory.visibility = View.VISIBLE

        // Change the click listener to launch an Intent to the new Details Screen
        val adapter = FoodAdapter(list) { clickedItem ->
            val intent = Intent(this, FoodDetailActivity::class.java)
            intent.putExtra("FOOD_ITEM", clickedItem)
            startActivity(intent)
        }
        rvHistory.adapter = adapter
    }
}