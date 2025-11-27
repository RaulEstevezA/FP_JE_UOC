package com.example.piedraPapelTijeras.data.source

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.piedraPapelTijeras.data.model.Jugador
import com.example.piedraPapelTijeras.data.dao.JugadorDao

const val DATABASE_NAME = "jugadores.db"

@Database(
    entities = [Jugador::class],
    version = 3,
    exportSchema = false
)
abstract class JugadoresDatabase : RoomDatabase() {

    abstract val jugadorDao: JugadorDao


    companion object {

        @Volatile
        private var Instance: JugadoresDatabase? = null

        fun getInstance(context: Context): JugadoresDatabase {

            return Instance ?: synchronized(this) {

                Room.databaseBuilder(
                    context,
                    JugadoresDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()   // evita error al añadir tabla nueva
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
