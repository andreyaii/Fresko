package com.example.myfresko.data

import com.example.myfresko.model.FoodItem

/**
 * In-memory store for soft-deleted items.
 * Acts as a frontend-only workaround until the DB layer is updated.
 * Survives for the lifetime of the app process.
 */
object DeletedItemsStore {
    private val deletedItems = mutableListOf<FoodItem>()

    fun add(item: FoodItem, status: String = "deleted") {
        // Avoid duplicates — replace if same ID already exists
        deletedItems.removeAll { it.id == item.id }
        deletedItems.add(item.copy(status = status))
    }

    fun getAll(): List<FoodItem> = deletedItems.toList()

    fun remove(id: Int) {
        deletedItems.removeAll { it.id == id }
    }
}