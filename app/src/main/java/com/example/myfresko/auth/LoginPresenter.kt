package com.example.myfresko.auth

import android.content.Context
import android.content.SharedPreferences

class LoginPresenter(
    private val view: LoginContract.View,
    private val context: Context
) : LoginContract.Presenter {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("fresko_users", Context.MODE_PRIVATE)

    // Changed parameter name from email to username for clarity
    override fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            view.onLoginError("Please fill in all fields.")
            return
        }

        // REMOVED: Email pattern validation

        view.showLoading(true)

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            view.showLoading(false)

            // Keys are now stored as users:username
            val storedHash = prefs.getString("users:${username.lowercase()}", null)
            if (storedHash == null) {
                view.onLoginError("No account found. Please register first.")
                return@postDelayed
            }
            if (storedHash != hashPassword(password)) {
                view.onLoginError("Incorrect password. Please try again.")
                return@postDelayed
            }

            // Persist the logged-in state using the username
            prefs.edit().putString("current_user", username.lowercase()).apply()
            view.onLoginSuccess()
        }, 800)
    }

    override fun goToRegister() {}

    companion object {
        fun hashPassword(password: String): String {
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            val bytes  = digest.digest(password.toByteArray(Charsets.UTF_8))
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }
}