package com.example.piedraPapelTijeras.ui.pantallas

import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.piedraPapelTijeras.R
import com.example.piedraPapelTijeras.data.model.EnumElegirJugada
import com.example.piedraPapelTijeras.data.model.EnumResultado
import com.example.piedraPapelTijeras.ui.AgregarBoton
import com.example.piedraPapelTijeras.ui.componentes.AgregarSurface
import com.example.piedraPapelTijeras.ui.componentes.CambiarBotonMusica
import com.example.piedraPapelTijeras.ui.util.SoundPlayer
import com.example.piedraPapelTijeras.ui.util.salvarFotoAGaleria
import com.example.piedraPapelTijeras.viewmodel.JuegoViewModel
import com.example.piedraPapelTijeras.viewmodel.MusicViewModel
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController


@Composable

fun PantallaJuego(
    juegoViewModel: JuegoViewModel,
    navController: NavHostController,
    musicViewModel: MusicViewModel,
    soundPlayer: SoundPlayer
) {
    // --ESTADOS DE LA PANTALLA
    var jugadaJugador: EnumElegirJugada by remember { mutableStateOf(EnumElegirJugada.PIEDRA) }
    val jugadaMaquina = juegoViewModel.jugadaMaquina.collectAsState().value
    val puntuacion by juegoViewModel.puntuacion.collectAsState()
    val resultado by juegoViewModel.resultado.collectAsState()
    val juegoEnCurso by juegoViewModel.juegoEnCurso.collectAsState()

    // -- PARA CAPTURA Y PERMISOS
    val capturarPantalla = rememberCaptureController()
    val context = LocalContext.current


    //envolvemos todo en el capturable que es la captura que realizara
    Capturable(
        controller = capturarPantalla,
        modifier = Modifier.fillMaxSize(),
        onCaptured = { imageBitmap, error ->
            // Se ejecuta DESPUÉS de capturar.

            // Comprobamos si la foto (imageBitmap) se ha creado con éxito.
            if (imageBitmap != null) {
                //si se ha creado la llamamos a la herramienta para guardarla
                salvarFotoAGaleria(
                    context = context,
                    bitmap = imageBitmap,
                    //nombre del archivo gane+hora actual
                    displayName = "Gane_${System.currentTimeMillis()}"
                )
            }

            // Comprobamos si ha habido un error.
            if (error != null) {
                // Error. Lo mostramos en el Log para saber qué ha pasado.
                Log.e("CapturaPantalla", "Error al capturar la pantalla", error)
            }
        }

    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFA8E6CF))
        ) { // Un padding general para que no se pegue a los bordes
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //Boton musica on/off
                    CambiarBotonMusica(musicViewModel = musicViewModel)
                    Spacer(modifier = Modifier.weight(1f))
                    //boton volver
                    AgregarBoton(
                        onclick = {
                            soundPlayer.playSounds(soundPlayer.sonidoBotonId)
                            navController.popBackStack()
                        },
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        des = stringResource(R.string.volver_text_desc),
                        text = stringResource(R.string.volver_text),
                        fontsize = 12,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .width(130.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    //puntuacion y moneda
                    Text(
                        text = puntuacion.toString(), fontSize = 50.sp

                    )
                    Image(
                        painter = painterResource(R.drawable.coins),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp)

                    )

                }
                //animacion de resultado
                AnimatedVisibility(
                    visible = resultado != null,
                    enter = scaleIn(initialScale = 0.5f, animationSpec = tween(500)),
                    exit = scaleOut(targetScale = 0.5f, animationSpec = tween(500))
                ) {
                    when (resultado) {

                        EnumResultado.GANASTES -> {
                            LaunchedEffect("ganaste_sound") {
                                soundPlayer.playSounds(soundPlayer.sonidoVictoriaId)
                            }
                            Text(
                                text = stringResource(R.string.ganastes),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }

                        EnumResultado.PERDISTES -> {
                            LaunchedEffect("perdistes_sound") {
                                soundPlayer.playSounds(soundPlayer.sonidoDerrotaId)
                            }
                            Text(
                                text = stringResource(R.string.perdistes),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC62828)
                            )
                        }

                        EnumResultado.EMPATE -> {
                            LaunchedEffect("empate_sound") {
                                soundPlayer.playSounds(soundPlayer.sonidoEmpateId)
                            }
                            Text(
                                text = stringResource(R.string.empate),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF888888)
                            )
                        }

                        null -> Unit  // no mostrar nada
                    }

                }
                // Text elegir jugada

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.elige_jugada), fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                val tamañoBoton = 85.dp

                //eleccion jugada piedra papel o tijeras
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    AgregarSurface(
                        modifier = Modifier.size(tamañoBoton),
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
                        modifier = Modifier.size(tamañoBoton),
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
                        modifier = Modifier.size(tamañoBoton),
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

                Spacer(modifier = Modifier.height(10.dp))
                //Texto un dos tres piedra papel....
                Text(
                    text = stringResource(R.string.un_dos_tres),
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(35.dp))

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
                    modifier = Modifier.width(180.dp),
                    enabled = !juegoEnCurso,
                )
                //Boton captura pantalla aparece al ganar
                AnimatedVisibility(visible = resultado == EnumResultado.GANASTES) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        //Boton Foto
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = {
                                    soundPlayer.playSounds(soundPlayer.sonidoFotoId)
                                    //capturar victoria
                                    capturarPantalla.capture()
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Filled.PhotoCamera,
                                    contentDescription = stringResource(R.string.captura_desc),
                                    tint = Color.DarkGray
                                )
                            }
                            Text(
                                stringResource(R.string.captura_text),
                                fontSize = 12.sp
                            )


                        }
                        //Boton Calendario
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = {
                                    val nombre =
                                        juegoViewModel.jugadorActual.value?.mail ?: "Jugador"
                                    val puntos = juegoViewModel.puntuacion.value
                                    juegoViewModel.saveWinToCalendar(nombre, puntos)
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Event,
                                    contentDescription = stringResource(R.string.calendar_desc),
                                    tint = Color.DarkGray
                                )
                            }
                            Text(
                                stringResource(R.string.calendar_text),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

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

                    EnumElegirJugada.PIEDRA -> painterResource(R.drawable.piedra) to stringResource(
                        R.string.piedra_text_desc
                    )

                    EnumElegirJugada.PAPEL -> painterResource(R.drawable.papel) to stringResource(
                        R.string.papel_text_desc
                    )

                    EnumElegirJugada.TIJERA -> painterResource(R.drawable.tijeras) to stringResource(
                        R.string.tijeras_text_desc
                    )

                    null -> painterResource(R.drawable.interrogante) to stringResource(R.string.interrogante_text_desc)
                }
                AgregarSurface(
                    modifier = Modifier.size(90.dp),
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
}



