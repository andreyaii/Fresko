package com.example.myfresko.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfresko.R
import com.example.myfresko.addfood.AddFoodActivity
import com.example.myfresko.common.FoodAdapter
import com.example.myfresko.history.HistoryActivity
import com.example.myfresko.model.FoodItem

class HomeActivity : AppCompatActivity(), HomeContract.View {

    private lateinit var presenter: HomePresenter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        presenter = HomePresenter(this, this)
        initViews()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.rvFoodList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val btnAddFood = findViewById<Button>(R.id.btnAddFood)
        val btnViewHistory = findViewById<Button>(R.id.btnHistory)

        btnAddFood.setOnClickListener {
            val intent = Intent(this, AddFoodActivity::class.java)
            startActivity(intent)
        }

        btnViewHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun displayFoodList(list: List<FoodItem>) {
        recyclerView.visibility = View.VISIBLE

        // Change the click listener to launch an Intent!
        val adapter = FoodAdapter(list) { clickedItem ->
            val intent = Intent(this, FoodDetailActivity::class.java)
            intent.putExtra("FOOD_ITEM", clickedItem)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    private fun showEditDeleteDialog(item: FoodItem) {
        // Calculate days left
        var daysLeftText = ""
        try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            // Zero-out the hours/minutes for accurate day math
            val today = sdf.parse(sdf.format(java.util.Date()))
            val expDate = sdf.parse(item.expiryDate)

            if (today != null && expDate != null) {
                val diffInMillies = expDate.time - today.time
                val daysBetween = diffInMillies / (1000 * 60 * 60 * 24)

                daysLeftText = when {
                    daysBetween > 0 -> "Expires in $daysBetween day(s)"
                    daysBetween == 0L -> "Expires TODAY!"
                    else -> "Expired ${-daysBetween} day(s) ago"
                }
            }
        } catch (e: Exception) {
            daysLeftText = "Exp: ${item.expiryDate}" // Fallback
        }

        // Build the description message
        val detailsMessage = """
            Category: ${item.category}
            Logged on: ${item.date}
            
            $daysLeftText (${item.expiryDate})
        """.trimIndent()

        // Create the upgraded popup
        android.app.AlertDialog.Builder(this)
            .setTitle(item.name)
            .setMessage(detailsMessage)
            .setPositiveButton("Edit") { _, _ ->
                val intent = Intent(this, AddFoodActivity::class.java)
                intent.putExtra("EDIT_FOOD", item)
                startActivity(intent)
            }
            .setNegativeButton("Delete") { _, _ ->
                presenter.deleteFood(item.id)
                Toast.makeText(this, "${item.name} deleted", Toast.LENGTH_SHORT).show()
                presenter.loadFoodItems()
            }
            .setNeutralButton("Close", null) // Lets them tap away without doing anything
            .show()
    }

    override fun showEmptyState() {
        recyclerView.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        presenter.loadFoodItems()
    }
}