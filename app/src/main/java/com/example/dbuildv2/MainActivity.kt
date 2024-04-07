package com.example.dbuildv2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_acticity)

        val sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", -1)
        if (userId > 0) {
            val intent = Intent(this, private_lobby_activity::class.java)
            startActivity(intent)
        }

        val lastNameInput: EditText = findViewById(R.id.last_name)
        val firstNameInput: EditText = findViewById(R.id.first_name)
        val phoneNumberInput: EditText = findViewById(R.id.phone_number)
        val passwordInput: EditText = findViewById(R.id.pwd)
        val passwordRepeatInput: EditText = findViewById(R.id.pwd_repeat)
        val acceptPersonalData: CheckBox = findViewById(R.id.personal_data)
        val acceptTermsOfUse: CheckBox = findViewById(R.id.terms_of_use)
        val regButton: Button = findViewById(R.id.reg_btn)
        val toLoginButton: Button = findViewById(R.id.to_log_btn)

        regButton.setOnClickListener {
            if (!acceptPersonalData.isChecked || !acceptTermsOfUse.isChecked) {
                Toast.makeText(this,"Регистрация невозможна без принятия условий соглашения.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val lastName = lastNameInput.text.toString().trim()
            val firstName = firstNameInput.text.toString().trim()
            val phoneNumber = phoneNumberInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val passwordRepeat = passwordRepeatInput.text.toString().trim()

            if (lastName == "" || firstName == "") {
                Toast.makeText(this,"Введите фамилию и имя.", Toast.LENGTH_SHORT).show()
            } else if (password.length < 8) {
                Toast.makeText(this,"Минимальная длина пароля - 8 символов.", Toast.LENGTH_SHORT).show()
            } else if (password != passwordRepeat) {
                Toast.makeText(this,"Введенные пароли не совпадают.", Toast.LENGTH_SHORT).show()
            } else if (phoneNumber.length != 12 || phoneNumber[0] != '+' || phoneNumber[1] != '7') {
                Toast.makeText(this,"Введите корректный номер телефона через +7.", Toast.LENGTH_SHORT).show()
            } else {
                val user = User(phoneNumber, lastName, firstName, password)

                val db = DBHelper(this, null)
                db.addUser(user)

                Toast.makeText(this,"Регистрация прошла успешно.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, authorisation_activity::class.java)
                startActivity(intent)
            }
        }

        toLoginButton.setOnClickListener {
            val intent = Intent(this, authorisation_activity::class.java)
            startActivity(intent)
        }

    }
}