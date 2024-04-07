package com.example.dbuildv2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class authorisation_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorisation)

        val userLogin: EditText = findViewById(R.id.phone_auth)
        val userPassword: EditText = findViewById(R.id.pwd_auth)
        val loginButton: Button = findViewById(R.id.lgn_btn)
        val linkRegButton: Button = findViewById(R.id.link_reg_btn)

        loginButton.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val password = userPassword.text.toString().trim()
            if (login == "" || password == "") {
                Toast.makeText(
                    this,
                    "Введите логин и пароль",
                    Toast.LENGTH_SHORT
                ).show()
            }
            val db = DBHelper(this, null)
            val userId = db.getUser(login, password)

            if (userId == null) {
                Toast.makeText(
                    this,
                    "Неверные данные для входа",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt("userId", userId)
            editor.apply()

            val intent = Intent(this, private_lobby_activity::class.java)
            startActivity(intent)
        }

        linkRegButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}