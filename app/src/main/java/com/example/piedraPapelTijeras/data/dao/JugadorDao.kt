package com.example.piedraPapelTijeras.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.piedraPapelTijeras.data.model.Jugador

@Dao
interface JugadorDao {

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insert(jugador: Jugador)

    @Update
    suspend fun update(jugador: Jugador)

    @Delete
    suspend fun delete(jugador: Jugador)

    @Query("SELECT * FROM jugadores WHERE mail = :correo Limit 1")
    suspend fun obtenerJugador(correo: String): Jugador?

    @Query("SELECT * FROM jugadores ORDER BY puntuacion DESC Limit 10")
    suspend fun obtenerTop(): List<Jugador>

}