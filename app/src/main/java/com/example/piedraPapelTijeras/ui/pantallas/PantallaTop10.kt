package com.example.piedraPapelTijeras.ui.pantallas

import com.example.piedraPapelTijeras.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.piedraPapelTijeras.ui.AgregarBoton
import com.example.piedraPapelTijeras.ui.util.SoundPlayer
import com.example.piedraPapelTijeras.viewmodel.MusicViewModel
import com.example.piedraPapelTijeras.ui.util.formatTimestamp
import com.example.piedraPapelTijeras.viewmodel.Top10Viewmodel

@Composable
fun PantallaTop10(
    top10ViewModel: Top10Viewmodel,
    navController: NavHostController,
    musicViewModel: MusicViewModel,
    soundPlayer: SoundPlayer
) {

    LaunchedEffect(key1 = Unit) {
        top10ViewModel.cargarTop10()
    }

    val top10 by top10ViewModel.top10.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFA8E6CF))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Título traducido
        Text(
            text = stringResource(R.string.ranking_titulo),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Lista de jugadores
        top10.forEachIndexed { index, jugador ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {

                    // Nombre
                    Text(
                        text = "${index + 1}. ${jugador.mail}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Último login traducido
                    Text(
                        text = stringResource(R.string.ultimo_login, formatTimestamp(jugador.ultimaFecha)),
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }

                // Puntuación
                Text(
                    text = jugador.puntuacion.toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Divider()
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Botón volver
        AgregarBoton(
            onclick = {
                soundPlayer.playSounds(soundPlayer.sonidoBotonId)
                navController.popBackStack()
            },
            icon = null,
            des = stringResource(R.string.volver_text_desc),
            text = stringResource(R.string.volver_text),
            fontsize = 40,
            modifier = Modifier.width(200.dp)
        )
    }
}










