package com.example.myfresko.auth

interface RegisterContract {

    interface View {
        fun onRegisterSuccess()
        fun onRegisterError(message: String)
        fun showLoading(show: Boolean)
    }

    interface Presenter {
        fun register(email: String, password: String, confirmPassword: String)
        fun goToLogin()
    }
}