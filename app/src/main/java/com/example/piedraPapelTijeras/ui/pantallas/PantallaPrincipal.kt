package com.example.piedraPapelTijeras.ui.pantallas

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.automirrored.filled.HelpCenter
import androidx.compose.material.icons.filled.Audiotrack
import com.example.piedraPapelTijeras.R
import com.example.piedraPapelTijeras.ui.AgregarBoton
import com.example.piedraPapelTijeras.ui.componentes.CambiarBotonMusica
import com.example.piedraPapelTijeras.ui.util.SoundPlayer
import com.example.piedraPapelTijeras.viewmodel.MusicViewModel


@Composable
fun PantallaPrincipal(
    navController: NavHostController,
    musicViewModel: MusicViewModel,
    soundPlayer: SoundPlayer
) {

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                musicViewModel.loadNewMusic(context, it)
            }
        }
    )

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color(0xFFA8E6CF))
    ) {
        Column(
            modifier = Modifier.Companion.fillMaxSize()
                .padding(10.dp, 100.dp, 10.dp, 10.dp),

            horizontalAlignment = Alignment.Companion.CenterHorizontally,

            ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.Center, // CENTRAR ambos botones
                verticalAlignment = Alignment.CenterVertically
            ) {
                CambiarBotonMusica(musicViewModel = musicViewModel)

                Spacer(modifier = Modifier.width(20.dp))

                //Boton Cambiar Musica
                AgregarBoton(
                    onclick = { launcher.launch(arrayOf("audio/*")) },
                    icon = Icons.Default.Audiotrack,
                    des = stringResource(R.string.cambiar_musica_des),
                    text = stringResource(R.string.cambiar_musica_text),
                    modifier = Modifier.Companion.width(170.dp)
                )
            }

            Spacer(modifier = Modifier.Companion.height(25.dp))
            // pantalla inicio
            Image(
                painter = painterResource(R.drawable.image_inicio),
                contentDescription = stringResource(R.string.imagen_inicio_desc),
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
                des = stringResource(R.string.top_10_desc),
                text = stringResource(R.string.top_10_text),
                modifier = Modifier.Companion.width(200.dp)
            )

            Spacer(modifier = Modifier.Companion.height(50.dp))
            //Boton ayuda
            AgregarBoton(
                onclick = {
                    soundPlayer.playSounds(soundPlayer.sonidoBotonId)
                    navController.navigate("ayuda")
                },
                icon = Icons.AutoMirrored.Filled.HelpCenter,
                des = stringResource(R.string.ayuda_desc),
                text = stringResource(R.string.ayuda_text),
                modifier = Modifier.Companion.width(200.dp)
            )


        }
    }
}






