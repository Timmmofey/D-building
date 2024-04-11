package com.example.dbuildv2

import android.app.AlertDialog
import android.content.DialogInterface
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.squareup.picasso.Picasso

class appartment_item_activity : AppCompatActivity() {
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appartment_item)

        val db = DBHelper(this, null)

        val sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", -1)

        val photos: ViewPager2 = findViewById(R.id.itemmore_photos)
        val price: TextView = findViewById(R.id.itemmore_price)
        val rooms: TextView = findViewById(R.id.itemmore_rooms)
        val square: TextView = findViewById(R.id.itemmore_square)
        val city: TextView = findViewById(R.id.itemmore_city)
        val address: TextView = findViewById(R.id.itemmore_address)
        val chooseButton: Button = findViewById(R.id.itemmore_choosebtn)
        val backButton: Button = findViewById(R.id.itemmore_back)
        val rejectButton: Button = findViewById(R.id.itemmore_reject)
        val backtToLkButton: Button = findViewById(R.id.itemmore_backLK)

        val apartId = intent.getIntExtra("itemId", 1)
        val directedFrom = intent.getStringExtra("dirFrom")

        if (directedFrom == "search") {
            rejectButton.visibility = View.INVISIBLE
            backtToLkButton.visibility = View.INVISIBLE
        }

        val apartment = db.getApartmentById(apartId)
        price.text = apartment.price.toString()
        rooms.text = apartment.rooms.toString()
        square.text = apartment.square.toString()
        city.text = apartment.city
        address.text = apartment.address

        val imageUrls = arrayOf(
            apartment.photo1,
            apartment.photo2,
            apartment.photo3,
            apartment.photo4,
            apartment.photo5
        )

        val adapter = ImagePagerAdapter(imageUrls)
        photos.adapter = adapter

        db.close()

        chooseButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Подтверждение аренды")
            builder.setMessage("Вы уверены, что хотите арендовать эту квартиру?")
            builder.setPositiveButton("Да") { dialogInterface: DialogInterface, i: Int ->
                if (db.getBalance(userId).toInt() >= apartment.price) {
                    val isApartmentRented = (db.getAddress(userId) != "У вас нет арендованной квартиры." &&
                            db.getAddress(userId) != "Столбец с адресом не найден.")

                    db.rentApartment(userId, apartId, isApartmentRented, db.getBalance(userId).toInt() - apartment.price)

                    db.close()

                    Toast.makeText(this,"Вы арендовали квартиру.", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, private_lobby_activity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this,"Недостаточно средств для аренды.", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Нет") { dialogInterface: DialogInterface, i: Int ->

            }
            val dialog = builder.create()
            dialog.show()
        }

        backButton.setOnClickListener {
            val intent = Intent(this, actvity_appartment_search::class.java)
            startActivity(intent)
        }

        db.close()
    }

    private inner class ImagePagerAdapter(private val imageUrls: Array<String>) :
        androidx.viewpager2.adapter.FragmentStateAdapter(this) {
        override fun getItemCount(): Int = imageUrls.size

        override fun createFragment(position: Int): androidx.fragment.app.Fragment {
            val fragment = ImageFragment()
            val bundle = Bundle()
            bundle.putString("imageUrl", imageUrls[position])
            fragment.arguments = bundle
            return fragment
        }
    }

    class ImageFragment : androidx.fragment.app.Fragment() {
        override fun onCreateView(
            inflater: android.view.LayoutInflater, container: android.view.ViewGroup?,
            savedInstanceState: android.os.Bundle?
        ): android.view.View? {
            val rootView = inflater.inflate(R.layout.activity_fragment_image, container, false)
            val imageView = rootView.findViewById<ImageView>(R.id.imageView)

            val imageUrl = arguments?.getString("imageUrl")
            if (!imageUrl.isNullOrEmpty()) {
                Picasso.get().load(imageUrl).resize(375, 250).into(imageView)
            }

            return rootView
        }
    }

}