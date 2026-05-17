package com.example.myfresko.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myfresko.R
import com.example.myfresko.addfood.AddFoodActivity
import com.example.myfresko.data.DatabaseHelper
import com.example.myfresko.data.DeletedItemsStore
import com.example.myfresko.history.HistoryActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class InsightsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insights)

        setupNavbar()
        calculateAndDisplayStats()
    }

    private fun setupNavbar() {
        findViewById<View>(R.id.btnNavHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            })
            finish()
        }

        findViewById<View>(R.id.btnNavHistory).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
            finish()
        }

        findViewById<View>(R.id.btnNavInsights).setOnClickListener {
            // Already here
        }

        findViewById<View>(R.id.btnAddFood).setOnClickListener {
            startActivity(Intent(this, AddFoodActivity::class.java))
        }
    }

    private fun calculateAndDisplayStats() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        
        val db = DatabaseHelper(this)
        val activeItems = db.getAllFood().filter { it.status == "active" }
        val deletedItems = DeletedItemsStore.getAll()

        var consumedThisMonth = 0
        var wastedThisMonth = 0

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.time

        // 1. Process items in the history store
        deletedItems.forEach {
            if (it.status == "consumed") {
                consumedThisMonth++
            } else {
                wastedThisMonth++
            }
        }
        
        // 2. Process active items for active waste (expired this month but still sitting in DB)
        var activeExpiredThisMonth = 0
        activeItems.forEach {
            try {
                val expDate = sdf.parse(it.expiryDate)
                if (expDate != null) {
                    val cal = Calendar.getInstance().apply { time = expDate }
                    if (cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear) {
                        if ((expDate.time - today.time) / (1000L * 60 * 60 * 24) < 0) {
                            activeExpiredThisMonth++
                        }
                    }
                }
            } catch (e: Exception) {}
        }
        
        val totalWasted = wastedThisMonth + activeExpiredThisMonth
        val totalConsumed = consumedThisMonth
        
        val savedCount = totalConsumed
        val trackedCount = activeItems.size + totalConsumed + totalWasted

        val wasteRate = if ((totalConsumed + totalWasted) > 0) {
            (totalWasted.toFloat() / (totalConsumed + totalWasted).toFloat() * 100).toInt()
        } else {
            0
        }

        findViewById<TextView>(R.id.tvWasteRate).text = "$wasteRate%"
        findViewById<TextView>(R.id.tvWasteSubtext).text = "$totalConsumed consumed · $totalWasted wasted"
        findViewById<TextView>(R.id.tvTrackedCount).text = trackedCount.toString()
        findViewById<TextView>(R.id.tvSavedCount).text = savedCount.toString()
    }
}
