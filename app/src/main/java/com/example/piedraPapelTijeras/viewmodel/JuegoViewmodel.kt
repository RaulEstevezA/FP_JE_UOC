package com.example.piedraPapelTijeras.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.piedraPapelTijeras.data.model.EnumElegirJugada
import com.example.piedraPapelTijeras.data.model.EnumResultado
import com.example.piedraPapelTijeras.data.model.Jugador
import com.example.piedraPapelTijeras.repositorio.JugadorRepositorio
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val PUNTOS_GANAR = 5
private const val PUNTOS_PERDER = -5

class JuegoViewModel(private val repositorio: JugadorRepositorio, private val top10Viewmodel: Top10Viewmodel) : ViewModel() {

    //Jugador actual de sesion
    private val _jugadorActual = MutableStateFlow<Jugador?>(null)
    val jugadorActual: StateFlow<Jugador?> = repositorio.jugadorActual


    private val _puntuacion = MutableStateFlow(0)
    val puntuacion: StateFlow<Int> = _puntuacion

    private val _resultado = MutableStateFlow("")
    val resultado: StateFlow<String> = _resultado

    private val _jugadaMaquina = MutableStateFlow<EnumElegirJugada?>(null)
    val jugadaMaquina: StateFlow<EnumElegirJugada?> = _jugadaMaquina

    private val _mostrarDialogo = MutableStateFlow(false)
    val mostrarDialogo: StateFlow<Boolean> = _mostrarDialogo


    init {
        viewModelScope.launch {
            repositorio.jugadorActual.collect { jugador ->
                _puntuacion.value = jugador?.puntuacion ?: 0
            }
        }
    }






    fun jugar(jugadaJugador: EnumElegirJugada?){

        val jugadaMaquina = elegirMaquina()
        _jugadaMaquina.value = jugadaMaquina

        val resultado = comprobarJugada(jugadaJugador ,jugadaMaquina )

        _resultado.value = resultado.name

        when (resultado) {

            EnumResultado.GANASTES -> modificarPuntos(PUNTOS_GANAR)
            EnumResultado.PERDISTES -> modificarPuntos(PUNTOS_PERDER)
            EnumResultado.EMPATE -> modificarPuntos(0)

        }

        viewModelScope.launch {

            delay(1000)

            _mostrarDialogo.value = true

        }

    }

    fun inicializarDatosDelJuego(){

        val jugador = repositorio.jugadorActual.value
        if (jugador != null) {
            _puntuacion.value = jugador.puntuacion
        }else{
            _puntuacion.value = 0
        }


    }


    private fun elegirMaquina(): EnumElegirJugada {
        val eleccion = (1..3).random()
        return when (eleccion) {
            1 -> EnumElegirJugada.PIEDRA
            2 -> EnumElegirJugada.PAPEL
            else -> EnumElegirJugada.TIJERA
        }
    }

    fun comprobarJugada(jugador: EnumElegirJugada?, maquina: EnumElegirJugada?): EnumResultado {
        return when {
            jugador == maquina -> EnumResultado.EMPATE

            (jugador == EnumElegirJugada.PIEDRA && maquina == EnumElegirJugada.TIJERA) ||
                    (jugador == EnumElegirJugada.TIJERA && maquina == EnumElegirJugada.PAPEL) ||
                    (jugador == EnumElegirJugada.PAPEL && maquina == EnumElegirJugada.PIEDRA) -> EnumResultado.GANASTES
            else -> EnumResultado.PERDISTES

        }
    }

    private fun modificarPuntos(puntos: Int) {

        val jugador = jugadorActual.value ?: return

        val nuevaPuntuacion = (_puntuacion.value + puntos).coerceAtLeast(0)
        _puntuacion.value = nuevaPuntuacion

        val jugadorActualizado = jugador.copy(puntuacion = nuevaPuntuacion)



        viewModelScope.launch {
            repositorio.actualizarPuntuacion(jugadorActualizado)
            top10Viewmodel.cargarTop10()
        }
    }


    fun cerrarDialogo() {
        _mostrarDialogo.value = false
        _jugadaMaquina.value = null
        _resultado.value = ""
    }



}