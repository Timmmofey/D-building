package com.example.dbuildv2

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class private_lobby_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private_lobby)

        val sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", -1)
        val db = DBHelper(this, null)

        val fullName: TextView = findViewById(R.id.fullName)
        fullName.text = db.getUserName(userId)
    }
}