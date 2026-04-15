package com.example.myfresko

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        bottomNav.setupWithNavController(navController)

        // --- ADD THIS LOGIC BELOW ---
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.historyFragment -> {
                    // Show the bar on these screens
                    bottomNav.visibility = View.VISIBLE
                }
                else -> {
                    // Hide the bar on Splash, Add Food, etc.
                    bottomNav.visibility = View.GONE
                }
            }
        }
    }
}