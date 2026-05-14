package com.example.myfresko.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
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
    private lateinit var tvFridgeCount: TextView
    private lateinit var tvPantryCount: TextView
    private lateinit var tvFreezerCount: TextView

    private lateinit var pbFridge: ProgressBar
    private lateinit var tvFridgeFreshLabel: TextView
    private lateinit var pbPantry: ProgressBar
    private lateinit var tvPantryFreshLabel: TextView
    private lateinit var pbFreezer: ProgressBar
    private lateinit var tvFreezerFreshLabel: TextView

    private var tvLabelExpiringSoon: View? = null
    private var layoutNoExpiring: View? = null
    private var layoutTotallyEmpty: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        presenter = HomePresenter(this, this)
        initViews()
    }

    private fun initViews() {
        tvAttentionSummary = findViewById(R.id.tvAttentionSummary)
        tvFridgeCount      = findViewById(R.id.tvFridgeCount)
        tvPantryCount      = findViewById(R.id.tvPantryCount)
        tvFreezerCount     = findViewById(R.id.tvFreezerCount)

        pbFridge = findViewById(R.id.pbFridgeFreshness)
        tvFridgeFreshLabel = findViewById(R.id.tvFridgeFreshLabel)
        pbPantry = findViewById(R.id.pbPantryFreshness)
        tvPantryFreshLabel = findViewById(R.id.tvPantryFreshLabel)
        pbFreezer = findViewById(R.id.pbFreezerFreshness)
        tvFreezerFreshLabel = findViewById(R.id.tvFreezerFreshLabel)

        tvLabelExpiringSoon = findViewById(R.id.tvLabelExpiringSoon)
        layoutNoExpiring = findViewById(R.id.layoutNoExpiring)
        layoutTotallyEmpty = findViewById(R.id.layoutTotallyEmpty)

        rvExpiringSoon = findViewById(R.id.rvFoodList)
        rvExpiringSoon.layoutManager = LinearLayoutManager(this)

        // Both + buttons go to AddFood
        findViewById<View>(R.id.btnAddFood).setOnClickListener {
            startActivity(Intent(this, AddFoodActivity::class.java))
        }

        findViewById<View>(R.id.btnHistory).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        findViewById<View>(R.id.cardFridge).setOnClickListener  { openCategory("Fridge") }
        findViewById<View>(R.id.cardPantry).setOnClickListener  { openCategory("Pantry") }
        findViewById<View>(R.id.cardFreezer).setOnClickListener { openCategory("Freezer") }
    }

    private fun openCategory(name: String) {
        // Assume CategoryActivity exists or replace with toast if not yet implemented
        val intent = Intent(this, CategoryActivity::class.java)
        intent.putExtra("CATEGORY_NAME", name)
        startActivity(intent)
    }

    override fun displayFoodList(list: List<FoodItem>) {
        val activeList = list.filter { it.status == "active" }

        if (activeList.isEmpty()) {
            showEmptyState()
            return
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0);      set(Calendar.MILLISECOND, 0)
        }.time

        var attentionCount = 0
        val expiringSoonItems = mutableListOf<FoodItem>()

        var fridgeTotal = 0; var fridgeFresh = 0
        var pantryTotal = 0; var pantryFresh = 0
        var freezerTotal = 0; var freezerFresh = 0

        activeList.forEach { item ->
            val cat = item.category.lowercase()
            try {
                val expDate = sdf.parse(item.expiryDate) ?: return@forEach
                val daysLeft = (expDate.time - today.time) / (1000L * 60 * 60 * 24)

                when(cat) {
                    "fridge" -> { fridgeTotal++; if (daysLeft > 2) fridgeFresh++ }
                    "pantry" -> { pantryTotal++; if (daysLeft > 2) pantryFresh++ }
                    "freezer" -> { freezerTotal++; if (daysLeft > 2) freezerFresh++ }
                }

                if (daysLeft <= 2) {
                    attentionCount++
                    expiringSoonItems.add(item)
                }
            } catch (e: Exception) { e.printStackTrace() }
        }

        tvFridgeCount.text = fridgeTotal.toString()
        tvPantryCount.text = pantryTotal.toString()
        tvFreezerCount.text = freezerTotal.toString()

        updateProgress(pbFridge, tvFridgeFreshLabel, fridgeFresh, fridgeTotal)
        updateProgress(pbPantry, tvPantryFreshLabel, pantryFresh, pantryTotal)
        updateProgress(pbFreezer, tvFreezerFreshLabel, freezerFresh, freezerTotal)

        tvAttentionSummary.text = if (attentionCount > 0)
            "$attentionCount item(s) need attention" else "Everything looks fresh 🎉"

        layoutTotallyEmpty?.visibility = View.GONE
        if (expiringSoonItems.isEmpty()) {
            rvExpiringSoon.visibility = View.GONE
            tvLabelExpiringSoon?.visibility = View.GONE
            layoutNoExpiring?.visibility = View.VISIBLE
        } else {
            rvExpiringSoon.visibility = View.VISIBLE
            tvLabelExpiringSoon?.visibility = View.VISIBLE
            layoutNoExpiring?.visibility = View.GONE
            rvExpiringSoon.adapter = FoodAdapter(expiringSoonItems) { openDetail(it) }
        }
    }

    private fun updateProgress(bar: ProgressBar, label: TextView, fresh: Int, total: Int) {
        if (total == 0) {
            bar.progress = 100
            label.text = "100% FRESH"
        } else {
            val p = (fresh.toFloat() / total.toFloat() * 100).toInt()
            bar.progress = p
            label.text = "$p% FRESH"
        }
    }

    private fun openDetail(item: FoodItem) {
        startActivity(Intent(this, FoodDetailActivity::class.java).putExtra("FOOD_ITEM", item))
    }

    override fun showEmptyState() {
        rvExpiringSoon.visibility = View.GONE
        tvLabelExpiringSoon?.visibility = View.GONE
        layoutNoExpiring?.visibility = View.GONE
        layoutTotallyEmpty?.visibility = View.VISIBLE
        tvAttentionSummary.text = "Your inventory is empty"
    }

    override fun onResume() {
        super.onResume()
        presenter.loadFoodItems()
    }
}