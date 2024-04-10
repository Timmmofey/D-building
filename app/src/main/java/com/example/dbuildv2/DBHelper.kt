package com.example.dbuildv2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(val context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "dBuilding", factory, 4) {
    override fun onCreate(db: SQLiteDatabase?) {
        val queryUsers = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "phone TEXT, " +
                "last_name TEXT," +
                "first_name TEXT," +
                "password TEXT," +
                "balance REAL," +
                "photo TEXT)"
        db!!.execSQL(queryUsers)
        val queryApartments = "CREATE TABLE apartments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "rooms INTEGER, " +
                "square REAL, " +
                "city TEXT, " +
                "address TEXT, " +
                "price INT, " +
                "photo1 TEXT, " +
                "photo2 TEXT, " +
                "photo3 TEXT, " +
                "photo4 TEXT," +
                "photo5 TEXT)"
        db.execSQL(queryApartments)
        val queryRentals = "CREATE TABLE rentals (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER, " +
                "apartment_id INTEGER, " +
                "is_archived INTEGER, " +
                "FOREIGN KEY (user_id) REFERENCES users(id), " +
                "FOREIGN KEY (apartment_id) REFERENCES apartments(id))"
        db.execSQL(queryRentals)
        val queryPayments = "CREATE TABLE payments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "rental_id INTEGER, " +
                "year INTEGER, " +
                "month TEXT, " +
                "rental_price REAL," +
                "rental_price_paid INTEGER, " +
                "gas_price REAL, " +
                "gas_price_paid INTEGER, " +
                "electricity_price REAL, " +
                "electricity_price_paid INTEGER, " +
                "water_price REAL, " +
                "water_price_paid INTEGER, " +
                "FOREIGN KEY (rental_id) REFERENCES rentals(id))"
        db.execSQL(queryPayments)

        val defaultApartments = listOf(
            Apartment(1,2, 60.0, "Ростов-на-Дону", "ул. Береговая, 2а", 65000,
                "https://images.cdn-cian.ru/images/kvartira-rostovnadonu-budennovskiy-prospekt-2122804571-1.jpg",
                "https://images.cdn-cian.ru/images/kvartira-rostovnadonu-budennovskiy-prospekt-2122700861-1.jpg",
                "https://images.cdn-cian.ru/images/kvartira-rostovnadonu-budennovskiy-prospekt-2122710977-1.jpg",
                "https://images.cdn-cian.ru/images/kvartira-rostovnadonu-budennovskiy-prospekt-2122804559-1.jpg",
                "https://images.cdn-cian.ru/images/kvartira-rostovnadonu-budennovskiy-prospekt-2122700862-1.jpg"),
            Apartment(2,1, 41.1, "Ростов-на-Дону", "ул. Береговая, 6", 50000,
                "https://images.cdn-cian.ru/images/kvartira-rostovnadonu-beregovaya-ulica-2129458415-1.jpg",
                "https://images.cdn-cian.ru/images/kvartira-rostovnadonu-beregovaya-ulica-2129458409-1.jpg",
                "https://images.cdn-cian.ru/images/kvartira-rostovnadonu-beregovaya-ulica-2129456347-1.jpg",
                "https://images.cdn-cian.ru/images/kvartira-rostovnadonu-beregovaya-ulica-2129456383-1.jpg",
                "https://images.cdn-cian.ru/images/kvartira-rostovnadonu-beregovaya-ulica-2129456328-1.jpg"),
            Apartment(3,1, 42.0, "Ростов-на-Дону", "ул. Варфоломеева, 222а", 30000,
                "https://40.img.avito.st/image/1/1.4G-yrLa6TIaEG86L_ruHKn8PSowGj1oECw9OgBIJTg.k4_uajcnO2digQhnYEcN4GRikF9PIx4Fgfj82t9N01Q",
                "https://40.img.avito.st/image/1/1.55eWg7a6S36gNMlz1JSA0lsgTXQioF38LyBJeDYmSQ.pe0dZvq_Y5OSWXdNun7gTeADgGP8O6Clr6SwqpvIAtc",
                "https://50.img.avito.st/image/1/1.lKv2W7a6OELA7LpP0k3z7jv4PkhCeC7AT_g6RFb-Og.aK4y0hD-w47nF7H3NFrT2GwcPGhj0EqzjuuQRTMjfsY",
                "https://20.img.avito.st/image/1/1.62x3hLa6R4VBM8WILaPFKLonQY_Dp1EHzidFg9chRQ.V4WPHY6NwSqoX1eRdmS5B4clxHi9Rip54ZiA1bYioKI",
                "https://40.img.avito.st/image/1/1.4eO-vLa6TQqIC88H8KuGpnMfSwAKn1uIBx9PDB4ZTw.ZM-RbLY-8kRA_st0my3286MGbgZfIIsUjTy26RIto1g")
        )

        for (apartment in defaultApartments) {
            val values = ContentValues().apply {
                put("rooms", apartment.rooms)
                put("square", apartment.square)
                put("city", apartment.city)
                put("address", apartment.address)
                put("price", apartment.price)
                put("photo1", apartment.photo1)
                put("photo2", apartment.photo2)
                put("photo3", apartment.photo3)
                put("photo4", apartment.photo4)
                put("photo5", apartment.photo5)
            }
            db.insert("apartments", null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        context.deleteDatabase("dBuilding")

        onCreate(db)
    }

    fun addUser(user: User) {
        val values = ContentValues()
        values.put("phone", user.phone)
        values.put("last_name", user.lastName)
        values.put("first_name", user.firstName)
        values.put("password", user.password)
        values.put("balance", user.balance)
        values.put("photo", user.photo)

        val db = this.writableDatabase
        db.insert("users", null, values)

        db.close()
    }

    fun getUser(phone: String, password: String) : Int? {
        val db = this.readableDatabase

        val selection = "phone = ? AND password = ?"
        val selectionArgs = arrayOf(phone, password)

        val result = db.query(
            "users",
            arrayOf("id"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var userId: Int? = null
        if (result.moveToFirst()) {
            val columnIndex = result.getColumnIndex("id")
            if (columnIndex >= 0) {
                userId = result.getInt(columnIndex)
            }
        }

        result.close()
        db.close()

        return userId
    }


    fun getUserName(userId: Int) : String {
        if (userId == -1) {
            return "John Doe"
        }

        val db = this.readableDatabase

        val result = db.rawQuery(
            "SELECT last_name, first_name FROM users WHERE id = $userId",
            null
        )
        result.moveToFirst()

        val fullName = result.getString(0) + " " + result.getString(1)

        result.close()
        db.close()
        return fullName
    }

    fun getUserPhoto(userId: Int) : String {
        val db = this.readableDatabase

        val result = db.rawQuery(
            "SELECT photo FROM users WHERE id = $userId",
            null
        )
        result.moveToFirst()

        val photoURL = result.getString(0)

        result.close()
        db.close()

        if (photoURL == "none") {
            return "https://ntrepidcorp.com/wp-content/uploads/2016/06/team-1.jpg"
        }

        return photoURL
    }

    fun getAddress(userId: Int): String {
        val db = this.readableDatabase

        // Запрос для получения адреса пользователя, если у него есть арендованная квартира
        val query = "SELECT address FROM apartments " +
                "INNER JOIN rentals ON apartments.id = rentals.apartment_id " +
                "WHERE rentals.user_id = $userId"

        val result = db.rawQuery(query, null)
        val address: String

        // Проверяем, есть ли у пользователя арендованная квартира
        if (result.moveToFirst()) {
            val columnIndex = result.getColumnIndex("address")
            if (columnIndex != -1) {
                address = result.getString(columnIndex)
            } else {
                address = "Столбец с адресом не найден."
            }
        } else {
            // Если у пользователя нет квартиры, возвращаем сообщение об этом
            address = "У вас нет арендованной квартиры."
        }

        result.close()
        db.close()

        return address
    }

    fun getBalance(userId: Int) : String {
        val db = this.readableDatabase

        val result = db.rawQuery(
            "SELECT balance FROM users WHERE id = $userId",
            null
        )
        result.moveToFirst()

        val balance = result.getDouble(0)

        result.close()
        db.close()

        val integerPart = balance.toInt()

        // Если исходное число равно его целой части, то это целое число
        return if (balance == integerPart.toDouble()) {
            // Вернуть целое число без дробной части
            integerPart.toString()
        } else {
            // Если число нецелое, вернуть его без изменений
            balance.toString()
        }
    }

    fun getApartmentsFromDatabase(): ArrayList<Apartment> {
        val apartments = ArrayList<Apartment>()
        val db = this.readableDatabase

        val query = "SELECT * FROM apartments"
        val cursor = db.rawQuery(query, null)

        if (cursor != null && cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex("id")
            val roomsIndex = cursor.getColumnIndex("rooms")
            val squareIndex = cursor.getColumnIndex("square")
            val cityIndex = cursor.getColumnIndex("city")
            val addressIndex = cursor.getColumnIndex("address")
            val priceIndex = cursor.getColumnIndex("price")
            val photo1Index = cursor.getColumnIndex("photo1")
            val photo2Index = cursor.getColumnIndex("photo2")
            val photo3Index = cursor.getColumnIndex("photo3")
            val photo4Index = cursor.getColumnIndex("photo4")
            val photo5Index = cursor.getColumnIndex("photo5")

            do {
                val id = if (idIndex != -1) cursor.getInt(idIndex) else 0
                val rooms = if (roomsIndex != -1) cursor.getInt(roomsIndex) else 0
                val square = if (squareIndex != -1) cursor.getDouble(squareIndex) else 0.0
                val city = if (cityIndex != -1) cursor.getString(cityIndex) else ""
                val address = if (addressIndex != -1) cursor.getString(addressIndex) else ""
                val price = if (priceIndex != -1) cursor.getInt(priceIndex) else 0
                val photo1 = if (photo1Index != -1) cursor.getString(photo1Index) else ""
                val photo2 = if (photo2Index != -1) cursor.getString(photo2Index) else ""
                val photo3 = if (photo3Index != -1) cursor.getString(photo3Index) else ""
                val photo4 = if (photo4Index != -1) cursor.getString(photo4Index) else ""
                val photo5 = if (photo5Index != -1) cursor.getString(photo5Index) else ""

                val apartment = Apartment(id, rooms, square, city, address, price, photo1,
                    photo2, photo3, photo4, photo5)
                apartments.add(apartment)
            } while (cursor.moveToNext())

            cursor.close()
        }


        db.close()

        return apartments
    }

    fun getApartmentById(id: Int): Apartment {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM apartments WHERE id = $id", null)
        cursor.moveToFirst()
        val apartment = Apartment(
            cursor.getInt(0), cursor.getInt(1),
            cursor.getDouble(2), cursor.getString(3),
            cursor.getString(4), cursor.getInt(5),
            cursor.getString(6), cursor.getString(7),
            cursor.getString(8), cursor.getString(9),
            cursor.getString(10)
        )
        db.close()
        cursor.close()
        return apartment
    }
}
