package com.example.myfresko

data class FoodItem(
    val id: Int,
    val name: String,
    val expiryDate: String,
    val category: String,
    val location: String,
    val daysLeft: Int
)

object MockData {
    val foodList = mutableListOf(
        FoodItem(1, "Milk", "Mar 11", "Dairy", "Fridge", 2),
        FoodItem(2, "Strawberries", "Mar 11", "Fruit", "Fridge", 2),
        FoodItem(3, "Bread", "Mar 10", "Pantry", "Pantry", 1),
        FoodItem(4, "Eggs", "Mar 20", "Dairy", "Fridge", 11),
        FoodItem(5, "Spinach", "Mar 12", "Vegetable", "Fridge", 3)
    )
}