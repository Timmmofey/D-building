package com.example.dbuildv2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ItemsAdapter(var items: List<Apartment>, var context: Context) : RecyclerView.Adapter<ItemsAdapter.MyViewHolder>() {
    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.item_image)
        val city: TextView = view.findViewById(R.id.item_city)
        val address: TextView = view.findViewById(R.id.item_address)
        val rooms: TextView = view.findViewById(R.id.item_rooms)
        val square: TextView = view.findViewById(R.id.item_square)
        val price: TextView = view.findViewById(R.id.item_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_appartment_search_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Picasso.get().load("https://my-dom.design/wp-content/uploads/2023/07/6.jpg").resize(375, 375).into(holder.image)
        holder.city.text = items[position].city
        holder.address.text = items[position].address
        holder.rooms.text = items[position].rooms.toString()
        holder.square.text = items[position].square.toString()
        holder.price.text = items[position].price.toString()
    }
}