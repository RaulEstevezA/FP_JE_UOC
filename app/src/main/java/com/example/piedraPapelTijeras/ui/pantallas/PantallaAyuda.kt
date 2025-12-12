package com.example.piedraPapelTijeras.ui.pantallas

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalConfiguration
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.piedraPapelTijeras.R
import com.example.piedraPapelTijeras.ui.AgregarBoton
import com.example.piedraPapelTijeras.ui.util.SoundPlayer
import com.example.piedraPapelTijeras.viewmodel.MusicViewModel

//Hemos creado una pantalla ayuda hibrida por una lado usamos la parte nativa
//Compose y el resto lo usamos desde un HTML de esta forma a modo didactico tocamos
//Las dos opiocnes diponibles.

@Composable
fun PantallaAyuda(
    navController: NavController,
    musicViewModel: MusicViewModel,
    soundPlayer: SoundPlayer
) {
    //ruta al archivo local html
    val idioma = LocalConfiguration.current.locales[0].language

    val urlLocal = when (idioma) {
        "en" -> "file:///android_asset/ayuda_en.html"
        else -> "file:///android_asset/ayuda_es.html"
    }

    //Columna que organiza la visulizacion de la parte Nativa
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFA8E6CF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, start = 16.dp,end=16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Fila para el boton de volver
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.volver_text_desc),
                        tint = Color(0xFF004D40),
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Column {
                    //titulo
                    Text(
                        text = stringResource(R.string.ayuda),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF004D40)

                    )
                    Text(
                        text = stringResource(R.string.reglas),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            //Zona de la parte HTML
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White)
            ) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )

                            webViewClient = WebViewClient()

                            // Cargamos el archivo
                            loadUrl(urlLocal)
                        }
                    }
                )

            }

        }

    }
}