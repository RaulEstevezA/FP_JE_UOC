package com.example.piedraPapelTijeras.ui.pantallas

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.piedraPapelTijeras.R
import com.example.piedraPapelTijeras.data.model.EnumElegirJugada
import com.example.piedraPapelTijeras.ui.AgregarBoton
import com.example.piedraPapelTijeras.ui.componentes.AgregarSurface
import com.example.piedraPapelTijeras.ui.util.CambiarBotonMusica
import com.example.piedraPapelTijeras.ui.util.SoundPlayer
import com.example.piedraPapelTijeras.viewmodel.JuegoViewModel
import com.example.piedraPapelTijeras.viewmodel.MusicViewModel


@Composable

fun PantallaJuego(
    juegoViewModel: JuegoViewModel,
    navController: NavHostController,
    musicViewModel: MusicViewModel,
    soundPlayer: SoundPlayer
) {

    var jugadaJugador: EnumElegirJugada by remember { mutableStateOf(EnumElegirJugada.PIEDRA) }
    val jugadaMaquina = juegoViewModel.jugadaMaquina.collectAsState().value
    val puntuacion by juegoViewModel.puntuacion.collectAsState()
    val resultado by juegoViewModel.resultado.collectAsState()
    val juegoEnCurso by juegoViewModel.juegoEnCurso.collectAsState()


    Box(
        modifier = Modifier
            .fillMaxSize()
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
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp, 80.dp, 10.dp, 10.dp),

            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ){
                //puntuacion y moneda
                Text(
                    text = puntuacion.toString(),
                    fontSize = 50.sp

                )
                Image(
                    painter = painterResource(R.drawable.coins),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)

                )

            }
            //animacion de resultado
            AnimatedVisibility(
                visible = resultado.isNotEmpty(),
                enter = scaleIn(initialScale = 0.5f, animationSpec = tween(500)),
                exit = scaleOut(targetScale = 0.5f, animationSpec = tween(500))
            ) {

                val colorResultado = when (resultado) {
                    "GANASTES" -> {
                        LaunchedEffect("GANASTE") {
                            soundPlayer.playSounds(soundPlayer.sonidoVictoriaId)
                        }
                        Text(
                            text = resultado,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )

                    }
                    "PERDISTES" -> {
                        LaunchedEffect("PERDISTES") {
                            soundPlayer.playSounds(soundPlayer.sonidoDerrotaId)
                        }
                        Text(
                            text = resultado,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC62828)
                        )

                    }
                    "EMPATE" -> {
                        LaunchedEffect("EMPATE") {
                            soundPlayer.playSounds(soundPlayer.sonidoEmpateId)
                        }
                        Text(
                            text = resultado,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF888888)
                        )

                    }
                    else -> Color.Gray
                }



            }



            // Text elegir jugada

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = stringResource(R.string.elige_jugada),
                fontSize = 30.sp
            )


            Spacer(modifier = Modifier.height(30.dp))

            //eleccion jugada piedra papel o tijeras
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically

            ) {
                AgregarSurface(
                    modifier = Modifier
                        .size(100.dp),
                    shape = RoundedCornerShape(50.dp),
                    color = Color.Red,
                    imagen = painterResource(R.drawable.piedra),
                    textdes = stringResource(R.string.piedra_text_desc),
                    seleccionado = jugadaJugador == EnumElegirJugada.PIEDRA,
                    onClick = {
                        jugadaJugador = EnumElegirJugada.PIEDRA
                        soundPlayer.playSounds(soundPlayer.sonidoSeleccionId)
                    }

                )
                AgregarSurface(
                    modifier = Modifier
                        .size(100.dp),
                    shape = RoundedCornerShape(50.dp),
                    color = Color.Red,
                    imagen = painterResource(R.drawable.papel),
                    textdes = stringResource(R.string.papel_text_desc),
                    seleccionado = jugadaJugador == EnumElegirJugada.PAPEL,
                    onClick = {
                        jugadaJugador = EnumElegirJugada.PAPEL
                        soundPlayer.playSounds(soundPlayer.sonidoSeleccionId)
                    }

                )
                AgregarSurface(
                    modifier = Modifier
                        .size(100.dp),
                    shape = RoundedCornerShape(50.dp),
                    color = Color.Red,
                    imagen = painterResource(R.drawable.tijeras),
                    textdes = stringResource(R.string.tijeras_text_desc),
                    seleccionado = jugadaJugador == EnumElegirJugada.TIJERA,
                    onClick = {
                        jugadaJugador = EnumElegirJugada.TIJERA
                        soundPlayer.playSounds(soundPlayer.sonidoSeleccionId)
                    }

                )

            }

            Spacer(modifier = Modifier.height(15.dp))
            //Texto un dos tre piedra papel....
            Text(
                text = stringResource(R.string.un_dos_tres),
                fontSize = 30.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            //Boton jugar
            AgregarBoton(

                onclick = {
                    juegoViewModel.jugar(jugadaJugador)
                    soundPlayer.playSounds(soundPlayer.sonidoBotonId)
                },

                icon = null,
                des = stringResource(R.string.jugar_desc),
                text = stringResource(R.string.tres),
                fontsize = 40,
                modifier = Modifier.width(200.dp),
                enabled = !juegoEnCurso,
            )

            // Boton volver
            AgregarBoton(

                onclick = {
                    soundPlayer.playSounds(soundPlayer.sonidoBotonId)
                    navController.popBackStack()
                },
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                des = "Volver",
                text = "Volver",
                fontsize = 15,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .width(150.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))
            //Texto jugada maquina
            Text(
                text = stringResource(R.string.jugada_maquina),
                fontSize = 30.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))
            // jugada escogida por la maquina

            val (imagen, textdes) = when (jugadaMaquina) {

                EnumElegirJugada.PIEDRA -> painterResource(R.drawable.piedra) to stringResource(R.string.piedra_text_desc)
                EnumElegirJugada.PAPEL -> painterResource(R.drawable.papel) to stringResource(R.string.papel_text_desc)
                EnumElegirJugada.TIJERA -> painterResource(R.drawable.tijeras) to stringResource(R.string.tijeras_text_desc)
                null -> painterResource(R.drawable.interrogante) to stringResource(R.string.interrogante_text_desc)
            }


            AgregarSurface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(50.dp),
                color = Color.Red,
                imagen = imagen,
                textdes = textdes,
                seleccionado = false,
                onClick = { }

            )






        }

    }
}






