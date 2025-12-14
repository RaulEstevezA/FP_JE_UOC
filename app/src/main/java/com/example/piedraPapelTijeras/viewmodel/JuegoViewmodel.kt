package com.example.piedraPapelTijeras.viewmodel

import android.content.Context
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
import android.content.Intent
import android.provider.CalendarContract
import java.util.Calendar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.piedraPapelTijeras.R
import com.example.piedraPapelTijeras.data.remote.Top10FirebaseRepository

private const val PUNTOS_GANAR = 5
private const val PUNTOS_PERDER = -5
private const val CHANNEL_ID = "victoria_channel"
private const val NOTIFICATION_ID = 10101

class JuegoViewModel(private val repositorio: JugadorRepositorio, private val top10Viewmodel: Top10Viewmodel, private val context: Context) : ViewModel() {

    //Jugador actual de sesion
    private val _jugadorActual = MutableStateFlow<Jugador?>(null)
    val jugadorActual: StateFlow<Jugador?> = repositorio.jugadorActual
    //dar tiempo a la animacion del juego
    private val _juegoEnCurso = MutableStateFlow(false)
    val juegoEnCurso: StateFlow<Boolean> = _juegoEnCurso
    private val top10FirebaseRepository = Top10FirebaseRepository()


    private val _puntuacion = MutableStateFlow(0)
    val puntuacion: StateFlow<Int> = _puntuacion

    private val _resultado = MutableStateFlow<EnumResultado?>(null)
    val resultado: StateFlow<EnumResultado?> = _resultado

    private val _jugadaMaquina = MutableStateFlow<EnumElegirJugada?>(null)
    val jugadaMaquina: StateFlow<EnumElegirJugada?> = _jugadaMaquina


    init {
        createNotificationChannel()
        viewModelScope.launch {
            repositorio.jugadorActual.collect { jugador ->
                _jugadorActual.value = jugador
                _puntuacion.value = jugador?.puntuacion ?: 0
            }
        }
    }


    fun jugar(jugadaJugador: EnumElegirJugada?) {

        //si esta ocupado no hacemos nada
        if(_juegoEnCurso.value) return





        viewModelScope.launch {
            try {
                //avisamos que estamos empezando
                _juegoEnCurso.value = true

                _jugadaMaquina.value = null
                _resultado.value = null
                val jugadaMaquinaSeleccionada = elegirMaquina()
                _jugadaMaquina.value = jugadaMaquinaSeleccionada

                val resultadoEnum = comprobarJugada(jugadaJugador, jugadaMaquinaSeleccionada)

                delay(500)
                _resultado.value = resultadoEnum




                when (resultadoEnum) {
                    EnumResultado.GANASTES -> {
                        val emailJugador = _jugadorActual.value?.mail ?: return@launch
                        val puntuacionFinal = _puntuacion.value + PUNTOS_GANAR

                        // Guardar puntuación en Firebase
                        top10FirebaseRepository.guardarPuntuacion(
                            email = emailJugador,
                            puntos = puntuacionFinal
                        )

                        sendWinNotification(emailJugador, puntuacionFinal)
                        modificarPuntos(PUNTOS_GANAR)
                    }

                    EnumResultado.PERDISTES -> modificarPuntos(PUNTOS_PERDER)
                    EnumResultado.EMPATE -> modificarPuntos(0)
                }


                delay(2000)

                reiniciarParaSiguienteRonda()
            }finally {
                //ya hemos acabado
                _juegoEnCurso.value = false
            }

        }


    }

    fun inicializarDatosDelJuego() {

        val jugador = repositorio.jugadorActual.value
        if (jugador != null) {
            _puntuacion.value = jugador.puntuacion
        } else {
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

    fun reiniciarParaSiguienteRonda() {

        _resultado.value = null

        _jugadaMaquina.value = null
    }

    fun saveWinToCalendar(playerName: String, score: Int) {
        val beginTime = Calendar.getInstance()
        val endTime = Calendar.getInstance().apply { add(Calendar.MINUTE, 30) }
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, context.getString(R.string.evento_victoria_titulo))
            putExtra(CalendarContract.Events.EVENT_LOCATION, context.getString(R.string.evento_victoria_ubicacion))
            putExtra(
                CalendarContract.Events.DESCRIPTION,
                context.getString(R.string.evento_victoria_descripcion, playerName, score)
            )

            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.timeInMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.timeInMillis)

            // El Content Provider necesita este flag para iniciar una nueva Activity
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            // Iniciar la Activity del Calendario para crear el evento
            context.startActivity(intent)
            Log.i("JuegoViewModel", "Evento de victoria creado en el calendario.")
        } catch (e: Exception) {
            Log.e("JuegoViewModel", "Error al intentar guardar el evento en el calendario: ${e.message}")
            // Considera mostrar un Toast o Snackbar al usuario aquí.
        }
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificaciones de Victoria"
            val descriptionText = "Muestra una notificación cuando el jugador gana una ronda."
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun sendWinNotification(playerName: String, score: Int) {
        val title = context.getString(R.string.notificacion_victoria_titulo, playerName)
        val message = context.getString(R.string.notificacion_victoria_mensaje, score)

        val iconId = android.R.drawable.ic_dialog_info
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(iconId)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(NOTIFICATION_ID, builder.build())
                Log.i("JuegoViewModel", "Notificación de victoria enviada.")
            }
        } catch (e: SecurityException) {
            Log.e("JuegoViewModel", "Fallo al enviar notificación: Permiso denegado. ${e.message}")
        }
    }

    fun actualizarUbicacion(context: android.content.Context){
        viewModelScope.launch {
            try{
                //llamamos a nuestra classe GPS
                val locationService = com.example.piedraPapelTijeras.ui.util.LocationService(context)
                val ubicacion = locationService.getUserLocation()

                if(ubicacion != null){
                    //Si encontramos ubicacion, cogemos el jugador actual
                    _jugadorActual.value?.let { jugador ->
                        //creamos una copia del jugador pero con las coordenadas nuevas
                        val jugadorActualizado = jugador.copy(
                            latitud = ubicacion.latitude,
                            longitud = ubicacion.longitude
                        )
                        //lo guardamos en la Bd
                        repositorio.updateJugador(jugadorActualizado)

                        //Lo actualizamos en la memoria para que conozca los nuevos datos
                        _jugadorActual.value = jugadorActualizado

                        android.util.Log.d(
                            "GPS",
                            "Ubicación guardada: ${ubicacion.latitude}, ${ubicacion.longitude}"
                        )
                    }

                }else{
                    android.util.Log.d(
                        "GPS",
                        "No se pudo obtener la ubicación (es null)"
                    )
                }
            }catch (e: Exception){
                android.util.Log.e(
                    "GPS",
                    "Error al intentar guardar la ubicación", e
                )
            }
        }
    }
}






