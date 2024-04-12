package com.example.dbuildv2

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File
import android.provider.MediaStore
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import okhttp3.MediaType.Companion.toMediaTypeOrNull

interface ImgurApiService {
    @Multipart
    @POST("image")
    fun uploadImage(
        @Header("Authorization") authorization: String,
        @Part image: MultipartBody.Part
    ): Call<ImgurUploadResponse>

    companion object {
        private const val BASE_URL = "https://api.imgur.com/3/"

        fun create(): ImgurApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ImgurApiService::class.java)
        }
    }
}

data class ImgurUploadResponse(
    val data: ImgurImageData,
    val success: Boolean,
    val status: Int
)

data class ImgurImageData(
    val link: String
)

class private_lobby_activity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var db: DBHelper
    private lateinit var payment: Payment
    private var userId: Int = 0
    private lateinit var imgurApiService: ImgurApiService

    private lateinit var rentDebt: TextView
    private lateinit var gasDebt: TextView
    private lateinit var electricityDebt: TextView
    private lateinit var waterDebt: TextView
    private lateinit var rentDebtButton: Button
    private lateinit var gasDebtButton: Button
    private lateinit var electricityDebtButton: Button
    private lateinit var waterDebtButton: Button
    private lateinit var userAddress: TextView
    private lateinit var userMore: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private_lobby)

        val sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("userId", -1)
        db = DBHelper(this, null)
        imgurApiService = ImgurApiService.create()

        val fullName: TextView = findViewById(R.id.fullName)
        val photo: ImageView = findViewById(R.id.userPhoto)
        val logOut: Button = findViewById(R.id.logout)
        val userBalance: TextView = findViewById(R.id.user_balance)
        rentDebt = findViewById(R.id.user_rentDebt)
        gasDebt = findViewById(R.id.user_gasDebt)
        electricityDebt = findViewById(R.id.user_elecDebt)
        waterDebt = findViewById(R.id.user_waterDebt)
        rentDebtButton = findViewById(R.id.buttonPayRent)
        gasDebtButton = findViewById(R.id.buttonPayGas)
        electricityDebtButton = findViewById(R.id.buttonPayElectricity)
        waterDebtButton = findViewById(R.id.buttonPayWater)
        val userChangeApartment: Button = findViewById(R.id.user_changeapart)
        val spendMoneyButton: Button = findViewById(R.id.user_spendmoney)
        val changePhotoButton: Button = findViewById(R.id.user_change_photo)
        userMore = findViewById(R.id.user_more)

        userAddress = findViewById(R.id.user_adress)
        payment = db.getPayments(db.getRentId(userId, db.getApartmentId(userId)))

        updateMarkup()

        fullName.text = db.getUserName(userId)
        val imageUrl = db.getUserPhoto(userId)
        userAddress.text = db.getAddress(userId)
        userBalance.text = db.getBalance(userId)

        Picasso.get().load(imageUrl).resize(375, 375).into(photo)


        rentDebtButton.setOnClickListener {
            if (db.getBalance(userId).toDouble() == 0.0) {
                Toast.makeText(this, "Сначала пополните баланс.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Погашение коммунального долга")
            builder.setMessage("Выберите сумму для оплаты")
            val input = EditText(this)
            input.setText(payment.rentalPrice.toString())
            input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            builder.setView(input)
            builder.setPositiveButton("Оплатить") { dialogInterface: DialogInterface, i: Int ->
                val enteredAmount = input.text.toString()

                try {
                    val amountDouble = enteredAmount.toDouble()

                    if(amountDouble > payment.rentalPrice) {
                        Toast.makeText(this, "Сумма оплаты превышает сумму долга.", Toast.LENGTH_SHORT).show()

                        return@setPositiveButton
                    }

                    if (amountDouble > db.getBalance(userId).toDouble()) {
                        Toast.makeText(this, "У вас недостаточно средств.", Toast.LENGTH_SHORT).show()

                        return@setPositiveButton
                    }

                    val newPayment = Payment(payment.id, payment.rentalId, payment.rentalPrice - amountDouble,
                        payment.rentalPricePaid, payment.gasPrice, payment.gasPricePaid, payment.electricityPrice,
                        payment.electricityPricePaid, payment.waterPrice, payment.waterPricePaid
                    )

                    db.updatePayments(newPayment)
                    db.reduceBalance(userId, amountDouble)

                    payment = db.getPayments(db.getRentId(userId, db.getApartmentId(userId)))
                    updateMarkup()
                    userBalance.text = db.getBalance(userId)
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Некорректный формат числа.", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Отмена") { dialogInterface: DialogInterface, i: Int ->

            }
            val dialog = builder.create()
            dialog.show()
        }

        gasDebtButton.setOnClickListener {
            if (db.getBalance(userId).toDouble() == 0.0) {
                Toast.makeText(this, "Сначала пополните баланс.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Погашение коммунального долга")
            builder.setMessage("Выберите сумму для оплаты")
            val input = EditText(this)
            input.setText(payment.gasPrice.toString())
            input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            builder.setView(input)
            builder.setPositiveButton("Оплатить") { dialogInterface: DialogInterface, i: Int ->
                val enteredAmount = input.text.toString()

                try {
                    val amountDouble = enteredAmount.toDouble()

                    if(amountDouble > payment.gasPrice) {
                        Toast.makeText(this, "Сумма оплаты превышает сумму долга.", Toast.LENGTH_SHORT).show()

                        return@setPositiveButton
                    }

                    if (amountDouble > db.getBalance(userId).toDouble()) {
                        Toast.makeText(this, "У вас недостаточно средств.", Toast.LENGTH_SHORT).show()

                        return@setPositiveButton
                    }

                    val newPayment = Payment(payment.id, payment.rentalId, payment.rentalPrice,
                        payment.rentalPricePaid, payment.gasPrice - amountDouble, payment.gasPricePaid, payment.electricityPrice,
                        payment.electricityPricePaid, payment.waterPrice, payment.waterPricePaid
                    )

                    db.updatePayments(newPayment)
                    db.reduceBalance(userId, amountDouble)

                    payment = db.getPayments(db.getRentId(userId, db.getApartmentId(userId)))
                    updateMarkup()
                    userBalance.text = db.getBalance(userId)
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Некорректный формат числа.", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Отмена") { dialogInterface: DialogInterface, i: Int ->

            }
            val dialog = builder.create()
            dialog.show()
        }

        electricityDebtButton.setOnClickListener {
            if (db.getBalance(userId).toDouble() == 0.0) {
                Toast.makeText(this, "Сначала пополните баланс.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Погашение коммунального долга")
            builder.setMessage("Выберите сумму для оплаты")
            val input = EditText(this)
            input.setText(payment.electricityPrice.toString())
            input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            builder.setView(input)
            builder.setPositiveButton("Оплатить") { dialogInterface: DialogInterface, i: Int ->
                val enteredAmount = input.text.toString()

                try {
                    val amountDouble = enteredAmount.toDouble()

                    if(amountDouble > payment.electricityPrice) {
                        Toast.makeText(this, "Сумма оплаты превышает сумму долга.", Toast.LENGTH_SHORT).show()

                        return@setPositiveButton
                    }

                    if (amountDouble > db.getBalance(userId).toDouble()) {
                        Toast.makeText(this, "У вас недостаточно средств.", Toast.LENGTH_SHORT).show()

                        return@setPositiveButton
                    }

                    val newPayment = Payment(payment.id, payment.rentalId, payment.rentalPrice,
                        payment.rentalPricePaid, payment.gasPrice, payment.gasPricePaid, payment.electricityPrice - amountDouble,
                        payment.electricityPricePaid, payment.waterPrice, payment.waterPricePaid
                    )

                    db.updatePayments(newPayment)
                    db.reduceBalance(userId, amountDouble)

                    payment = db.getPayments(db.getRentId(userId, db.getApartmentId(userId)))
                    updateMarkup()
                    userBalance.text = db.getBalance(userId)
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Некорректный формат числа.", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Отмена") { dialogInterface: DialogInterface, i: Int ->

            }
            val dialog = builder.create()
            dialog.show()
        }

        waterDebtButton.setOnClickListener {
            if (db.getBalance(userId).toDouble() == 0.0) {
                Toast.makeText(this, "Сначала пополните баланс.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Погашение коммунального долга")
            builder.setMessage("Выберите сумму для оплаты")
            val input = EditText(this)
            input.setText(payment.waterPrice.toString())
            input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            builder.setView(input)
            builder.setPositiveButton("Оплатить") { dialogInterface: DialogInterface, i: Int ->
                val enteredAmount = input.text.toString()

                try {
                    val amountDouble = enteredAmount.toDouble()

                    if(amountDouble > payment.waterPrice) {
                        Toast.makeText(this, "Сумма оплаты превышает сумму долга.", Toast.LENGTH_SHORT).show()

                        return@setPositiveButton
                    }

                    if (amountDouble > db.getBalance(userId).toDouble()) {
                        Toast.makeText(this, "У вас недостаточно средств.", Toast.LENGTH_SHORT).show()

                        return@setPositiveButton
                    }

                    val newPayment = Payment(payment.id, payment.rentalId, payment.rentalPrice,
                        payment.rentalPricePaid, payment.gasPrice, payment.gasPricePaid, payment.electricityPrice,
                        payment.electricityPricePaid, payment.waterPrice - amountDouble, payment.waterPricePaid
                    )

                    db.updatePayments(newPayment)
                    db.reduceBalance(userId, amountDouble)

                    payment = db.getPayments(db.getRentId(userId, db.getApartmentId(userId)))
                    updateMarkup()
                    userBalance.text = db.getBalance(userId)
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Некорректный формат числа.", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Отмена") { dialogInterface: DialogInterface, i: Int ->

            }
            val dialog = builder.create()
            dialog.show()
        }

        userChangeApartment.setOnClickListener {
            val intent = Intent(this, actvity_appartment_search::class.java)
            startActivity(intent)
        }

        userMore.setOnClickListener {
            val intent = Intent(this, appartment_item_activity::class.java)

            intent.putExtra("itemId", db.getApartmentId(userId))
            intent.putExtra("dirFrom", "lk")

            startActivity(intent)
        }

        spendMoneyButton.setOnClickListener {
            val intent = Intent(this, activity_add_funds::class.java)
            startActivity(intent)
        }

        changePhotoButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImageUri: Uri = data.data!!

            // Получаем путь к файлу из URI
            val filePath = getPathFromURI(selectedImageUri)

            // Создаем объект File на основе пути к файлу
            val file = File(filePath)

            // Создаем объект MediaType для изображений
            val mediaType = "image/*".toMediaTypeOrNull()

            // Создаем объект RequestBody для файла
            val requestFile = file.asRequestBody(mediaType)

            val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

            // Остальная часть кода остается неизменной
            imgurApiService.uploadImage("Client-ID 03eab94a993ecfe", imagePart).enqueue(object : Callback<ImgurUploadResponse> {
                override fun onResponse(call: Call<ImgurUploadResponse>, response: Response<ImgurUploadResponse>) {
                    if (response.isSuccessful) {
                        val photoUrl = response.body()?.data?.link
                        if (photoUrl != null) {
                            // Update user's photo URL in the database
                            db.updateUserPhoto(userId, photoUrl)
                            Toast.makeText(this@private_lobby_activity, "Photo updated successfully", Toast.LENGTH_SHORT).show()
                            val photo: ImageView = findViewById(R.id.userPhoto)
                            Picasso.get().load(photoUrl).resize(375, 375).into(photo)
                        } else {
                            Toast.makeText(this@private_lobby_activity, "Failed to update photo", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@private_lobby_activity, "Failed to upload photo", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ImgurUploadResponse>, t: Throwable) {
                    Toast.makeText(this@private_lobby_activity, "Network error", Toast.LENGTH_SHORT).show()
                }
            })

        }
    }

    private fun getPathFromURI(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor!!.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        val path = cursor.getString(idx)
        cursor.close()
        return path
    }

    private fun updateMarkup() {
        if (userAddress.text == "У вас нет арендованной квартиры." || userAddress.text == "Столбец с адресом не найден.") {
            val hideButtons = listOf(
                findViewById<TextView>(R.id.textView12),
                findViewById<TextView>(R.id.textView13),
                findViewById<TextView>(R.id.textView14),
                findViewById<TextView>(R.id.textView15),
                findViewById<TextView>(R.id.textView16),
                rentDebt,
                findViewById<TextView>(R.id.textView18),
                gasDebt,
                findViewById<TextView>(R.id.textView20),
                electricityDebt,
                findViewById<TextView>(R.id.textView22),
                waterDebt,
                findViewById<TextView>(R.id.textView24),
                rentDebtButton,
                gasDebtButton,
                electricityDebtButton,
                waterDebtButton,
                userMore
            )

            for (item in hideButtons) {
                item.visibility = View.INVISIBLE
            }
        } else {
            rentDebt.text = payment.rentalPrice.toString()
            gasDebt.text = payment.gasPrice.toString()
            electricityDebt.text = payment.electricityPrice.toString()
            waterDebt.text = payment.waterPrice.toString()

            if (payment.rentalPrice == 0.0) {
                rentDebtButton.setBackgroundColor(Color.parseColor("#7C7777"))
                rentDebtButton.text = "Оплачено"
                rentDebtButton.isClickable = false
                rentDebtButton.isEnabled = false
            }
            if (payment.gasPrice == 0.0) {
                gasDebtButton.setBackgroundColor(Color.parseColor("#7C7777"))
                gasDebtButton.text = "Оплачено"
                gasDebtButton.isClickable = false
                gasDebtButton.isEnabled = false
            }
            if (payment.electricityPrice == 0.0) {
                electricityDebtButton.setBackgroundColor(Color.parseColor("#7C7777"))
                electricityDebtButton.text = "Оплачено"
                electricityDebtButton.isClickable = false
                electricityDebtButton.isEnabled = false
            }
            if (payment.waterPrice == 0.0) {
                waterDebtButton.setBackgroundColor(Color.parseColor("#7C7777"))
                waterDebtButton.text = "Оплачено"
                waterDebtButton.isClickable = false
                waterDebtButton.isEnabled = false
            }
        }
    }
}
