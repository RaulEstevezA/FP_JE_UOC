package com.example.piedraPapelTijeras.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.piedraPapelTijeras.data.model.Jugador
import com.example.piedraPapelTijeras.repositorio.JugadorRepositorio
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
    private val _dialogoTexto = MutableStateFlow("")
    val dialogoTexto: StateFlow<String> = _dialogoTexto

    // Cargar jugador desde la BD
    fun cargarJugador(email: String, usuarioExiste: () -> Unit) {

        if(!(ValidarMail.esMailValido(email) &&
            ValidarMail.esMailGmail(email))){
            _dialogoTexto.value = "El correo no es válido debe ser un formato valido y gmail."
            _invalidoEmailDialogo.value = true // <-- CAMBIO 1
            return
        }

        viewModelScope.launch {
            val jugador = repositorio.obtenerJugador(email)
            if (jugador != null) {
                repositorio.setJugadorActual(jugador)
                repositorio.actualizarUltimaFecha()
                usuarioExiste()

            } else {
                _dialogoTexto.value =
                    "El correo '$email' no está registrado. ¿Quieres crear un nuevo usuario?"
                _usuarioNoEncontrado.value = true // <-- CAMBIO 2
            }
        }
    }

    // Añadir un nuevo jugador
    fun añadirJugador(email: String, onJuegoNavigate: () -> Unit, onTop10Refresh: () -> Unit) {

        // Por seguridad, añadimos la validación aquí también
        if(!(ValidarMail.esMailValido(email) &&
            ValidarMail.esMailGmail(email))){
            return
        }

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
            _usuarioNoEncontrado.value = false // Cerramos el diálogo de "crear usuario"
            onJuegoNavigate()
        }
    }

    // Cerrar diálogo de registro
    fun cerrarDialogo() {
        _invalidoEmailDialogo.value = false // <-- CAMBIO 3
        _usuarioNoEncontrado.value = false // <-- CAMBIO 4
        _dialogoTexto.value = ""
    }
}
