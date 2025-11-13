package com.example.piedraPapelTijeras.viewmodel

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MusicViewModel : ViewModel() {

    private val _isMusicPlaying = MutableStateFlow(true)
    val isMusicPlaying: StateFlow<Boolean> = _isMusicPlaying

    private var mediaPlayer: MediaPlayer? = null

    fun initsetMediaPlayer(mp: MediaPlayer) {
        mediaPlayer = mp
        // Asegurarse de que esté en bucle (looping)
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
}