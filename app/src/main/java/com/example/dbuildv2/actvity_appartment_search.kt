package com.example.dbuildv2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class actvity_appartment_search : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actvity_appartment_search)

        val db = DBHelper(this, null)

        val inputMinPrice: EditText = findViewById(R.id.search_pricemin)
        val inputMaxPrice: EditText = findViewById(R.id.search_pricemax)
        val inputMinRooms: EditText = findViewById(R.id.search_roomsmin)
        val inputMaxRooms: EditText = findViewById(R.id.search_roomsmax)
        val inputMinSquare: EditText = findViewById(R.id.search_squaremin)
        val inputMaxSquare: EditText = findViewById(R.id.search_squaremax)
        val inputCity: Spinner = findViewById(R.id.search_city)
        val findButton: Button = findViewById(R.id.search_findbtn)

        val itemsList: RecyclerView = findViewById(R.id.items_list)
        val items = db.getApartmentsFromDatabase()
        val itemsFinal = ArrayList<Apartment>()

        val spinnerItems = db.getSortedUniqueCities()
        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inputCity.adapter = adapter


        findButton.setOnClickListener {
            val minPrice = inputMinPrice.text.toString()
            val maxPrice = inputMaxPrice.text.toString()
            val minRooms = inputMinRooms.text.toString()
            val maxRooms = inputMaxRooms.text.toString()
            val minSquare = inputMinSquare.text.toString()
            val maxSquare = inputMaxSquare.text.toString()
            val city = inputCity.selectedItem.toString()
            itemsFinal.clear()

            for (item in items) {
                if ( minPrice.isNotEmpty() && item.price < minPrice.toInt() ) {
                    continue
                }
                if ( maxPrice.isNotEmpty() && item.price > maxPrice.toInt() ) {
                    continue
                }
                if ( minRooms.isNotEmpty() && item.rooms < minRooms.toInt() ) {
                    continue
                }
                if ( maxRooms.isNotEmpty() && item.rooms > maxRooms.toInt() ) {
                    continue
                }
                if ( minSquare.isNotEmpty() && item.square < minSquare.toDouble() ) {
                    continue
                }
                if ( maxSquare.isNotEmpty() && item.square > maxSquare.toDouble() ) {
                    continue
                }
                if (city != item.city) {
                    continue
                }
                itemsFinal.add(item)

            }

            itemsList.layoutManager = LinearLayoutManager(this)
            itemsList.adapter = ItemsAdapter(itemsFinal, this)
        }

    }
}