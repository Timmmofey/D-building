package com.example.dbuildv2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class actvity_appartment_search : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actvity_appartment_search)

        val db = DBHelper(this, null)

        val itemsList: RecyclerView = findViewById(R.id.items_list)
        val items = db.getApartmentsFromDatabase()

        itemsList.layoutManager = LinearLayoutManager(this)
        itemsList.adapter = ItemsAdapter(items, this)
    }
}