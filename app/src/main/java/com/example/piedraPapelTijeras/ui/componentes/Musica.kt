package com.example.piedraPapelTijeras.ui.componentes

import kotlin.Triple
import android.media.MediaPlayer
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.piedraPapelTijeras.R
import com.example.piedraPapelTijeras.ui.AgregarBoton
import com.example.piedraPapelTijeras.viewmodel.MusicViewModel

@Composable
fun BackgroundMusicPlayer(musicViewModel: MusicViewModel) {
    val context = LocalContext.current
    val isPlaying by musicViewModel.isMusicPlaying.collectAsState()

    DisposableEffect(Unit) {
        val mp = MediaPlayer.create(context, R.raw.musica_fondo)
        mp.setVolume(0.5f, 0.5f)
        musicViewModel.initMediaPlayer(mp)

        if (isPlaying) {
            musicViewModel.startMusic()
        }

        onDispose {

        }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            musicViewModel.startMusic()
        } else {
            musicViewModel.pauseMusic()
        }
    }
}

@Composable
fun CambiarBotonMusica(musicViewModel: MusicViewModel) {
    val isPlaying by musicViewModel.isMusicPlaying.collectAsState()

    val (icon, description, text) = if (isPlaying) {
        Triple(Icons.Filled.MusicNote, "Pausar Música", "OFF")
    } else {
        Triple(Icons.Filled.MusicOff, "Reproducir Música", "ON")
    }

    // 2. Resolver referencia de AgregarBoton
    AgregarBoton(
        onclick = { musicViewModel.toggleMusic() },
        icon = icon,
        des = description,
        text = text,
        fontsize = 20,
        modifier = Modifier.width(120.dp)
    )

}
