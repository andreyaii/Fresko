package com.example.myfresko.addfood

interface AddFoodContract {
    interface View {
        fun onSaveSuccess()
        fun onSaveError(message: String)
    }

    interface Presenter {
        // expiryDate replaces the old "calories" param; editId is -1 for new items
        fun validateAndSave(name: String, expiryDate: String, category: String, editId: Int)
    }
}