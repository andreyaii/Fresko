package com.example.myfresko.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
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

        // Setup the partial clickable text
        setupRegisterText()
    }

    private fun setupRegisterText() {
        val fullText = "Don't have an account? Register"
        val spannableString = SpannableString(fullText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                // This is the color for the "Register" part only
                ds.color = Color.parseColor("#0B6646")
                ds.isUnderlineText = false
                ds.isFakeBoldText = true // Makes only "Register" bold
            }
        }

        val start = fullText.indexOf("Register")
        val end = fullText.length

        // Apply the span to "Register"
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tvGoRegister.text = spannableString
        tvGoRegister.movementMethod = LinkMovementMethod.getInstance()
        tvGoRegister.highlightColor = Color.TRANSPARENT
    }

    // ... rest of your override methods (onLoginSuccess, etc.)
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