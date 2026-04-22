package com.example.myfresko.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfresko.R
import com.example.myfresko.home.HomeActivity
import com.example.myfresko.model.FoodItem

class HistoryActivity : AppCompatActivity(), HistoryContract.View {

    private lateinit var presenter: HistoryPresenter
    private lateinit var adapter: HistoryAdapter
    private lateinit var rvHistory: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        presenter = HistoryPresenter(this, this)
        rvHistory = findViewById(R.id.rvHistoryList)
        rvHistory.layoutManager = LinearLayoutManager(this)

        // Bottom nav
        findViewById<View>(R.id.btnNavHome).setOnClickListener {
            // Navigate back to Home, clearing the back stack so Home is fresh
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
        // History tab is already active — tapping it again does nothing
        findViewById<View>(R.id.btnNavHistory).setOnClickListener { /* already here */ }

        presenter.loadHistory()
    }

    override fun displayHistory(list: List<FoodItem>) {
        val emptyView = findViewById<View>(R.id.layoutHistoryEmpty)

        if (list.isEmpty()) {
            rvHistory.visibility  = View.GONE
            emptyView.visibility  = View.VISIBLE
            return
        }

        rvHistory.visibility = View.VISIBLE
        emptyView.visibility = View.GONE

        adapter = HistoryAdapter(list.toMutableList()) { item, position ->
            presenter.deleteFood(item.id)
            adapter.removeAt(position)

            // Show empty state if the last item was just deleted
            if (adapter.itemCount == 0) {
                rvHistory.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            }
        }
        rvHistory.adapter = adapter
    }
}