package com.example.piedraPapelTijeras.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.piedraPapelTijeras.data.model.Jugador
import com.example.piedraPapelTijeras.repositorio.JugadorRepositorio
import com.example.piedraPapelTijeras.ui.mensajes.LoginMessage
import com.example.piedraPapelTijeras.ui.util.LocationService
import com.example.piedraPapelTijeras.ui.util.ValidarMail
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.example.piedraPapelTijeras.data.auth.FirebaseAuthManager

class LoginViewModel(
    private val repositorio: JugadorRepositorio,
    private val context: Context
) : ViewModel() {

    private val _invalidoEmailDialogo = MutableStateFlow(false)
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthManager = FirebaseAuthManager()
    val invalidoEmailDialogo: StateFlow<Boolean> = _invalidoEmailDialogo


    private val _usuarioNoEncontrado = MutableStateFlow(false)
    val usuarioNoEncontrado: StateFlow<Boolean> = _usuarioNoEncontrado

    private val _dialogoMensaje = MutableStateFlow<LoginMessage?>(null)
    val dialogoMensaje: StateFlow<LoginMessage?> = _dialogoMensaje

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

    fun añadirJugador(
        email: String,
        onJuegoNavigate: () -> Unit,
        onTop10Refresh: () -> Unit
    ) {

        if (!ValidarMail.esMailValido(email) || !ValidarMail.esMailGmail(email)) return

        viewModelScope.launch {

            val nuevoJugador = Jugador(
                mail = email,
                ultimaFecha = System.currentTimeMillis()
            )
            repositorio.agregarJugador(nuevoJugador)

            val jugadorConID = repositorio.obtenerJugador(email)
            jugadorConID?.let { repositorio.setJugadorActual(it) }

            // obtener ubicación al crear un jugador
            val locationService = LocationService(context)
            val ubicacion = locationService.getUserLocation()

            if (ubicacion != null && jugadorConID != null) {

                val jugadorActualizado = jugadorConID.copy(
                    latitud = ubicacion.latitude,
                    longitud = ubicacion.longitude
                )

                repositorio.updateJugador(jugadorActualizado)
                repositorio.setJugadorActual(jugadorActualizado)
            }

            onTop10Refresh()
            _dialogoMensaje.value = LoginMessage.UsuarioCreado
            delay(1500)
            _usuarioNoEncontrado.value = false
            onJuegoNavigate()
        }
    }

    fun loginConGoogle(
        idToken: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = firebaseAuthManager.signInWithGoogle(idToken)

            result
                .onSuccess { email ->
                    // reutilizamos tu lógica actual
                    viewModelScope.launch {
                        val jugador = repositorio.obtenerJugador(email)

                        if (jugador != null) {
                            repositorio.setJugadorActual(jugador)
                            repositorio.actualizarUltimaFecha()
                            onSuccess()
                        } else {
                            // crear jugador automáticamente si viene de Google
                            añadirJugador(
                                email = email,
                                onJuegoNavigate = onSuccess,
                                onTop10Refresh = {}
                            )
                        }
                    }
                }
                .onFailure {
                    onError(it.message ?: "Error login Google")
                }
        }
    }

    fun cerrarDialogo() {
        _invalidoEmailDialogo.value = false
        _usuarioNoEncontrado.value = false
        _dialogoMensaje.value = null
    }
}
