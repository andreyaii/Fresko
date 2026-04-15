package com.example.myfresko

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup Expiring Soon List (Filtering items with < 3 days left)
        val expiringSoonItems = MockData.foodList.filter { it.daysLeft <= 2 }
        setupRecycler(view.findViewById(R.id.rvExpiringSoon), expiringSoonItems)

        // Setup Fridge List
        val fridgeItems = MockData.foodList.filter { it.location == "Fridge" }
        setupRecycler(view.findViewById(R.id.rvFridge), fridgeItems)

        // Setup Pantry List
        val pantryItems = MockData.foodList.filter { it.location == "Pantry" }
        setupRecycler(view.findViewById(R.id.rvPantry), pantryItems)

        view.findViewById<FloatingActionButton>(R.id.fabAddFood).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_addFood)
        }
    }

    private fun setupRecycler(rv: RecyclerView, items: List<FoodItem>) {
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = FoodAdapter(items)
    }
}