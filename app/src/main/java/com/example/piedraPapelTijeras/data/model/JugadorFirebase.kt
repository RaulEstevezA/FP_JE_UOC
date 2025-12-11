package com.example.piedraPapelTijeras.data.model


data class JugadorFirebase(
    val nombre: String = "",
    val puntuacion: Int = 0,
    //-- Añadimos latitud y longitud, que pueden ser null
    val latitud: Double? = null,
    val longitud: Double? = null
)