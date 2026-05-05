package com.example.myfresko.auth

import android.content.Context
import android.content.SharedPreferences

class RegisterPresenter(
    private val view: RegisterContract.View,
    private val context: Context
) : RegisterContract.Presenter {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("fresko_users", Context.MODE_PRIVATE)

    // Changed parameter from email to username
    override fun register(username: String, password: String, confirmPassword: String) {

        if (username.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            view.onRegisterError("Please fill in all fields.")
            return
        }

        // NEW: Simple username length validation
        if (username.length < 3) {
            view.onRegisterError("Username must be at least 3 characters.")
            return
        }

        if (password != confirmPassword) {
            view.onRegisterError("Passwords do not match.")
            return
        }

        // Key is now users:username
        val key = "users:${username.lowercase()}"
        if (prefs.contains(key)) {
            view.onRegisterError("This username is already taken.")
            return
        }

        view.showLoading(true)

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            view.showLoading(false)
            prefs.edit()
                .putString(key, LoginPresenter.hashPassword(password))
                .apply()
            view.onRegisterSuccess()
        }, 800)
    }

    override fun goToLogin() {}
}