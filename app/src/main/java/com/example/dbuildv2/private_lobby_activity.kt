package com.example.dbuildv2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import okhttp3.MediaType
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
    private var userId: Int = 0
    private lateinit var imgurApiService: ImgurApiService

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
        val userAddress: TextView = findViewById(R.id.user_adress)
        val userBalance: TextView = findViewById(R.id.user_balance)
        val userChangeApartment: Button = findViewById(R.id.user_changeapart)
        val userMore: Button = findViewById(R.id.user_more)
        val spendMoneyButton: Button = findViewById(R.id.user_spendmoney)
        val changePhotoButton: Button = findViewById(R.id.user_change_photo)

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
            findViewById<Button>(R.id.buttonPayWater),
            findViewById<Button>(R.id.user_more)
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
}
