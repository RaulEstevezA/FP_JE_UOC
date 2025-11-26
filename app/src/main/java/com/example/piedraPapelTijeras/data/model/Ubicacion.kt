package com.example.piedraPapelTijeras.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ubicaciones")
data class Ubicacion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val latitud: Double,
    val longitud: Double,
    val fechaHora: Long
)
