package com.example.dbuildv2

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class appartment_item_activity : AppCompatActivity() {
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appartment_item)

        val db = DBHelper(this, null)

        val price: TextView = findViewById(R.id.itemmore_price)
        val rooms: TextView = findViewById(R.id.itemmore_rooms)
        val square: TextView = findViewById(R.id.itemmore_square)
        val city: TextView = findViewById(R.id.itemmore_city)
        val address: TextView = findViewById(R.id.itemmore_address)

        val apartId = intent.getIntExtra("itemId", 1)

        val apartment = db.getApartmentById(apartId)
        city.text = apartment.city
    }
}