package com.example.piedraPapelTijeras.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.piedraPapelTijeras.data.model.Jugador
import com.example.piedraPapelTijeras.repositorio.JugadorRepositorio
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repositorio: JugadorRepositorio) : ViewModel() {



    // Estado de registro de usuario
    private val _registroNuevo = MutableStateFlow(false)
    val registroNuevo: StateFlow<Boolean> = _registroNuevo

    // Texto para diálogos (usuario no encontrado, creado, etc.)
    private val _dialogoTexto = MutableStateFlow("")
    val dialogoTexto: StateFlow<String> = _dialogoTexto




    // Cargar jugador desde la BD
    fun cargarJugador(email: String, usuarioExiste: () -> Unit) {
        viewModelScope.launch {
            val jugador = repositorio.obtenerJugador(email)
            if (jugador != null) {

                repositorio.setJugadorActual(jugador)

                repositorio.actualizarUltimaFecha()

                _registroNuevo.value = false
                usuarioExiste()

            } else {

                _dialogoTexto.value =
                    "El correo '$email' no está registrado. ¿Quieres crear un nuevo usuario?"
                _registroNuevo.value = true
            }
        }
    }

    // Añadir un nuevo jugador
    fun añadirJugador(email: String, onJuegoNavigate: () -> Unit, onTop10Refresh: () -> Unit) {
        viewModelScope.launch {
            val nuevoJugador = Jugador(mail = email, ultimaFecha = System.currentTimeMillis())
            repositorio.agregarJugador(nuevoJugador)
            val jugadorConID = repositorio.obtenerJugador(email)
            
            if (jugadorConID != null) {
                repositorio.setJugadorActual(jugadorConID)
            }

            onTop10Refresh()

            _dialogoTexto.value = "Usuario creado con éxito."
            delay(1500)
            _registroNuevo.value = false
            onJuegoNavigate()
        }
    }

    // Cerrar diálogo de registro
    fun cerrarDialogo() {
        _registroNuevo.value = false
        _dialogoTexto.value = ""
    }
}
