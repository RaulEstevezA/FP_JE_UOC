package com.example.piedraPapelTijeras.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.piedraPapelTijeras.data.model.Ubicacion

@Dao
interface UbicacionDao {

    @Insert
    suspend fun insertarUbicacion(ubicacion: Ubicacion)

    @Query("SELECT * FROM ubicaciones WHERE userId = :id ORDER BY fechaHora DESC LIMIT 1")
    suspend fun obtenerUltimaUbicacion(id: Int): Ubicacion?
}
