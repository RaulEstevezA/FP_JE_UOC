package com.example.piedraPapelTijeras.ui.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.piedraPapelTijeras.R
import com.example.piedraPapelTijeras.ui.AgregarBoton
import com.example.piedraPapelTijeras.ui.util.CambiarBotonMusica
import com.example.piedraPapelTijeras.ui.util.SoundPlayer
import com.example.piedraPapelTijeras.viewmodel.MusicViewModel


@Composable
fun PantallaPrincipal(
    navController: NavHostController,
    musicViewModel: MusicViewModel,
    soundPlayer: SoundPlayer
) {

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color(0xFFA8E6CF))
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 40.dp)
        ) {
            CambiarBotonMusica(musicViewModel = musicViewModel)
        }

        Column(
            modifier = Modifier.Companion.fillMaxSize()
                .padding(10.dp, 100.dp, 10.dp, 10.dp),

            horizontalAlignment = Alignment.Companion.CenterHorizontally,


            ) {
            Spacer(modifier = Modifier.Companion.height(25.dp))
            // pantalla inicio
            Image(
                painter = painterResource(R.drawable.image_inicio),
                contentDescription = null,
                modifier = Modifier.Companion.fillMaxWidth().size(300.dp),
            )
            Spacer(modifier = Modifier.Companion.height(50.dp))
            //Boton jugar to Login
            AgregarBoton(
                onclick = {
                    soundPlayer.playSounds(soundPlayer.sonidoBotonId)
                    navController.navigate("login")
                },
                icon = Icons.AutoMirrored.Filled.Send,
                des = stringResource(R.string.jugar_desc),
                text = stringResource(R.string.jugar_text),
                modifier = Modifier.Companion.width(200.dp)
            )

            Spacer(modifier = Modifier.Companion.height(50.dp))
            //Boton Top10
            AgregarBoton(
                onclick = {
                    soundPlayer.playSounds(soundPlayer.sonidoBotonId)
                    navController.navigate("top10")
                },
                icon = Icons.AutoMirrored.Filled.Send,
                des = stringResource(R.string.jugar_desc),
                text = stringResource(R.string.top_10_text),
                modifier = Modifier.Companion.width(200.dp)
            )


        }
    }
}






