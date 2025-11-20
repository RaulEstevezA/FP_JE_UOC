package com.example.piedraPapelTijeras


import android.os.Bundle
import androidx

.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.piedraPapelTijeras.ui.util.BackgroundMusicPlayer
import com.example.piedraPapelTijeras.ui.pantallas.PantallaJuego
import com.example.piedraPapelTijeras.ui.pantallas.PantallaLogin
import com.example.piedraPapelTijeras.ui.pantallas.PantallaPrincipal
import com.example.piedraPapelTijeras.ui.pantallas.PantallaTop10
import com.example.piedraPapelTijeras.viewmodel.JuegoViewModel
import com.example.piedraPapelTijeras.viewmodel.LoginViewModel
import com.example.piedraPapelTijeras.viewmodel.MusicViewModel
import com.example.piedraPapelTijeras.viewmodel.Top10Viewmodel
import com.example.piedraPapelTijeras.ui.util.SoundPlayer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    val top10ViewModel: Top10Viewmodel = viewModel(

                        factory = Injeccion.provideTop10ViewModelFactory(context = applicationContext)
                    )

                    val juegoViewModel: JuegoViewModel = viewModel(

                        factory = Injeccion.provideJuegoViewModelFactory(context = applicationContext, top10ViewModel = top10ViewModel)
                    )

                    val loginViewModel: LoginViewModel = viewModel(

                        factory = Injeccion.provideLoginViewModelFactory(context = applicationContext)
                    )
                    //Para la musica de fondo
                    val musicViewModel: MusicViewModel = viewModel()

                    BackgroundMusicPlayer(musicViewModel = musicViewModel)

                    //Para los efectos de sonido de eleccion de jugada
                    //Creamos una una instancia de SoundPlayer en el contexto de la actividad
                    val soundPlayer = remember {SoundPlayer(applicationContext)}

                    //cuando cerramos nos aseguramos de limpiar memoria
                    DisposableEffect(Unit) {
                        onDispose {
                            soundPlayer.release()
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = "principal"
                    ) {
                        composable("principal") {
                            PantallaPrincipal(
                                navController = navController,
                                musicViewModel = musicViewModel,
                                soundPlayer = soundPlayer

                            )
                        }
                        composable("login") {
                            PantallaLogin(
                                loginViewModel = loginViewModel,
                                top10ViewModel = top10ViewModel,
                                navController = navController,
                                musicViewModel = musicViewModel,
                                soundPlayer = soundPlayer
                            )

                        }
                        composable("juego") {
                            PantallaJuego(
                                juegoViewModel = juegoViewModel,
                                navController = navController,
                                musicViewModel = musicViewModel,
                                soundPlayer = soundPlayer
                            )
                        }
                        composable("top10") {
                            PantallaTop10(
                                top10ViewModel = top10ViewModel,
                                navController = navController,
                                musicViewModel = musicViewModel,
                                soundPlayer = soundPlayer
                            )
                        }

                    }
                }
            }

        }
    }
}



