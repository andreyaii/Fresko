package com.example.myfresko.home

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfresko.R
import com.example.myfresko.common.FoodAdapter
import com.example.myfresko.model.FoodItem

class CategoryActivity : AppCompatActivity(), HomeContract.View {

    private lateinit var presenter: HomePresenter
    private lateinit var rvCategoryList: RecyclerView
    private lateinit var tvCategoryTitle: TextView
    private lateinit var ivCategoryIcon: ImageView // Changed from TextView
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
        ivCategoryIcon     = findViewById(R.id.ivCategoryIcon) // Matches new XML ID
        tvCategorySubtitle = findViewById(R.id.tvCategorySubtitle)
        tvEmptySubtext     = findViewById(R.id.tvEmptySubtext)
        layoutEmpty        = findViewById(R.id.tvEmptyCategory)
        rvCategoryList     = findViewById(R.id.rvCategoryList)
        rvCategoryList.layoutManager = LinearLayoutManager(this)

        tvCategoryTitle.text = currentCategory

        // Apply consistent theme based on category
        setupCategoryTheme()

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun setupCategoryTheme() {
        val categoryLower = currentCategory.lowercase()

        // 1. Set the Icon and Colors
        when (categoryLower) {
            "fridge" -> {
                ivCategoryIcon.setImageResource(R.drawable.ic_fridge)
                ivCategoryIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E8F5E9"))
                ivCategoryIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#2E7D32"))
            }
            "pantry" -> {
                ivCategoryIcon.setImageResource(R.drawable.ic_pantry)
                ivCategoryIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFF3E0"))
                ivCategoryIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#E65100"))
            }
            "freezer" -> {
                ivCategoryIcon.setImageResource(R.drawable.ic_freezer)
                ivCategoryIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E3F2FD"))
                ivCategoryIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#1976D2"))
            }
            else -> {
                ivCategoryIcon.setImageResource(R.drawable.ic_home)
                ivCategoryIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F5F5F5"))
                ivCategoryIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#616161"))
            }
        }

        // 2. Set the Empty State Text
        tvEmptySubtext.text = when (categoryLower) {
            "fridge"  -> "Your fridge is empty. Add some fresh produce!"
            "pantry"  -> "Nothing on the shelf yet. Stock up on pantry staples!"
            "freezer" -> "Your freezer is bare. Time to freeze something!"
            else      -> "No items found in this location."
        }
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