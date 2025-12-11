package com.example.piedraPapelTijeras.viewmodel

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MusicViewModel : ViewModel() {

    private lateinit var audioManager: AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null

    // Estado de si el usuario quiere música o no (botón mute)
    private val _isMusicPlaying = MutableStateFlow(true)
    val isMusicPlaying: StateFlow<Boolean> = _isMusicPlaying

    // Estado de si la música se ha pausado por el sistema (llamada, app al fondo, etc.)
    private val _wasSystemPaused = MutableStateFlow(false)
    val wasSystemPaused: StateFlow<Boolean> = _wasSystemPaused

    private var mediaPlayer: MediaPlayer? = null

    private val _currentMusicUri = MutableStateFlow<Uri?>(null)
    val currentMusicUri: StateFlow<Uri?> = _currentMusicUri.asStateFlow()

    private val handler = Handler(Looper.getMainLooper())

    fun initMediaPlayer(mp: MediaPlayer, context: Context) {
        mediaPlayer = mp
        mediaPlayer?.isLooping = true
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    /**
     * Botón de silenciar / reanudar música.
     * Si estaba sonando -> pausa manual del usuario.
     * Si estaba parada -> reanuda si el usuario quiere música.
     */
    fun toggleMusic(context: Context) {
        val currentlyPlaying = _isMusicPlaying.value

        if (currentlyPlaying) {
            // El usuario decide parar la música
            pauseMusic(context)
            _isMusicPlaying.value = false
            _wasSystemPaused.value = false
        } else {
            // El usuario decide que quiere música
            _isMusicPlaying.value = true
            _wasSystemPaused.value = false
            startMusic(context)
        }
    }

    /**
     * Inicia la música respetando el audio focus.
     */
    fun startMusic(context: Context) {
        Log.d("MUSIC-DEBUG", "startMusic() llamado")
        val canPlay = requestAudioFocus(context)
        Log.d("MUSIC-DEBUG", "¿AudioFocus concedido?: $canPlay")

        if (canPlay) {
            if (mediaPlayer?.isPlaying == false) {
                Log.d("MUSIC-DEBUG", "MediaPlayer.start() ejecutado")
                mediaPlayer?.start()
            }
            _isMusicPlaying.value = true
        } else {
            Log.d("MUSIC-DEBUG", "NO se pudo reproducir música: AudioFocus DENEGADO")
        }
    }

    /**
     * Pausa la música por decisión del usuario (mute).
     */
    fun pauseMusic(context: Context) {
        abandonFocusInternal()
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
        // Aquí NO tocamos wasSystemPaused porque esto es pausa manual
    }

    /**
     * Pausa causada por el sistema (onPause, llamada, alarma…).
     * No toca isMusicPlaying para saber que el usuario quería música.
     */
    fun systemPauseMusic() {
        Log.w("MUSIC-DEBUG", "systemPauseMusic() ejecutado")
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            Log.d("MUSIC-DEBUG", "MediaPlayer pausado por sistema")
        }
        _wasSystemPaused.value = true
    }

    /**
     * Reanudar después de una pausa del sistema,
     * solo si el usuario quería música.
     */
    fun resumeAfterSystemPause(context: Context) {
        if (_wasSystemPaused.value && _isMusicPlaying.value) {
            _wasSystemPaused.value = false
            startMusic(context)
        }
    }

    private fun abandonFocusInternal() {
        if (::audioManager.isInitialized) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
            } else {
                @Suppress("DEPRECATION")
                audioManager.abandonAudioFocus(null)
            }
        }
    }

    /**
     * Cargar nueva música desde el móvil.
     */
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
                _wasSystemPaused.value = false
                startMusic(context)
            }
        } catch (e: Exception) {
            Log.e("MusicViewModel", "Error al cargar la música desde URI: $e")
        }
    }

    /**
     * Gestiona el audio focus para llamadas, alarmas, etc.
     */
    fun requestAudioFocus(context: Context): Boolean {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        val focusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->

            Log.d("MUSIC-FOCUS", "Cambio de foco: $focusChange")

            when (focusChange) {

                AudioManager.AUDIOFOCUS_LOSS,
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    Log.d("MUSIC-FOCUS", "→ El sistema quita el foco. Pausa de sistema.")
                    systemPauseMusic()
                }

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    Log.d("MUSIC-FOCUS", "→ Ducking (bajar volumen temporalmente)")
                    mediaPlayer?.setVolume(0.1f, 0.1f)
                }

                AudioManager.AUDIOFOCUS_GAIN -> {
                    Log.d("MUSIC-FOCUS", "→ Recuperamos el foco de audio")
                    mediaPlayer?.setVolume(0.3f, 0.3f)

                    handler.postDelayed({
                        if (_wasSystemPaused.value && _isMusicPlaying.value) {
                            Log.d("MUSIC-FOCUS", "→ Reanudando música tras pausa del sistema")
                            resumeAfterSystemPause(context)
                        }
                    }, 250)
                }
            }
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setOnAudioFocusChangeListener(focusChangeListener)
                .build()

            audioManager.requestAudioFocus(audioFocusRequest!!) ==
                    AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                focusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }

    override fun onCleared() {
        abandonFocusInternal()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        super.onCleared()
    }
}
