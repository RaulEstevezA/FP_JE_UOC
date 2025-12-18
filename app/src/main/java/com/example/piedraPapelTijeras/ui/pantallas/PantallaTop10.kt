package com.example.piedraPapelTijeras.ui.pantallas

import com.example.piedraPapelTijeras.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.piedraPapelTijeras.ui.AgregarBoton
import com.example.piedraPapelTijeras.ui.util.SoundPlayer
import com.example.piedraPapelTijeras.viewmodel.MusicViewModel
import com.example.piedraPapelTijeras.ui.util.localizedString
import com.example.piedraPapelTijeras.viewmodel.RankingViewModel

@Composable
fun PantallaTop10(
    //top10ViewModel: Top10Viewmodel,//uso SQL LIte
    rankingViewModel: RankingViewModel,//USO para Firebase
    navController: NavHostController,
    musicViewModel: MusicViewModel,
    soundPlayer: SoundPlayer
) {

    //val top10 by top10ViewModel.top10.collectAsState()//SQL LITE
    val top10 by rankingViewModel.ranking.collectAsState()//FireBASe

    //Variables para premios
    val tituloPremio by rankingViewModel.tituloPremio.collectAsState()
    val descripcionPremio by rankingViewModel.descripcionPremio.collectAsState()

    // Estado para controlar el scroll de la pantalla
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFA8E6CF))
            .verticalScroll(scrollState) // scroll vertical
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Título traducido
        Text(
            text = localizedString(R.string.ranking_titulo),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        //Tarjeta para el premio
        if(tituloPremio.isNotEmpty()){
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF8E1)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ){

                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //  de regalo
                    Text(text = "🎁", fontSize = 36.sp)

                    // Título del premio
                    Text(
                        text = tituloPremio,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100) // Naranja
                    )

                    // Descripción del premio
                    if (descripcionPremio.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = descripcionPremio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }

        // Lista de jugadores

        Column(modifier = Modifier.fillMaxWidth()) {
            top10.forEachIndexed { index, jugador ->
                
                // LÓGICA PARA RESALTAR EL TOP 3
                val (colorFondo, medalla) = when (index) {
                    0 -> Color(0xFFFFD700).copy(alpha = 0.4f) to "🥇 " // Oro
                    1 -> Color(0xFFC0C0C0).copy(alpha = 0.4f) to "🥈 " // Plata
                    2 -> Color(0xFFCD7F32).copy(alpha = 0.4f) to "🥉 " // Bronce
                    else -> Color.Transparent to "" // El resto normal
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(color = colorFondo, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // Nombre con Medalla
                        Text(
                            text = "${index + 1}. $medalla${jugador.nombre}",
                            fontSize = 20.sp,
                            fontWeight = if(index < 3) FontWeight.Bold else FontWeight.SemiBold
                        )
                    }
                    // Puntuación
                    Text(
                        text = jugador.puntuacion.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Línea divisoria solo para los que no son top 3
                if (index >= 3) {
                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.5f))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Botón volver
        AgregarBoton(
            onclick = {
                soundPlayer.playSounds(soundPlayer.sonidoBotonId)
                navController.popBackStack()
            },
            icon = null,
            des = localizedString(R.string.volver_text_desc),
            text = localizedString(R.string.volver_text),
            fontsize = 40,
            modifier = Modifier.width(200.dp)
        )
        

        Spacer(modifier = Modifier.height(20.dp))
    }
}