package com.example.piedraPapelTijeras.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jugadores")
data class Jugador(
    @PrimaryKey(autoGenerate = true)
    val id: Int= 0,
    val mail: String,
    var puntuacion: Int = 0,
    var ultimaFecha: Long = 0L,
    //variables ubicacion pueden ser null si el usuario no da permiso
    val latitud: Double? = null,
    val longitud: Double? = null

)