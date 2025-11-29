package com.example.piedraPapelTijeras.ui.pantallas

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpCenter
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.piedraPapelTijeras.R
import com.example.piedraPapelTijeras.data.model.Idioma
import com.example.piedraPapelTijeras.ui.AgregarBoton
import com.example.piedraPapelTijeras.ui.componentes.CambiarBotonMusica
import com.example.piedraPapelTijeras.ui.util.SoundPlayer
import com.example.piedraPapelTijeras.ui.util.localizedString
import com.example.piedraPapelTijeras.viewmodel.LanguageViewModel
import com.example.piedraPapelTijeras.viewmodel.MusicViewModel

@Composable
fun PantallaPrincipal(
    navController: NavHostController,
    musicViewModel: MusicViewModel,
    soundPlayer: SoundPlayer,
    languageViewModel: LanguageViewModel
) {

    val context = LocalContext.current
    val idioma by languageViewModel.idiomaActual.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let { musicViewModel.loadNewMusic(context, it) }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFA8E6CF))
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp, 40.dp, 10.dp, 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AgregarBoton(
                onclick = { languageViewModel.toggleLanguage() },
                icon = null,
                des = localizedString(
                    if (idioma == Idioma.ES) R.string.cambiar_idioma
                    else R.string.change_language
                ),
                text = localizedString(
                    if (idioma == Idioma.ES) R.string.cambiar_idioma
                    else R.string.change_language
                ),
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .width(220.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CambiarBotonMusica(musicViewModel = musicViewModel)

                Spacer(modifier = Modifier.width(20.dp))

                AgregarBoton(
                    onclick = { launcher.launch(arrayOf("audio/*")) },
                    icon = Icons.Default.Audiotrack,
                    des = localizedString(R.string.cambiar_musica_des),
                    text = localizedString(R.string.cambiar_musica_text),
                    modifier = Modifier.width(170.dp)
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            Image(
                painter = painterResource(R.drawable.image_inicio),
                contentDescription = localizedString(R.string.imagen_inicio_desc),
                modifier = Modifier
                    .fillMaxWidth()
                    .size(300.dp)
            )

            Spacer(modifier = Modifier.height(50.dp))

            AgregarBoton(
                onclick = {
                    soundPlayer.playSounds(soundPlayer.sonidoBotonId)
                    navController.navigate("login")
                },
                icon = Icons.AutoMirrored.Filled.Send,
                des = localizedString(R.string.jugar_desc),
                text = localizedString(R.string.jugar_text),
                modifier = Modifier.width(200.dp)
            )

            Spacer(modifier = Modifier.height(50.dp))

            AgregarBoton(
                onclick = {
                    soundPlayer.playSounds(soundPlayer.sonidoBotonId)
                    navController.navigate("top10")
                },
                icon = Icons.AutoMirrored.Filled.Send,
                des = localizedString(R.string.top_10_desc),
                text = localizedString(R.string.top_10_text),
                modifier = Modifier.width(200.dp)
            )

            Spacer(modifier = Modifier.height(50.dp))

            AgregarBoton(
                onclick = {
                    soundPlayer.playSounds(soundPlayer.sonidoBotonId)
                    navController.navigate("ayuda")
                },
                icon = Icons.AutoMirrored.Filled.HelpCenter,
                des = localizedString(R.string.ayuda_desc),
                text = localizedString(R.string.ayuda_text),
                modifier = Modifier.width(200.dp)
            )
        }
    }
}
