package com.example.piedraPapelTijeras.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.piedraPapelTijeras.data.model.Jugador
import com.example.piedraPapelTijeras.repositorio.JugadorRepositorio
import com.example.piedraPapelTijeras.ui.mensajes.LoginMessage
import com.example.piedraPapelTijeras.ui.util.ValidarMail
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repositorio: JugadorRepositorio) : ViewModel() {

    // Para el diálogo de "correo inválido"
    private val _invalidoEmailDialogo = MutableStateFlow(false)
    val invalidoEmailDialogo: StateFlow<Boolean> = _invalidoEmailDialogo

    // Para el diálogo de "usuario no encontrado"
    private val _usuarioNoEncontrado = MutableStateFlow(false)
    val usuarioNoEncontrado: StateFlow<Boolean> = _usuarioNoEncontrado

    // Texto para diálogos (usuario no encontrado, creado, etc.)
    private val _dialogoMensaje = MutableStateFlow<LoginMessage?>(null)
    val dialogoMensaje: StateFlow<LoginMessage?> = _dialogoMensaje

    // Cargar jugador desde la BD
    fun cargarJugador(email: String, usuarioExiste: () -> Unit) {

        if (!ValidarMail.esMailValido(email) || !ValidarMail.esMailGmail(email)) {
            _dialogoMensaje.value = LoginMessage.EmailInvalido
            _invalidoEmailDialogo.value = true
            return
        }

        viewModelScope.launch {
            val jugador = repositorio.obtenerJugador(email)

            if (jugador != null) {
                repositorio.setJugadorActual(jugador)
                repositorio.actualizarUltimaFecha()
                usuarioExiste()
            } else {
                _dialogoMensaje.value = LoginMessage.UsuarioNoRegistrado
                _usuarioNoEncontrado.value = true
            }
        }
    }

    // Añadir un nuevo jugador
    fun añadirJugador(email: String, onJuegoNavigate: () -> Unit, onTop10Refresh: () -> Unit) {

        if (!ValidarMail.esMailValido(email) || !ValidarMail.esMailGmail(email)) return

        viewModelScope.launch {
            val nuevoJugador = Jugador(mail = email, ultimaFecha = System.currentTimeMillis())
            repositorio.agregarJugador(nuevoJugador)

            val jugadorConID = repositorio.obtenerJugador(email)
            jugadorConID?.let { repositorio.setJugadorActual(it) }

            onTop10Refresh()

            _dialogoMensaje.value = LoginMessage.UsuarioCreado

            delay(1500)
            _usuarioNoEncontrado.value = false
            onJuegoNavigate()
        }
    }

    // Cerrar diálogo de registro
    fun cerrarDialogo() {
        _invalidoEmailDialogo.value = false // <-- CAMBIO 3
        _usuarioNoEncontrado.value = false // <-- CAMBIO 4
        _dialogoMensaje.value = null
    }
}
