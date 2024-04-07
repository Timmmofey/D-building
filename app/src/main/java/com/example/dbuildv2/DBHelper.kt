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
                "password TEXT," +
                "balance INTEGER," +
                "photo TEXT)"
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
        values.put("balance", user.balance)
        values.put("photo", user.photo)

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

    fun getUserName(id: Int) : String {
        if (id == -1) {
            return "John Doe"
        }

        val db = this.readableDatabase

        val result = db.rawQuery(
            "SELECT last_name, first_name FROM users WHERE id = $id",
            null
        )
        result.moveToFirst()

        val fullName = result.getString(0) + " " + result.getString(1)
        result.close()
        return fullName
    }

    fun getUserPhoto(id: Int) : String {
        val db = this.readableDatabase

        val result = db.rawQuery(
            "SELECT photo FROM users WHERE id = $id",
            null
        )
        result.moveToFirst()

        val photoURL = result.getString(0)
        result.close()

        if (photoURL == "none") {
            return "https://ntrepidcorp.com/wp-content/uploads/2016/06/team-1.jpg"
        }

        return photoURL
    }
}
