package com.example.piedraPapelTijeras.viewmodel

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MusicViewModel : ViewModel() {

    private val _isMusicPlaying = MutableStateFlow(true)
    val isMusicPlaying: StateFlow<Boolean> = _isMusicPlaying

    private var mediaPlayer: MediaPlayer? = null

    private val _currentMusicUri = MutableStateFlow<Uri?>(null)
    val currentMusicUri: StateFlow<Uri?> = _currentMusicUri.asStateFlow()

    fun initMediaPlayer(mp: MediaPlayer) {
        mediaPlayer = mp
        mediaPlayer?.isLooping = true
    }

    fun toggleMusic() {
        val currentlyPlaying = _isMusicPlaying.value
        _isMusicPlaying.value = !currentlyPlaying

        if (currentlyPlaying) {
            pauseMusic()
        } else {
            startMusic()
        }
    }

    fun startMusic() {
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
        _isMusicPlaying.value = true
    }

    fun pauseMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
        _isMusicPlaying.value = false
    }

    override fun onCleared() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        super.onCleared()
    }

    fun loadNewMusic(context: Context, uri: Uri) {
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

                initMediaPlayer(mp)
                startMusic()
            }
        } catch (e: Exception) {
            Log.e("MusicViewModel", "Error al cargar la música desde URI: $e")
        }
    }
}