package com.example.myfresko.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfresko.R
import com.example.myfresko.addfood.AddFoodActivity
import com.example.myfresko.data.DeletedItemsStore
import com.example.myfresko.home.HomeActivity
import com.example.myfresko.model.FoodItem

class HistoryActivity : AppCompatActivity(), HistoryContract.View {

    private lateinit var presenter: HistoryPresenter
    private lateinit var adapter: HistoryAdapter
    private lateinit var rvHistory: RecyclerView
    private lateinit var tvSubtitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        presenter = HistoryPresenter(this, this)
        rvHistory  = findViewById(R.id.rvHistoryList)
        tvSubtitle = findViewById(R.id.tvHistorySubtitle)
        rvHistory.layoutManager = LinearLayoutManager(this)

        findViewById<View>(R.id.btnNavHome).setOnClickListener {
            startActivity(
                Intent(this, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
            )
            finish()
        }
        findViewById<View>(R.id.btnNavHistory).setOnClickListener { /* already here */ }
        
        findViewById<View>(R.id.btnNavInsights).setOnClickListener {
            startActivity(Intent(this, com.example.myfresko.home.InsightsActivity::class.java))
            finish()
        }
        
        findViewById<View>(R.id.btnAddFood).setOnClickListener {
            startActivity(Intent(this, AddFoodActivity::class.java))
        }

        presenter.loadHistory()
    }

    override fun displayHistory(list: List<FoodItem>) {
        // ── Build history list ────────────────────────────
        // We only show items from the in-memory store (deleted or consumed)
        val combined = DeletedItemsStore.getAll().toMutableList()

        val emptyView = findViewById<View>(R.id.layoutHistoryEmpty)

        if (combined.isEmpty()) {
            rvHistory.visibility  = View.GONE
            emptyView.visibility  = View.VISIBLE
            tvSubtitle.text       = "Consumed & deleted items"
            return
        }

        tvSubtitle.text      = "${combined.size} item(s) logged"
        rvHistory.visibility = View.VISIBLE
        emptyView.visibility = View.GONE

        adapter = HistoryAdapter(
            items = combined,
            onPermanentDelete = { item, position ->
                // Permanent delete: remove from in-memory store (DB already wiped on soft-delete)
                DeletedItemsStore.remove(item.id)
                adapter.removeAt(position)

                if (adapter.itemCount == 0) {
                    rvHistory.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                    tvSubtitle.text = "Consumed & deleted items"
                } else {
                    tvSubtitle.text = "${adapter.itemCount} item(s) logged"
                }
            },
            onRestore = { item, position ->
                // Restore item
                DeletedItemsStore.remove(item.id)
                val db = com.example.myfresko.data.DatabaseHelper(this)
                db.addFood(item)
                
                adapter.removeAt(position)
                
                if (adapter.itemCount == 0) {
                    rvHistory.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                    tvSubtitle.text = "Consumed & deleted items"
                } else {
                    tvSubtitle.text = "${adapter.itemCount} item(s) logged"
                }
                
                com.example.myfresko.common.FreskoToast.success(this, "${item.name} restored to active inventory")
            }
        )
        rvHistory.adapter = adapter
    }
}