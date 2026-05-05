package com.example.myfresko.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myfresko.R
import com.example.myfresko.home.HomeActivity

class LoginActivity : AppCompatActivity(), LoginContract.View {

    private lateinit var presenter: LoginPresenter

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvGoRegister: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        presenter = LoginPresenter(this, this)

        etEmail     = findViewById(R.id.etLoginEmail)
        etPassword  = findViewById(R.id.etLoginPassword)
        btnLogin    = findViewById(R.id.btnLogin)
        tvGoRegister = findViewById(R.id.tvGoToRegister)
        progressBar = findViewById(R.id.loginProgressBar)
        tvError     = findViewById(R.id.tvLoginError)

        btnLogin.setOnClickListener {
            tvError.visibility = View.GONE
            presenter.login(
                etEmail.text.toString().trim(),
                etPassword.text.toString()
            )
        }

        tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    // ── LoginContract.View ─────────────────────────────────────────

    override fun onLoginSuccess() {
        startActivity(
            Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }

    override fun onLoginError(message: String) {
        tvError.text       = message
        tvError.visibility = View.VISIBLE
    }

    override fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnLogin.isEnabled     = !show
    }
}