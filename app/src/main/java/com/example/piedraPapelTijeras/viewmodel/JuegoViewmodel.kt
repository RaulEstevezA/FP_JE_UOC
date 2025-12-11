package com.example.piedraPapelTijeras.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.piedraPapelTijeras.data.model.EnumElegirJugada
import com.example.piedraPapelTijeras.data.model.EnumResultado
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
import com.example.piedraPapelTijeras.data.model.JugadorFirebase
import com.example.piedraPapelTijeras.repositorio.RankingRepositorio
import kotlinx.coroutines.flow.first

// REGLAS DEL JUEGO
private const val PUNTOS_GANAR = 4
private const val PUNTOS_PERDER = -4
private const val PUNTOS_AL_BOTE_AL_PERDER = 2
private const val CHANNEL_ID = "victoria_channel"
private const val NOTIFICATION_ID = 10101

class JuegoViewModel(private val jugadorRepositorio: JugadorRepositorio,
                     private val context: Context
) : ViewModel() {

    // -- ESTADOS DEL JUEGO --

    private val _juegoEnCurso = MutableStateFlow(false)
    val juegoEnCurso: StateFlow<Boolean> = _juegoEnCurso

    private val _resultado = MutableStateFlow<EnumResultado?>(null)
    val resultado: StateFlow<EnumResultado?> = _resultado

    private val _jugadaMaquina = MutableStateFlow<EnumElegirJugada?>(null)
    val jugadaMaquina: StateFlow<EnumElegirJugada?> = _jugadaMaquina

    private val _puntuacion = MutableStateFlow(0)
    val puntuacion: StateFlow<Int> = _puntuacion
    
    private val _bote = MutableStateFlow(0)
    val bote: StateFlow<Int> = _bote

    private val _jugadorFirebase = MutableStateFlow<JugadorFirebase?>(null)
    val jugadorFirebase: StateFlow<JugadorFirebase?> = _jugadorFirebase

    private val rankingRepository = RankingRepositorio()

    //REST
    private val _resultadoRest = MutableStateFlow<String?>(null)
    val resultadoRest: StateFlow<String?> = _resultadoRest

    init {
        createNotificationChannel()
        // --- SINCRONIZACIÓN DIRECTA CON FIREBASE ---
        viewModelScope.launch {
            //Obtenemos el jugador local para saber su email.
            val jugadorLocal = jugadorRepositorio.jugadorActual.first { it != null}!!

            //Empezamos a ESCUCHAR a ese jugador en FIREBASE con la función.
            rankingRepository.obtenerJugadorEnTiempoReal(jugadorLocal.mail).collect { jugadorDeLaNube ->
                if (jugadorDeLaNube != null) {
                    _jugadorFirebase.value = jugadorDeLaNube
                    // La puntuación que ve la pantalla viene de la nube.
                    _puntuacion.value = jugadorDeLaNube.puntuacion
                } else {
                    // Si el jugador no existe en Firebase (ej. es un login nuevo), lo creamos.

                    rankingRepository.subirPuntuacion(jugadorLocal.mail, jugadorLocal.puntuacion)
                }
            }
        }


        // Escuchar el bote en tiempo real
        viewModelScope.launch {
            rankingRepository.obtenerBoteEnTiempoReal().collect { puntosBote ->
                _bote.value = puntosBote
            }
        }
    }



    fun jugar(jugadaJugador: EnumElegirJugada?) {
        if(_juegoEnCurso.value) return

        viewModelScope.launch {
            try {
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
                        val boteGanado = rankingRepository.llevarseBote()
                        val puntosTotalesGanados = PUNTOS_GANAR + boteGanado
                        val nombreJugador = _jugadorFirebase.value?.nombre ?: "Jugador"
                        sendWinNotification(nombreJugador, _puntuacion.value + puntosTotalesGanados)
                        modificarPuntos(puntosTotalesGanados)
                    }
                    EnumResultado.PERDISTES -> {
                        rankingRepository.sumarAlBote(PUNTOS_AL_BOTE_AL_PERDER)
                        modificarPuntos(PUNTOS_PERDER)
                    }
                    EnumResultado.EMPATE -> modificarPuntos(0)
                }

                delay(2000)
                reiniciarParaSiguienteRonda()
            } finally {
                _juegoEnCurso.value = false
            }
        }
    }

    private fun modificarPuntos(puntos: Int) {
        val jugador = _jugadorFirebase.value ?: return
        val nuevaPuntuacion = (_puntuacion.value + puntos).coerceAtLeast(0)


        viewModelScope.launch {
            // Guardamos en firebase
            rankingRepository.subirPuntuacion(jugador.nombre, nuevaPuntuacion)

        }
    }
    
    fun actualizarUbicacion(context: Context) {
        viewModelScope.launch {
            val jugador = jugadorFirebase.value ?: return@launch
            try {
                val locationService = com.example.piedraPapelTijeras.ui.util.LocationService(context)
                val ubicacion = locationService.getUserLocation()
                if (ubicacion != null) {
                    rankingRepository.actualizarUbicacion(
                        nombre = jugador.nombre,
                        latitud = ubicacion.latitude,
                        longitud = ubicacion.longitude
                    )
                } else {
                    Log.d("GPS", "No se pudo obtener la ubicación")
                }
            } catch (e: Exception) {
                Log.e("GPS", "Error al actualizar ubicación", e)
            }
        }
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
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
            Log.i("JuegoViewModel", "Evento de victoria creado en el calendario.")
        } catch (e: Exception) {
            Log.e("JuegoViewModel", "Error al intentar guardar el evento: ${e.message}")
        }
    }

    fun reiniciarParaSiguienteRonda() {
        _resultado.value = null
        _jugadaMaquina.value = null
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
            }
        } catch (e: SecurityException) {
            Log.e("JuegoViewModel", "Fallo al enviar notificación: Permiso denegado. ${e.message}")
        }
    }

    //Funcion para realizar llamada REST
    fun probarLlamadaRest() {
        //  el ID del proyecto  de Firebase.
        val projectId = "piedrapapeltijera-60fb9"

        _resultadoRest.value = ""

        viewModelScope.launch {
            try {
                // fábrica de Retrofit realizamos llamada
                val respuesta = com.example.piedraPapelTijeras.data.network.RetrofitInstance.api.getBote(projectId)

                // Comprobamos si la respuesta fue exitosa
                if (respuesta.isSuccessful) {
                    val cuerpo = respuesta.body()
                    if (cuerpo != null) {
                        val puntos = cuerpo.fields?.puntos?.value
                        // Escribimos el resultado
                        _resultadoRest.value = "Éxito con REST: El bote tiene $puntos puntos."
                    } else {
                        _resultadoRest.value = "Error: Respuesta exitosa pero sin datos."
                    }
                } else {
                    // Si la respuesta no fue exitosa
                    _resultadoRest.value = "Error REST: Código ${respuesta.code()}"
                }
            } catch (e: Exception) {
                // Si hay un error de red (sin internet, etc.)
                _resultadoRest.value = "Error de Red: ${e.message}"
            }
        }
    }
}