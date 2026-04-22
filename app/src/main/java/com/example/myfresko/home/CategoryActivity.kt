package com.example.myfresko.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfresko.R
import com.example.myfresko.common.FoodAdapter
import com.example.myfresko.model.FoodItem

class CategoryActivity : AppCompatActivity(), HomeContract.View {

    private lateinit var presenter: HomePresenter
    private lateinit var rvCategoryList: RecyclerView
    private lateinit var tvCategoryTitle: TextView
    private lateinit var tvCategoryEmoji: TextView
    private lateinit var tvCategorySubtitle: TextView
    private lateinit var tvEmptySubtext: TextView
    private lateinit var layoutEmpty: LinearLayout
    private var currentCategory: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        currentCategory = intent.getStringExtra("CATEGORY_NAME") ?: "Inventory"
        presenter = HomePresenter(this, this)
        initViews()
    }

    private fun initViews() {
        tvCategoryTitle    = findViewById(R.id.tvCategoryTitle)
        tvCategoryEmoji    = findViewById(R.id.tvCategoryEmoji)
        tvCategorySubtitle = findViewById(R.id.tvCategorySubtitle)
        tvEmptySubtext     = findViewById(R.id.tvEmptySubtext)
        layoutEmpty        = findViewById(R.id.tvEmptyCategory)
        rvCategoryList     = findViewById(R.id.rvCategoryList)
        rvCategoryList.layoutManager = LinearLayoutManager(this)

        tvCategoryTitle.text = currentCategory

        // Emoji stays dynamic per category but header is always green
        tvCategoryEmoji.text = when (currentCategory.lowercase()) {
            "fridge"  -> "🥦"
            "pantry"  -> "🥫"
            "freezer" -> "❄️"
            else      -> "🍽️"
        }

        tvEmptySubtext.text = when (currentCategory.lowercase()) {
            "fridge"  -> "Your fridge is empty. Add some fresh produce!"
            "pantry"  -> "Nothing on the shelf yet. Stock up on pantry staples!"
            "freezer" -> "Your freezer is bare. Time to freeze something!"
            else      -> "No items found in this location."
        }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }

    override fun displayFoodList(list: List<FoodItem>) {
        val filtered = list.filter {
            it.category.equals(currentCategory, ignoreCase = true) && it.status == "active"
        }

        tvCategorySubtitle.text = "${filtered.size} item(s) stored"

        if (filtered.isEmpty()) {
            showEmptyState()
        } else {
            rvCategoryList.visibility = View.VISIBLE
            layoutEmpty.visibility    = View.GONE
            rvCategoryList.adapter    = FoodAdapter(filtered) { clickedItem ->
                startActivity(
                    Intent(this, FoodDetailActivity::class.java)
                        .putExtra("FOOD_ITEM", clickedItem)
                )
            }
        }
    }

    override fun showEmptyState() {
        rvCategoryList.visibility = View.GONE
        layoutEmpty.visibility    = View.VISIBLE
        tvCategorySubtitle.text   = "0 items stored"
    }

    override fun onResume() {
        super.onResume()
        presenter.loadFoodItems()
    }
}