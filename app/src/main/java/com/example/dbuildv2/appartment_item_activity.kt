package com.example.dbuildv2

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.squareup.picasso.Picasso

class appartment_item_activity : AppCompatActivity() {
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appartment_item)

        val db = DBHelper(this, null)

        val photos: ViewPager2 = findViewById(R.id.itemmore_photos)
        val price: TextView = findViewById(R.id.itemmore_price)
        val rooms: TextView = findViewById(R.id.itemmore_rooms)
        val square: TextView = findViewById(R.id.itemmore_square)
        val city: TextView = findViewById(R.id.itemmore_city)
        val address: TextView = findViewById(R.id.itemmore_address)

        val apartId = intent.getIntExtra("itemId", 1)

        val apartment = db.getApartmentById(apartId)
        price.text = apartment.price.toString()
        rooms.text = apartment.rooms.toString()
        square.text = apartment.square.toString()
        city.text = apartment.city
        address.text = apartment.address

        val imageUrls = arrayOf(
            "https://opis-cdn.tinkoffjournal.ru/mercury/prodala-kvartiry-in.jpg",
            "https://mainadmin.novostroyki.shop/media/imagemanager/43242_ee445a56ae509627ddd968ecef5c63f1.jpg",
            "https://remont-f.ru/upload/iblock/4f8/dizayn-interyera-2-komnatnoj-kvartiry-47-kv-m-foto-6-3814.jpg"
        )

        val adapter = ImagePagerAdapter(imageUrls)
        photos.adapter = adapter
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