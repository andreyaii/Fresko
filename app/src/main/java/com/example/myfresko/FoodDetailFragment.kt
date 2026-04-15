
package com.example.myfresko

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class FoodDetailFragment : Fragment(R.layout.fragment_food_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Back button logic
        view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            findNavController().popBackStack()
        }

        // Later, you will add code here to display the specific food data
        // that the user clicked on.
    }
}