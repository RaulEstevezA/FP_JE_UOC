package com.example.piedraPapelTijeras.ui.pantallas

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.piedraPapelTijeras.R
import com.example.piedraPapelTijeras.data.model.EnumElegirJugada
import com.example.piedraPapelTijeras.data.model.EnumResultado
import com.example.piedraPapelTijeras.ui.AgregarBoton
import com.example.piedraPapelTijeras.ui.componentes.AgregarSurface
import com.example.piedraPapelTijeras.ui.util.CambiarBotonMusica
import com.example.piedraPapelTijeras.ui.util.SoundPlayer
import com.example.piedraPapelTijeras.ui.util.localizedString
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
    var jugadaJugador by remember { mutableStateOf(EnumElegirJugada.PIEDRA) }
    val jugadaMaquina = juegoViewModel.jugadaMaquina.collectAsState().value
    val puntuacion by juegoViewModel.puntuacion.collectAsState()
    val resultado by juegoViewModel.resultado.collectAsState()
    val juegoEnCurso by juegoViewModel.juegoEnCurso.collectAsState()

    //bote en tiempo real
    val bote by juegoViewModel.bote.collectAsState()
    //REST
    val resultadoRest by juegoViewModel.resultadoRest.collectAsState()

    val capturarPantalla = rememberCaptureController()
    val context = LocalContext.current

    val permisosUbicacion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permisos ->
            val aceptado = permisos.values.all { it }
            if (aceptado) {
                juegoViewModel.actualizarUbicacion(context)
            }
        }
    )

    LaunchedEffect(Unit) {
        permisosUbicacion.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Capturable(
        controller = capturarPantalla,
        modifier = Modifier.fillMaxSize(),
        onCaptured = { imageBitmap, error ->
            if (imageBitmap != null) {
                salvarFotoAGaleria(
                    context = context,
                    bitmap = imageBitmap,
                    displayName = "Gane_${System.currentTimeMillis()}"
                )
            }
            if (error != null) {
                Log.e("CapturaPantalla", "Error al capturar pantalla", error)
            }
        }
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFA8E6CF))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CambiarBotonMusica(musicViewModel = musicViewModel)
                    Spacer(modifier = Modifier.weight(1f))

                    AgregarBoton(
                        onclick = {
                            soundPlayer.playSounds(soundPlayer.sonidoBotonId)
                            navController.popBackStack()
                        },
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        des = localizedString(R.string.volver_text_desc),
                        text = localizedString(R.string.volver_text),
                        fontsize = 12,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .width(130.dp)
                    )
                }

                // MARCADOR (PUNTOS Y BOTE)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Puntos del Jugador
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = puntuacion.toString(), fontSize = 40.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Image(
                            painter = painterResource(R.drawable.coins),
                            contentDescription = null,
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    // Bote Acumulado
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD54F)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = localizedString(R.string.bote_text), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                            Text(text = "$bote", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.Black)
                        }
                    }
                }


                AnimatedVisibility(
                    visible = resultado != null,
                    enter = scaleIn(
                        initialScale = 0.5f,
                        animationSpec = tween(500)
                    ),
                    exit = scaleOut(
                        targetScale = 0.5f,
                        animationSpec = tween(500)
                    )
                ) {
                    when (resultado) {

                        EnumResultado.GANASTES -> {
                            LaunchedEffect(Unit) {
                                soundPlayer.playSounds(soundPlayer.sonidoVictoriaId)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = localizedString(R.string.ganastes),
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )

                            }
                        }

                        EnumResultado.PERDISTES -> {
                            LaunchedEffect(Unit) {
                                soundPlayer.playSounds(soundPlayer.sonidoDerrotaId)
                            }
                            Text(
                                text = localizedString(R.string.perdistes),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC62828)
                            )
                        }

                        EnumResultado.EMPATE -> {
                            LaunchedEffect(Unit) {
                                soundPlayer.playSounds(soundPlayer.sonidoEmpateId)
                            }
                            Text(
                                text = localizedString(R.string.empate),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF888888)
                            )
                        }

                        null -> Unit
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = localizedString(R.string.elige_jugada),
                    fontSize = 30.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                val size = 85.dp

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    AgregarSurface(
                        modifier = Modifier.size(size),
                        shape = RoundedCornerShape(50.dp),
                        color = Color.Red,
                        imagen = painterResource(R.drawable.piedra),
                        textdes = localizedString(R.string.piedra_text_desc),
                        seleccionado = jugadaJugador == EnumElegirJugada.PIEDRA,
                        onClick = {
                            jugadaJugador = EnumElegirJugada.PIEDRA
                            soundPlayer.playSounds(soundPlayer.sonidoSeleccionId)
                        }
                    )

                    AgregarSurface(
                        modifier = Modifier.size(size),
                        shape = RoundedCornerShape(50.dp),
                        color = Color.Red,
                        imagen = painterResource(R.drawable.papel),
                        textdes = localizedString(R.string.papel_text_desc),
                        seleccionado = jugadaJugador == EnumElegirJugada.PAPEL,
                        onClick = {
                            jugadaJugador = EnumElegirJugada.PAPEL
                            soundPlayer.playSounds(soundPlayer.sonidoSeleccionId)
                        }
                    )

                    AgregarSurface(
                        modifier = Modifier.size(size),
                        shape = RoundedCornerShape(50.dp),
                        color = Color.Red,
                        imagen = painterResource(R.drawable.tijeras),
                        textdes = localizedString(R.string.tijeras_text_desc),
                        seleccionado = jugadaJugador == EnumElegirJugada.TIJERA,
                        onClick = {
                            jugadaJugador = EnumElegirJugada.TIJERA
                            soundPlayer.playSounds(soundPlayer.sonidoSeleccionId)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = localizedString(R.string.un_dos_tres),
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(35.dp))

                AgregarBoton(
                    onclick = {
                        juegoViewModel.jugar(jugadaJugador)
                        soundPlayer.playSounds(soundPlayer.sonidoBotonId)
                    },
                    icon = null,
                    des = localizedString(R.string.jugar_desc),
                    text = localizedString(R.string.tres),
                    fontsize = 40,
                    modifier = Modifier.width(180.dp),
                    enabled = !juegoEnCurso
                )

                AnimatedVisibility(visible = resultado == EnumResultado.GANASTES) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = {
                                    soundPlayer.playSounds(soundPlayer.sonidoFotoId)
                                    capturarPantalla.capture()
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Filled.PhotoCamera,
                                    contentDescription = localizedString(R.string.captura_desc),
                                    tint = Color.DarkGray
                                )
                            }
                            Text(localizedString(R.string.captura_text), fontSize = 12.sp)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = {
                                    val nombre = juegoViewModel.jugadorFirebase.value?.nombre ?: "Jugador"
                                    val puntos = juegoViewModel.puntuacion.value
                                    juegoViewModel.saveWinToCalendar(nombre, puntos)
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Event,
                                    contentDescription = localizedString(R.string.calendar_desc),
                                    tint = Color.DarkGray
                                )
                            }
                            Text(localizedString(R.string.calendar_text), fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = localizedString(R.string.jugada_maquina),
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                val (imagen, textdes) = when (jugadaMaquina) {
                    EnumElegirJugada.PIEDRA ->
                        painterResource(R.drawable.piedra) to localizedString(R.string.piedra_text_desc)

                    EnumElegirJugada.PAPEL ->
                        painterResource(R.drawable.papel) to localizedString(R.string.papel_text_desc)

                    EnumElegirJugada.TIJERA ->
                        painterResource(R.drawable.tijeras) to localizedString(R.string.tijeras_text_desc)

                    else ->
                        painterResource(R.drawable.interrogante) to localizedString(R.string.interrogante_text_desc)
                }

                AgregarSurface(
                    modifier = Modifier.size(90.dp),
                    shape = RoundedCornerShape(50.dp),
                    color = Color.Red,
                    imagen = imagen,
                    textdes = textdes,
                    seleccionado = false,
                    onClick = {}
                )
                // PRUEBA PARA DEMOSTRAR QUE RETROFIT FUNCIONA


                Spacer(modifier = Modifier.height(20.dp))


                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.DarkGray.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    // botón que inicia la llamada REST
                    AgregarBoton(
                        onclick = { juegoViewModel.probarLlamadaRest() },
                        icon = null,
                        des = localizedString(R.string.desc_rest),
                        text = localizedString(R.string.prueba_rest),
                        fontsize = 12,
                        modifier = Modifier.width(150.dp),
                    )

                    // El texto que muestra el resultado

                    if (resultadoRest != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = resultadoRest!!,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
