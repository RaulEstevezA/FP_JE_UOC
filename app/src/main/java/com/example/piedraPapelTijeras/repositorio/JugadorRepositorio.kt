package com.example.piedraPapelTijeras.repositorio

import com.example.piedraPapelTijeras.data.dao.JugadorDao
import com.example.piedraPapelTijeras.data.model.Jugador
import com.example.piedraPapelTijeras.data.remote.Top10FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class JugadorRepositorio(
    private val jugadorDao: JugadorDao,
    private val top10FirebaseRepository: Top10FirebaseRepository
) {

    // estado del jugador actual
    private val _jugadorActual = MutableStateFlow<Jugador?>(null)
    val jugadorActual: StateFlow<Jugador?> = _jugadorActual

    // guardar el jugador que ha iniciado sesion
    fun setJugadorActual(jugador: Jugador) {
        _jugadorActual.value = jugador
    }

    // añadir jugador
    suspend fun agregarJugador(jugador: Jugador) {
        jugadorDao.insert(jugador)
    }

    // actualizar puntuacion
    suspend fun actualizarPuntuacion(jugador: Jugador) {
        jugadorDao.update(jugador)
    }

    suspend fun sumarVictoria() {
        val jugador = _jugadorActual.value ?: return

        val jugadorActualizado = jugador.copy(
            puntuacion = jugador.puntuacion + 1,
            ultimaFecha = System.currentTimeMillis()
        )

        actualizarPuntuacion(jugadorActualizado)
        _jugadorActual.value = jugadorActualizado
    }

    // obtener jugador por mail
    suspend fun obtenerJugador(email: String): Jugador? {
        return jugadorDao.obtenerJugador(email)
    }

    // obtener una lista con el top 10 (Firebase)
    suspend fun obtenerTop10(): List<Jugador> {
        return top10FirebaseRepository.obtenerTop10()
    }

    suspend fun actualizarUltimaFecha() {
        val jugador = _jugadorActual.value ?: return

        val jugadorActualizado = jugador.copy(
            ultimaFecha = System.currentTimeMillis()
        )

        jugadorDao.update(jugadorActualizado)
        _jugadorActual.value = jugadorActualizado
    }

    // actualiza jugador (la usamos para la ubicacion)
    suspend fun updateJugador(jugador: Jugador) {
        jugadorDao.update(jugador)
    }
}