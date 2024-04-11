package com.example.dbuildv2

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.time.LocalDate

class activity_add_funds : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_funds)

        val cardNumber: EditText = findViewById(R.id.balance_card)
        val cardExpiration: EditText = findViewById(R.id.balance_exp)
        val cardCvc: EditText = findViewById(R.id.balance_cvc)
        val cardAmount: EditText = findViewById(R.id.balance_summ)
        val addFundsButton: Button = findViewById(R.id.balance_button)

        val currentDate = LocalDate.now()

        addFundsButton.setOnClickListener {
            if (cardNumber.text.length != 16) {
                Toast.makeText(this, "Ошибка в номере карты", Toast.LENGTH_SHORT).show()
            } else if (cardExpiration.text.length != 5 || cardExpiration.text[2] != '/') {
                Toast.makeText(this, "Неправильный срок действия карты. MM/YY.", Toast.LENGTH_SHORT).show()
            } else if (cardCvc.text.length != 3) {
                Toast.makeText(this, "Введите трёхзначный CVC код.", Toast.LENGTH_SHORT).show()
            } else if (cardAmount.text.toString() == "" || cardAmount.text.toString() == "0") {
                Toast.makeText(this, "Введите сумму для пополнения.", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    val dm = DateManager()

                    val cardNum = cardNumber.text.toString().toDouble()
                    val CVC = cardCvc.text.toString().toInt()
                    val cardMonth = dm.removeZero(cardExpiration.text[0].toString() + cardExpiration.text[1].toString()).toInt()
                    val cardYear = dm.removeZero(cardExpiration.text[3].toString() + cardExpiration.text[4].toString()).toInt()
                    val fullcardYear: Int = ("20" + cardYear.toString()).toInt()
                    val paymentAmount = cardAmount.text.toString().toInt()

                    if ( (fullcardYear < currentDate.year) || (fullcardYear == currentDate.year && cardMonth < currentDate.monthValue) ) {
                        Toast.makeText(this, "Срок карты истек.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE)
                    val userId = sharedPreferences.getInt("userId", -1)

                    val db = DBHelper(this, null)
                    db.addBalance(userId, paymentAmount)

                    val intent = Intent(this, private_lobby_activity::class.java)
                    startActivity(intent)
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Убедитесь, что вы вводите цифры", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private class DateManager {
        fun stringToInt(string: String) : Int {
            if (string[0] == '0') {
                return string[1].toString().toInt()
            }
            return string.toInt()
        }

        fun removeZero(string: String) : String {
            if (string[0] == '0') {
                return string[1].toString()
            }
            return string
        }
    }
}