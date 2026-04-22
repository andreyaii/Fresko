package com.example.myfresko.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myfresko.model.FoodItem

// Change version to 3 at the top
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "Fresko.db", null, 3) {

    override fun onCreate(db: SQLiteDatabase?) {
        // Added 'category TEXT' to the end
        val createTable = "CREATE TABLE Food (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, expiryDate TEXT, dateAdded TEXT, category TEXT)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS Food")
        onCreate(db)
    }

    fun addFood(food: FoodItem): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put("name", food.name)
        cv.put("expiryDate", food.expiryDate)
        cv.put("dateAdded", food.date)
        cv.put("category", food.category) // Tell the DB to save the category!
        return db.insert("Food", null, cv) != -1L


    }

    fun getAllFood(): List<FoodItem> {
        val list = mutableListOf<FoodItem>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Food", null)

        if (cursor.moveToFirst()) {
            do {
                list.add(FoodItem(
                id = cursor.getInt(0),
                name = cursor.getString(1),
                expiryDate = cursor.getString(2),
                date = cursor.getString(3),
                category = cursor.getString(4) // Tell the DB to read the category!
            ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun deleteFood(id: Int) {
        val db = this.writableDatabase
        db.delete("Food", "id=?", arrayOf(id.toString()))
        db.close()
    }
    fun updateFood(food: FoodItem): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put("name", food.name)
        cv.put("expiryDate", food.expiryDate)
        cv.put("category", food.category)          // ← persist category on edit too
        val result = db.update("Food", cv, "id=?", arrayOf(food.id.toString()))
        return result > 0
    }
}