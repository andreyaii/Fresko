package com.example.myfresko.auth

interface LoginContract {

    interface View {
        fun onLoginSuccess()
        fun onLoginError(message: String)
        fun showLoading(show: Boolean)
    }

    interface Presenter {
        fun login(email: String, password: String)
        fun goToRegister()
    }
}