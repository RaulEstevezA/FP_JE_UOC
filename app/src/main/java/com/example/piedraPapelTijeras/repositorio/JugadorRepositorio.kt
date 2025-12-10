package com.example.piedraPapelTijeras.repositorio

import com.example.piedraPapelTijeras.data.dao.JugadorDao
import com.example.piedraPapelTijeras.data.model.Jugador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class JugadorRepositorio(private val jugadorDao: JugadorDao) {

    //estado del jugador actual
    private val _jugadorActual = MutableStateFlow<Jugador?>(null)
    val jugadorActual: StateFlow<Jugador?> = _jugadorActual

    //gardar el jugador que ha iniciado sesion
    fun setJugadorActual(jugador: Jugador) {
        _jugadorActual.value = jugador
    }

    //añadir jugador
    suspend fun agregarJugador(jugador: Jugador){

        jugadorDao.insert(jugador)


    }



    //actualizar puntación
    suspend fun actualizarPuntuacion(jugador: Jugador){



        jugadorDao.update(jugador)

    }

    suspend fun sumarVictoria() {
        val jugador = _jugadorActual.value

        if (jugador != null) {
            // Crear una copia del jugador con la nueva puntuación
            val jugadorActualizado = jugador.copy(
                puntuacion = jugador.puntuacion + 1,
                ultimaFecha = System.currentTimeMillis() // Opcional: actualizar la fecha
            )

            // Actualizar la base de datos
            actualizarPuntuacion(jugadorActualizado)

            // Actualizar el StateFlow para que otras partes de la app (como la UI del juego) lo vean
            _jugadorActual.value = jugadorActualizado
        }
    }

    //obtener jugador por mail
    suspend fun obtenerJugador(email: String): Jugador? {

        val jugador = jugadorDao.obtenerJugador(email)



        return jugador

    }

    //obtenerl una lista con el top 10
    suspend fun obtenerTop10():List<Jugador>{

        return jugadorDao.obtenerTop()


    }

    suspend fun actualizarUltimaFecha() {
        val jugador = _jugadorActual.value
        if (jugador != null) {
            val jugadorActualizado = jugador.copy(
                ultimaFecha = System.currentTimeMillis()
            )
            jugadorDao.update(jugadorActualizado)
            _jugadorActual.value = jugadorActualizado
        }
    }
    //actualiza jugador la usamos para la ubicacion
    suspend fun updateJugador(jugador: Jugador){
        jugadorDao.update(jugador)
    }


}