package com.example.piedraPapelTijeras.data.model

// Añadimos latitud y longitud, que pueden ser null
data class JugadorFirebase(
    val nombre: String = "",
    val puntuacion: Int = 0,
    val latitud: Double? = null,
    val longitud: Double? = null
)