package com.example.dbuildv2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class private_lobby_activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private_lobby)

        val sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", -1)
        val db = DBHelper(this, null)

        val fullName: TextView = findViewById(R.id.fullName)
        val photo: ImageView = findViewById(R.id.userPhoto)
        val logOut: Button = findViewById(R.id.logout)
        val userAddress: TextView = findViewById(R.id.user_adress)
        val userBalance: TextView = findViewById(R.id.user_balance)
        val userChangeApartment: Button = findViewById(R.id.user_changeapart)

        val hideButtons = listOf(
            findViewById<TextView>(R.id.textView12),
            findViewById<TextView>(R.id.textView13),
            findViewById<TextView>(R.id.textView14),
            findViewById<TextView>(R.id.textView15),
            findViewById<TextView>(R.id.textView16),
            findViewById<TextView>(R.id.textView17),
            findViewById<TextView>(R.id.textView18),
            findViewById<TextView>(R.id.textView19),
            findViewById<TextView>(R.id.textView20),
            findViewById<TextView>(R.id.textView21),
            findViewById<TextView>(R.id.textView22),
            findViewById<TextView>(R.id.textView23),
            findViewById<TextView>(R.id.textView24),
            findViewById<TextView>(R.id.textView25),
            findViewById<Button>(R.id.buttonPayRent),
            findViewById<Button>(R.id.buttonPayGas),
            findViewById<Button>(R.id.buttonPayElectricity),
            findViewById<Button>(R.id.buttonPayWater)
        )

        fullName.text = db.getUserName(userId)
        val imageUrl = db.getUserPhoto(userId)
        userAddress.text = db.getAddress(userId)
        userBalance.text = db.getBalance(userId)

        Picasso.get().load(imageUrl).resize(375, 375).into(photo)

        if (userAddress.text == "У вас нет арендованной квартиры." || userAddress.text == "Столбец с адресом не найден.") {
            for (item in hideButtons) {
                item.visibility = View.INVISIBLE
            }
        }

        userChangeApartment.setOnClickListener {
            val intent = Intent(this, actvity_appartment_search::class.java)
            startActivity(intent)
        }

        logOut.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.remove("userId") // удалить идентификатор пользователя или другие данные сеанса
            editor.apply()

            val intent = Intent(this, authorisation_activity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Закрываем текущую активность, чтобы пользователь не мог вернуться по кнопке "назад"

        }
    }
}