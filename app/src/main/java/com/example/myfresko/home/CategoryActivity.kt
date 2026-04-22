package com.example.myfresko.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfresko.R
import com.example.myfresko.common.FoodAdapter
import com.example.myfresko.model.FoodItem

// Notice how we reuse HomeContract.View!
class CategoryActivity : AppCompatActivity(), HomeContract.View {

    private lateinit var presenter: HomePresenter
    private lateinit var rvCategoryList: RecyclerView
    private lateinit var tvCategoryTitle: TextView
    private lateinit var tvEmptyCategory: TextView
    private var currentCategory: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        // Grab the category name passed from the Home screen
        currentCategory = intent.getStringExtra("CATEGORY_NAME") ?: "Inventory"

        presenter = HomePresenter(this, this)
        initViews()
    }

    private fun initViews() {
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle)
        tvEmptyCategory = findViewById(R.id.tvEmptyCategory)
        rvCategoryList = findViewById(R.id.rvCategoryList)

        // Set the header title (e.g., "Fridge" or "Pantry")
        tvCategoryTitle.text = currentCategory
        rvCategoryList.layoutManager = LinearLayoutManager(this)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    override fun displayFoodList(list: List<FoodItem>) {
        // FILTER the master list to ONLY show items for this specific category
        val filteredList = list.filter { it.category.equals(currentCategory, ignoreCase = true) }

        if (filteredList.isEmpty()) {
            showEmptyState()
        } else {
            rvCategoryList.visibility = View.VISIBLE
            tvEmptyCategory.visibility = View.GONE

            rvCategoryList.adapter = FoodAdapter(filteredList) { clickedItem ->
                val intent = Intent(this, FoodDetailActivity::class.java)
                intent.putExtra("FOOD_ITEM", clickedItem)
                startActivity(intent)
            }
        }
    }

    override fun showEmptyState() {
        rvCategoryList.visibility = View.GONE
        tvEmptyCategory.visibility = View.VISIBLE
        tvEmptyCategory.text = "Your $currentCategory is empty."
    }

    override fun onResume() {
        super.onResume()
        presenter.loadFoodItems() // Reloads data when returning from Details page
    }
}