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

    private lateinit var tvAttentionSummary: TextView
    private lateinit var tvPillExpiring: TextView
    private lateinit var tvPillFresh: TextView
    private lateinit var tvFridgeCount: TextView
    private lateinit var tvPantryCount: TextView
    private lateinit var tvFreezerCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        presenter = HomePresenter(this, this)
        initViews()
    }

    private fun initViews() {
        tvAttentionSummary = findViewById(R.id.tvAttentionSummary)
        tvPillExpiring     = findViewById(R.id.tvPillExpiring)
        tvPillFresh        = findViewById(R.id.tvPillFresh)
        tvFridgeCount      = findViewById(R.id.tvFridgeCount)
        tvPantryCount      = findViewById(R.id.tvPantryCount)
        tvFreezerCount     = findViewById(R.id.tvFreezerCount)

        rvExpiringSoon = findViewById(R.id.rvFoodList)
        rvExpiringSoon.layoutManager = LinearLayoutManager(this)

        findViewById<View>(R.id.btnAddFood).setOnClickListener {
            startActivity(Intent(this, AddFoodActivity::class.java))
        }

        findViewById<View>(R.id.btnHistory).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        findViewById<View>(R.id.cardFridge).setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java).putExtra("CATEGORY_NAME", "Fridge"))
        }
        findViewById<View>(R.id.cardPantry).setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java).putExtra("CATEGORY_NAME", "Pantry"))
        }
        findViewById<View>(R.id.cardFreezer).setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java).putExtra("CATEGORY_NAME", "Freezer"))
        }
    }

    override fun displayFoodList(list: List<FoodItem>) {
        if (list.isEmpty()) { showEmptyState(); return }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0);      set(Calendar.MILLISECOND, 0)
        }.time

        var expiringTodayCount = 0
        var attentionCount = 0
        var freshCount = 0
        val expiringSoonItems = mutableListOf<FoodItem>()

        // Per-category counts
        var fridgeCount = 0; var pantryCount = 0; var freezerCount = 0

        list.forEach { item ->
            // Count by storage location
            when (item.category.lowercase()) {
                "fridge"  -> fridgeCount++
                "pantry"  -> pantryCount++
                "freezer" -> freezerCount++
            }

            try {
                val expDate = sdf.parse(item.expiryDate)
                if (expDate != null) {
                    val daysLeft = (expDate.time - today.time) / (1000 * 60 * 60 * 24)
                    when {
                        daysLeft == 0L -> { expiringTodayCount++; attentionCount++; expiringSoonItems.add(item) }
                        daysLeft in 1..2 -> { attentionCount++; expiringSoonItems.add(item) }
                        daysLeft > 2    -> freshCount++
                        daysLeft < 0    -> { attentionCount++; expiringSoonItems.add(item) }
                    }
                }
            } catch (e: Exception) { e.printStackTrace() }
        }

        // Header pills
        tvAttentionSummary.text = "$attentionCount items need your attention"
        tvPillExpiring.text     = "● $expiringTodayCount expiring today"
        tvPillFresh.text        = "● $freshCount fresh"

        // Storage counts
        tvFridgeCount.text  = "$fridgeCount item(s)"
        tvPantryCount.text  = "$pantryCount item(s)"
        tvFreezerCount.text = "$freezerCount item(s)"

        // Expiring Soon list
        val labelView = findViewById<View>(R.id.tvLabelExpiringSoon)
        val emptyView = findViewById<View>(R.id.layoutNoExpiring)
        if (expiringSoonItems.isEmpty()) {
            rvExpiringSoon.visibility = View.GONE
            labelView?.visibility    = View.GONE
            emptyView?.visibility    = View.VISIBLE
        } else {
            rvExpiringSoon.visibility = View.VISIBLE
            labelView?.visibility    = View.VISIBLE
            emptyView?.visibility    = View.GONE
            rvExpiringSoon.adapter   = FoodAdapter(expiringSoonItems) { openDetail(it) }
        }
    }

    private fun openDetail(item: FoodItem) {
        startActivity(Intent(this, FoodDetailActivity::class.java).putExtra("FOOD_ITEM", item))
    }

    override fun showEmptyState() {
        rvExpiringSoon.visibility = View.GONE
        findViewById<View>(R.id.tvLabelExpiringSoon)?.visibility = View.GONE
        findViewById<View>(R.id.layoutNoExpiring)?.visibility    = View.VISIBLE
        tvAttentionSummary.text = "Your fridge is empty"
        tvPillExpiring.text     = "● 0 expiring"
        tvPillFresh.text        = "● 0 fresh"
        tvFridgeCount.text  = "0 items"
        tvPantryCount.text  = "0 items"
        tvFreezerCount.text = "0 items"
    }

    override fun onResume() {
        super.onResume()
        presenter.loadFoodItems()
    }
}