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

class RegisterActivity : AppCompatActivity(), RegisterContract.View {

    private lateinit var presenter: RegisterPresenter

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvGoLogin: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        presenter = RegisterPresenter(this, this)

        etEmail           = findViewById(R.id.etRegisterEmail)
        etPassword        = findViewById(R.id.etRegisterPassword)
        etConfirmPassword = findViewById(R.id.etRegisterConfirmPassword)
        btnRegister       = findViewById(R.id.btnRegister)
        tvGoLogin         = findViewById(R.id.tvGoToLogin)
        progressBar       = findViewById(R.id.registerProgressBar)
        tvError           = findViewById(R.id.tvRegisterError)

        btnRegister.setOnClickListener {
            tvError.visibility = View.GONE
            presenter.register(
                etEmail.text.toString().trim(),
                etPassword.text.toString(),
                etConfirmPassword.text.toString()
            )
        }

        tvGoLogin.setOnClickListener {
            finish() // pops back to LoginActivity
        }
    }

    // ── RegisterContract.View ──────────────────────────────────────

    override fun onRegisterSuccess() {
        // Go back to Login so the user can sign in with their new account
        startActivity(
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("REGISTER_SUCCESS", true)
            }
        )
        finish()
    }

    override fun onRegisterError(message: String) {
        tvError.text       = message
        tvError.visibility = View.VISIBLE
    }

    override fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnRegister.isEnabled  = !show
    }
}