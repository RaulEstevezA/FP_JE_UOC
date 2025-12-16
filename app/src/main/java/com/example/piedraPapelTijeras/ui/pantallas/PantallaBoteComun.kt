package com.example.piedraPapelTijeras.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.piedraPapelTijeras.R
import com.example.piedraPapelTijeras.ui.AgregarBoton
import com.example.piedraPapelTijeras.ui.util.SoundPlayer
import com.example.piedraPapelTijeras.ui.util.localizedString
import com.example.piedraPapelTijeras.viewmodel.BoteComunViewModel

@Composable
fun PantallaBoteComun(
    navController: NavHostController,
    viewModel: BoteComunViewModel,
    soundPlayer: SoundPlayer,
) {
    val bote by viewModel.bote.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarBote()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFA8E6CF))
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp, 140.dp, 10.dp, 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // TÍTULO
            Text(
                text = localizedString(R.string.ver_bote),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // VALOR DEL BOTE
            Text(
                text = localizedString(R.string.bote_actual, bote),
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // BOTÓN VOLVER (igual que en Login)
            AgregarBoton(
                onclick = {
                    soundPlayer.playSounds(soundPlayer.sonidoBotonId)
                    navController.popBackStack()
                },
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                des = localizedString(R.string.volver_text_desc),
                text = localizedString(R.string.volver_text),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .width(150.dp)
            )
        }
    }
}