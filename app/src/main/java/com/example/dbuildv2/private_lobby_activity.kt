package com.example.dbuildv2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        fullName.text = db.getUserName(userId)
        val imageUrl = db.getUserPhoto(userId)

        Picasso.get().load(imageUrl).resize(375, 375).into(photo)

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