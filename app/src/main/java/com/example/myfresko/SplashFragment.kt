package com.example.myfresko

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val handler = Handler(Looper.getMainLooper())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val brandContainer = view.findViewById<View>(R.id.brandContainer)
        val dotsContainer  = view.findViewById<View>(R.id.dotsContainer)
        val dot1 = view.findViewById<View>(R.id.dot1)
        val dot2 = view.findViewById<View>(R.id.dot2)
        val dot3 = view.findViewById<View>(R.id.dot3)

        // 1. Fade + slide brand up on enter
        brandContainer.translationY = 32f
        brandContainer.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // 2. Fade dots in slightly after
        handler.postDelayed({
            dotsContainer.animate().alpha(1f).setDuration(300).start()
        }, 400)

        // 3. Animate dots sequentially to suggest loading
        animateDots(dot1, dot2, dot3)

        // 4. Navigate to home
        handler.postDelayed({
            if (isAdded) findNavController().navigate(R.id.action_splash_to_home)
        }, 2800)
    }

    private fun animateDots(dot1: View, dot2: View, dot3: View) {
        val dots = listOf(dot1, dot2, dot3)
        var index = 0

        val runnable = object : Runnable {
            override fun run() {
                dots.forEach { it.alpha = 0.3f }
                dots[index % 3].animate().alpha(1f).setDuration(200).start()
                index++
                handler.postDelayed(this, 500)
            }
        }
        handler.postDelayed(runnable, 600)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }
}