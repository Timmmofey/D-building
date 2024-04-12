package com.example.dbuildv2

class Payment(
    val id: Int,
    val rentalId: Int,
    val rentalPrice: Double,
    val rentalPricePaid: Int,
    val gasPrice: Double,
    val gasPricePaid: Int,
    val electricityPrice: Double,
    val electricityPricePaid: Int,
    val waterPrice: Double,
    val waterPricePaid: Int
)
