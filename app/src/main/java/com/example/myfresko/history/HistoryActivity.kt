package com.example.myfresko.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfresko.R
import com.example.myfresko.data.DeletedItemsStore
import com.example.myfresko.home.HomeActivity
import com.example.myfresko.model.FoodItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

        presenter.loadHistory()
    }

    override fun displayHistory(list: List<FoodItem>) {
        // ── Build combined history list ────────────────────────────
        // 1. Expired items from DB (status = "active" but date has passed)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = sdf.format(Date())
        val today = sdf.parse(todayStr)

        val expiredFromDb = list.filter { item ->
            if (item.status != "active") return@filter false
            try {
                val exp = sdf.parse(item.expiryDate)
                exp != null && today != null && exp.before(today)
            } catch (e: Exception) { false }
        }

        // 2. Soft-deleted items from the in-memory store
        val deletedItems = DeletedItemsStore.getAll()

        // 3. Merge: deleted first (most recently actioned), then expired
        val combined = (deletedItems + expiredFromDb).toMutableList()

        val emptyView = findViewById<View>(R.id.layoutHistoryEmpty)

        if (combined.isEmpty()) {
            rvHistory.visibility  = View.GONE
            emptyView.visibility  = View.VISIBLE
            tvSubtitle.text       = "Expired & deleted items"
            return
        }

        tvSubtitle.text      = "${combined.size} item(s) logged"
        rvHistory.visibility = View.VISIBLE
        emptyView.visibility = View.GONE

        adapter = HistoryAdapter(combined) { item, position ->
            // Permanent delete: remove from in-memory store (DB already wiped on soft-delete)
            DeletedItemsStore.remove(item.id)
            adapter.removeAt(position)

            if (adapter.itemCount == 0) {
                rvHistory.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
                tvSubtitle.text = "Expired & deleted items"
            } else {
                tvSubtitle.text = "${adapter.itemCount} item(s) logged"
            }
        }
        rvHistory.adapter = adapter
    }
}