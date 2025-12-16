package com.example.piedraPapelTijeras.data.remote.dto

data class BoteDto(
    val fields: Fields
)

data class Fields(
    val cantidad: IntegerValue
)

data class IntegerValue(
    val integerValue: String
)