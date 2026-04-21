package com.example.myfresko.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfresko.R
import com.example.myfresko.addfood.AddFoodActivity
import com.example.myfresko.common.FoodAdapter
import com.example.myfresko.history.HistoryActivity
import com.example.myfresko.model.FoodItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeActivity : AppCompatActivity(), HomeContract.View {

    private lateinit var presenter: HomePresenter
    private lateinit var rvExpiringSoon: RecyclerView
    private lateinit var rvFridge: RecyclerView

    private lateinit var tvAttentionSummary: TextView
    private lateinit var tvPillExpiring: TextView
    private lateinit var tvPillFresh: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        presenter = HomePresenter(this, this)
        initViews()
    }

    private fun initViews() {
        // Headers
        tvAttentionSummary = findViewById(R.id.tvAttentionSummary)
        tvPillExpiring = findViewById(R.id.tvPillExpiring)
        tvPillFresh = findViewById(R.id.tvPillFresh)

        // RecyclerViews
        rvExpiringSoon = findViewById(R.id.rvFoodList)
        rvFridge = findViewById(R.id.rvFridgeList)

        rvExpiringSoon.layoutManager = LinearLayoutManager(this)
        rvFridge.layoutManager = LinearLayoutManager(this)

        // Buttons
        findViewById<View>(R.id.btnAddFood).setOnClickListener {
            startActivity(Intent(this, AddFoodActivity::class.java))
        }

        findViewById<View>(R.id.btnHistory).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }

    override fun displayFoodList(list: List<FoodItem>) {
        if (list.isEmpty()) {
            showEmptyState()
            return
        }

        rvExpiringSoon.visibility = View.VISIBLE
        rvFridge.visibility = View.VISIBLE

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        var expiringTodayCount = 0
        var attentionCount = 0 // Expiring within 2 days
        var freshCount = 0

        val expiringSoonItems = mutableListOf<FoodItem>()

        // Process the full list
        list.forEach { item ->
            try {
                val expDate = sdf.parse(item.expiryDate)
                if (expDate != null) {
                    val diff = expDate.time - today.time
                    val daysLeft = diff / (1000 * 60 * 60 * 24)

                    when {
                        daysLeft == 0L -> {
                            expiringTodayCount++
                            attentionCount++
                            expiringSoonItems.add(item)
                        }
                        daysLeft in 1..2 -> {
                            attentionCount++
                            expiringSoonItems.add(item)
                        }
                        daysLeft > 2 -> {
                            freshCount++
                        }
                        daysLeft < 0 -> {
                            // Optional: handle already expired items
                            attentionCount++
                            expiringSoonItems.add(item)
                        }
                    }
                }
            } catch (e: Exception) { e.printStackTrace() }
        }

        // Update Header UI
        tvAttentionSummary.text = "$attentionCount items need your attention"
        tvPillExpiring.text = "● $expiringTodayCount expiring today"
        tvPillFresh.text = "● $freshCount fresh"

        // Set Adapter for "Expiring Soon" Section
        rvExpiringSoon.adapter = FoodAdapter(expiringSoonItems) { clickedItem ->
            openDetail(clickedItem)
        }

        // Set Adapter for "Fridge" Section (All Items)
        rvFridge.adapter = FoodAdapter(list) { clickedItem ->
            openDetail(clickedItem)
        }

        // Hide "Expiring soon" label if no items are expiring
        findViewById<View>(R.id.labelExpiring).visibility = if (expiringSoonItems.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun openDetail(item: FoodItem) {
        val intent = Intent(this, FoodDetailActivity::class.java)
        intent.putExtra("FOOD_ITEM", item)
        startActivity(intent)
    }

    override fun showEmptyState() {
        rvExpiringSoon.visibility = View.GONE
        rvFridge.visibility = View.GONE
        tvAttentionSummary.text = "Your fridge is empty"
        tvPillExpiring.text = "● 0 expiring"
        tvPillFresh.text = "● 0 fresh"
    }

    override fun onResume() {
        super.onResume()
        presenter.loadFoodItems()
    }
}