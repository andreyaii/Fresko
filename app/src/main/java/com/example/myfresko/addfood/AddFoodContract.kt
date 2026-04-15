package com.example.myfresko.addfood

interface AddFoodContract {
    interface View {
        fun onSaveSuccess()
        fun onSaveError(message: String)
    }

    interface Presenter {
        fun validateAndSave(name: String, calories: String)
    }
}