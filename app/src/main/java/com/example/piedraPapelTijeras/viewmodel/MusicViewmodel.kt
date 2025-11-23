package com.example.piedraPapelTijeras.viewmodel

import android.content.Context
import android.media.MediaPlayer
import android.media.AudioManager
import android.media.AudioFocusRequest
import android.media.AudioAttributes
import android.os.Build
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.os.Handler
import android.os.Looper

class MusicViewModel : ViewModel() {

    private lateinit var audioManager: AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null

    private val _isMusicPlaying = MutableStateFlow(true)
    val isMusicPlaying: StateFlow<Boolean> = _isMusicPlaying

    private var mediaPlayer: MediaPlayer? = null

    private val _currentMusicUri = MutableStateFlow<Uri?>(null)
    val currentMusicUri: StateFlow<Uri?> = _currentMusicUri.asStateFlow()

    private val handler = Handler(Looper.getMainLooper())

    private fun abandonFocusInternal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(null)
        }
        // No toca _isMusicPlaying
    }

    fun initMediaPlayer(mp: MediaPlayer, context: Context) {
        mediaPlayer = mp
        mediaPlayer?.isLooping = true

        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    fun toggleMusic(context: Context) {
        val currentlyPlaying = _isMusicPlaying.value
        _isMusicPlaying.value = !currentlyPlaying

        if (currentlyPlaying) {
            pauseMusic(context)
        } else {
            startMusic(context)
        }
    }

    fun startMusic(context: Context) {
        if (_isMusicPlaying.value && requestAudioFocus(context)) {
            if (mediaPlayer?.isPlaying == false) {
                mediaPlayer?.start()
            }
            _isMusicPlaying.value = true
        }
    }

    fun pauseMusic(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            audioManager.abandonAudioFocus(null)
        }
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
        _isMusicPlaying.value = false
    }

    override fun onCleared() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            audioManager.abandonAudioFocus(null)
        }

        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        super.onCleared()
    }

    // Pausa sin cambiar el estado deseado del usuario (_isMusicPlaying)
    fun systemPauseMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    fun loadNewMusic(context: Context, uri: Uri) {
        abandonFocusInternal()

        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        _currentMusicUri.value = uri

        try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")

            parcelFileDescriptor?.use { pfd ->

                val mp = MediaPlayer()
                mp.setDataSource(pfd.fileDescriptor)
                mp.prepare()

                initMediaPlayer(mp, context)
                _isMusicPlaying.value = true
                startMusic(context)
            }
        } catch (e: Exception) {
            Log.e("MusicViewModel", "Error al cargar la música desde URI: $e")
        }
    }

    fun requestAudioFocus(context: Context): Boolean {
        // Definición de qué tipo de audio estamos reproduciendo (música, juego, etc.)
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA) // Uso: música o medios
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC) // Contenido: Música
            .build()

        // Manejador que el sistema llamará cuando el foco cambie (ver paso 1.B)
        val focusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    // Detener la reproducción
                    systemPauseMusic()
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    mediaPlayer?.setVolume(0.1f, 0.1f) // Bajar volumen (ducking)
                }
                AudioManager.AUDIOFOCUS_GAIN -> {
                    mediaPlayer?.setVolume(0.3f, 0.3f) // Restaurar volumen original
                    handler.postDelayed({
                        if (mediaPlayer?.isPlaying == false && _isMusicPlaying.value) {
                            mediaPlayer?.start() // Reanudar
                        }
                    }, 250)

                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Para API 26 y superior: Usar AudioFocusRequest
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setOnAudioFocusChangeListener(focusChangeListener)
                .build()

            val result = audioManager.requestAudioFocus(audioFocusRequest!!)
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            // Para API 25 e inferior: Usar el metodo obsoleto, pero necesario por compatibilidad
            val result = audioManager.requestAudioFocus(
                focusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }
}