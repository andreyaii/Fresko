package com.example.myfresko.model

import java.io.Serializable

data class FoodItem(
    val id: Int = 0,
    val name: String,
    val expiryDate: String,
    val date: String,
    val category: String = "General",
    val status: String = "active"
) : Serializable