package com.example.piedraPapelTijeras.ui.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.piedraPapelTijeras.R
import com.example.piedraPapelTijeras.data.model.EnumElegirJugada
import com.example.piedraPapelTijeras.ui.AgregarBoton
import com.example.piedraPapelTijeras.ui.componentes.AgregarSurface
import com.example.piedraPapelTijeras.viewmodel.JuegoViewModel


@Composable

fun PantallaJuego(juegoViewModel: JuegoViewModel, navController: NavHostController){

    var jugadaJugador: EnumElegirJugada by remember { mutableStateOf(EnumElegirJugada.PIEDRA) }

    val jugadaMaquina = juegoViewModel.jugadaMaquina.collectAsState().value
    val puntuacion by juegoViewModel.puntuacion.collectAsState()
    val mostrarDialogo by juegoViewModel.mostrarDialogo.collectAsState()
    val resultado by juegoViewModel.resultado.collectAsState()


    Column(
        modifier = Modifier.Companion
            .fillMaxSize()
            .background(Color(0xFFA8E6CF))
            .padding(10.dp, 80.dp, 10.dp, 10.dp),

        horizontalAlignment = Alignment.Companion.CenterHorizontally,

    ) {
        //puntuacion y moneda
        Image(
            painter = painterResource(R.drawable.coins),
            contentDescription = null,
            modifier = Modifier.size(120.dp)

        )
        Text(
            text = puntuacion.toString(),
            fontSize = 50.sp

        )



        // Text elegir jugada
        Spacer(modifier = Modifier.Companion.height(50.dp))

        Text(
            text = "Elige tu jugada",
            fontSize = 50.sp
        )


        Spacer(modifier = Modifier.Companion.height(30.dp))

        //eleccion jugada piedra papel o tijeras
        Row(
            modifier = Modifier.Companion.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically

        ) {
            AgregarSurface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(50.dp),
                color = Color.Red,
                imagen = painterResource(R.drawable.piedra),
                textdes = stringResource(R.string.piedra_text_desc),
                seleccionado = jugadaJugador == EnumElegirJugada.PIEDRA,
                onClick = { jugadaJugador = EnumElegirJugada.PIEDRA }

            )
            AgregarSurface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(50.dp),
                color = Color.Red,
                imagen = painterResource(R.drawable.papel),
                textdes = stringResource(R.string.papel_text_desc),
                seleccionado = jugadaJugador == EnumElegirJugada.PAPEL,
                onClick = { jugadaJugador = EnumElegirJugada.PAPEL }

            )
            AgregarSurface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(50.dp),
                color = Color.Red,
                imagen = painterResource(R.drawable.tijeras),
                textdes = stringResource(R.string.tijeras_text_desc),
                seleccionado = jugadaJugador == EnumElegirJugada.TIJERA,
                onClick = { jugadaJugador = EnumElegirJugada.TIJERA }

            )

        }

        Spacer(modifier = Modifier.Companion.height(15.dp))
        //Texto un dos tre piedra papel....
        Text(
            text = stringResource(R.string.un_dos_tres),
            fontSize = 30.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.Companion.height(30.dp))
        //Boton jugar
        AgregarBoton(

            onclick = {juegoViewModel.jugar(jugadaJugador) },
            icon = null,
            des = stringResource(R.string.jugar_desc),
            text = stringResource(R.string.tres),
            fontsize = 40,
            modifier = Modifier.Companion.width(200.dp)
        )

        // Boton volver
        AgregarBoton(

            onclick = { navController.popBackStack() },
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            des = "Volver",
            text = "Volver",
            fontsize = 15,
            modifier = Modifier.padding(top = 16.dp).width(150.dp)
        )

        Spacer(modifier = Modifier.Companion.height(30.dp))
        //Texto jugada maquina
        Text(
            text = stringResource(R.string.jugada_maquina),
            fontSize = 30.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.Companion.height(10.dp))
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
            onClick = {  }

        )

        if (mostrarDialogo) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { },
                title = { Text("Resultado") },
                text = { Text(
                    "$resultado",
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center
                ) },
                containerColor = when (resultado) {
                    "GANASTES" -> Color.Cyan
                    "PERDISTES" -> Color.Yellow
                    else -> Color.White

                },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = { juegoViewModel.cerrarDialogo() }
                    ) {
                        Text("Continuar")
                    }
                }
            )
        }






    }

}






