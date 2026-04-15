package com.example.myfresko

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Wait 2 seconds (2000 milliseconds) then move to Home
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.action_splash_to_home)
        }, 3000)
    }
}