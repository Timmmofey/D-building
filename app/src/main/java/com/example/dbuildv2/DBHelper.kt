package com.example.dbuildv2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(val context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "dBuilding", factory, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "phone TEXT, " +
                "last_name TEXT," +
                "first_name TEXT," +
                "password TEXT)"
        db!!.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    fun addUser(user: User) {
        val values = ContentValues()
        values.put("phone", user.phone)
        values.put("last_name", user.lastName)
        values.put("first_name", user.firstName)
        values.put("password", user.password)

        val db = this.writableDatabase
        db.insert("users", null, values)

        db.close()
    }

    fun getUser(phone: String, password: String) : Int? {
        val db = this.readableDatabase

        val result = db.rawQuery(
            "SELECT id FROM users WHERE phone = '$phone' AND password = '$password'",
            null
        )

        var userId: Int? = null
        if (result.moveToFirst()) {
            val columnIndex = result.getColumnIndex("id")
            if (columnIndex >= 0) {
                userId = result.getInt(columnIndex)
            }
        }
        result.close()
        return userId
    }
}
