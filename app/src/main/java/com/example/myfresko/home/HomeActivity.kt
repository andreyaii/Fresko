package com.example.myfresko.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.progressindicator.LinearProgressIndicator
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfresko.R
import com.example.myfresko.addfood.AddFoodActivity
import com.example.myfresko.auth.LoginActivity
import com.example.myfresko.common.FoodAdapter
import com.example.myfresko.data.DeletedItemsStore
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

    private lateinit var etSearch: EditText
    private var allActiveItems: List<FoodItem> = emptyList()

    private lateinit var tvStatTotal: TextView
    private lateinit var tvStatConsumed: TextView
    private lateinit var tvStatExpired: TextView
    private lateinit var tvStatWaste: TextView

    private lateinit var pbFridge: LinearProgressIndicator
    private lateinit var tvFridgeFreshLabel: TextView
    private lateinit var pbPantry: LinearProgressIndicator
    private lateinit var tvPantryFreshLabel: TextView
    private lateinit var pbFreezer: LinearProgressIndicator
    private lateinit var tvFreezerFreshLabel: TextView

    private var tvLabelExpiringSoon: TextView? = null
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

        etSearch = findViewById(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterItems(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        tvStatTotal = findViewById(R.id.tvStatTotal)
        tvStatConsumed = findViewById(R.id.tvStatConsumed)
        tvStatExpired = findViewById(R.id.tvStatExpired)
        tvStatWaste = findViewById(R.id.tvStatWaste)

        findViewById<View>(R.id.btnLogout).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

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

    private fun filterItems(query: String) {
        if (allActiveItems.isEmpty()) return
        
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0);      set(Calendar.MILLISECOND, 0)
        }.time

        val isSearching = query.isNotBlank()
        
        val filtered = if (!isSearching) {
            tvLabelExpiringSoon?.text = "EXPIRING SOON"
            // If not searching, just show expiring soon (<= 2 days)
            allActiveItems.filter {
                try {
                    val expDate = sdf.parse(it.expiryDate)
                    expDate != null && (expDate.time - today.time) / (1000L * 60 * 60 * 24) <= 2
                } catch (e: Exception) { false }
            }
        } else {
            tvLabelExpiringSoon?.text = "SEARCH RESULTS"
            allActiveItems.filter { it.name.contains(query, ignoreCase = true) }
        }
        rvExpiringSoon.adapter = FoodAdapter(filtered) { openDetail(it) }
        
        if (filtered.isEmpty() && isSearching) {
            layoutNoExpiring?.visibility = View.VISIBLE
            layoutNoExpiring?.findViewById<TextView>(R.id.tvNoExpiringText)?.text = "No results found for '$query'" // Assuming there's an ID, or I'll just let it be empty state
        } else {
            layoutNoExpiring?.visibility = View.GONE
        }
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

        // Compute Simple Stats
        allActiveItems = activeList.toList()
        tvStatTotal.text = activeList.size.toString()
        
        // Items expired this month: active items that are expired and in current month/year
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        
        var expiredThisMonthCount = 0
        activeList.forEach {
            try {
                val expDate = sdf.parse(it.expiryDate)
                if (expDate != null) {
                    val cal = Calendar.getInstance().apply { time = expDate }
                    if (cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear) {
                        if ((expDate.time - today.time) / (1000L * 60 * 60 * 24) < 0) {
                            expiredThisMonthCount++
                        }
                    }
                }
            } catch (e: Exception) {}
        }
        tvStatExpired.text = expiredThisMonthCount.toString()
        
        // Consumed and Wasted
        var consumedCount = 0
        var wasteCount = 0
        DeletedItemsStore.getAll().forEach {
            try {
                val expDate = sdf.parse(it.expiryDate)
                if (expDate != null) {
                    val daysLeft = (expDate.time - today.time) / (1000L * 60 * 60 * 24)
                    if (daysLeft >= 0) consumedCount++ else wasteCount++
                }
            } catch(e: Exception) {}
        }
        tvStatConsumed.text = consumedCount.toString()
        tvStatWaste.text = wasteCount.toString()

        layoutTotallyEmpty?.visibility = View.GONE
        rvExpiringSoon.visibility = View.VISIBLE
        tvLabelExpiringSoon?.visibility = View.VISIBLE
        findViewById<View>(R.id.cardSearch)?.visibility = View.VISIBLE
        layoutNoExpiring?.visibility = View.GONE
        
        // Default filter to current search text
        filterItems(etSearch.text.toString())
    }

    private fun updateProgress(bar: LinearProgressIndicator, label: TextView, fresh: Int, total: Int) {
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
        findViewById<View>(R.id.cardSearch)?.visibility = View.GONE
        layoutNoExpiring?.visibility = View.GONE
        layoutTotallyEmpty?.visibility = View.VISIBLE
        tvAttentionSummary.text = "Your inventory is empty"
    }

    override fun onResume() {
        super.onResume()
        presenter.loadFoodItems()
    }
}