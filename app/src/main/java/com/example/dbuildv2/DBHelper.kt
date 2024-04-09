package com.example.dbuildv2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(val context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "dBuilding", factory, 2) {
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
                "price REAL)"
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
            Apartment(2, 80.0, "Ростов-на-Дону", "ул. Береговая, 2а", 50000.0),
            Apartment(3, 100.0, "Ростов-на-Дону", "ул. Береговая, 6", 65000.0),
            Apartment(1, 50.0, "Ростов-на-Дону", "ул. Варфоломеева, 222а", 30000.0)
        )

        for (apartment in defaultApartments) {
            val values = ContentValues().apply {
                put("rooms", apartment.rooms)
                put("square", apartment.square)
                put("city", apartment.city)
                put("address", apartment.address)
                put("price", apartment.price)
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
            val roomsIndex = cursor.getColumnIndex("rooms")
            val squareIndex = cursor.getColumnIndex("square")
            val cityIndex = cursor.getColumnIndex("city")
            val addressIndex = cursor.getColumnIndex("address")
            val priceIndex = cursor.getColumnIndex("price")

            do {
                val rooms = if (roomsIndex != -1) cursor.getInt(roomsIndex) else 0
                val square = if (squareIndex != -1) cursor.getDouble(squareIndex) else 0.0
                val city = if (cityIndex != -1) cursor.getString(cityIndex) else ""
                val address = if (addressIndex != -1) cursor.getString(addressIndex) else ""
                val price = if (priceIndex != -1) cursor.getDouble(priceIndex) else 0.0

                val apartment = Apartment(rooms, square, city, address, price)
                apartments.add(apartment)
            } while (cursor.moveToNext())

            cursor.close()
        }


        db.close()

        return apartments
    }

}
