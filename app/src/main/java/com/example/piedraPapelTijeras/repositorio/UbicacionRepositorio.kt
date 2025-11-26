package com.example.piedraPapelTijeras.repositorio

import com.example.piedraPapelTijeras.data.dao.UbicacionDao
import com.example.piedraPapelTijeras.data.model.Ubicacion

class UbicacionRepositorio(private val ubicacionDao: UbicacionDao) {

    suspend fun guardarUbicacion(userId: Int, lat: Double, lon: Double) {
        val ubicacion = Ubicacion(
            userId = userId,
            latitud = lat,
            longitud = lon,
            fechaHora = System.currentTimeMillis()
        )
        ubicacionDao.insertarUbicacion(ubicacion)
    }

    suspend fun obtenerUltimaUbicacion(userId: Int): Ubicacion? {
        return ubicacionDao.obtenerUltimaUbicacion(userId)
    }
}
