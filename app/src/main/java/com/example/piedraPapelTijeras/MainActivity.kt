package com.example.piedraPapelTijeras

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.piedraPapelTijeras.ui.util.BackgroundMusicPlayer
import com.example.piedraPapelTijeras.ui.pantallas.PantallaAyuda
import com.example.piedraPapelTijeras.ui.pantallas.PantallaJuego
import com.example.piedraPapelTijeras.ui.pantallas.PantallaLogin
import com.example.piedraPapelTijeras.ui.pantallas.PantallaPrincipal
import com.example.piedraPapelTijeras.ui.pantallas.PantallaTop10
import com.example.piedraPapelTijeras.ui.util.LocalAppLocale
import com.example.piedraPapelTijeras.ui.util.SoundPlayer
import com.example.piedraPapelTijeras.viewmodel.JuegoViewModel
import com.example.piedraPapelTijeras.viewmodel.LanguageViewModel
import com.example.piedraPapelTijeras.viewmodel.LoginViewModel
import com.example.piedraPapelTijeras.viewmodel.MusicViewModel
import com.example.piedraPapelTijeras.viewmodel.Top10Viewmodel
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var juegoViewModel: JuegoViewModel
    private lateinit var musicViewModel: MusicViewModel
    private val handler = Handler(Looper.getMainLooper())

    // permiso calendario
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("MainActivity", "Permiso WRITE_CALENDAR concedido.")
        } else {
            Log.w("MainActivity", "Permiso WRITE_CALENDAR denegado.")
        }
    }

    // permiso notificaciones (Android 13+)
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("MainActivity", "Permiso POST_NOTIFICATIONS concedido.")
        } else {
            Log.w("MainActivity", "Permiso POST_NOTIFICATIONS denegado.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // pedir permiso calendario
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_CALENDAR)
        }

        // pedir permiso notificaciones en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {

            // viewmodel de idioma
            val languageViewModel: LanguageViewModel = viewModel(
                factory = Injeccion.provideLanguageViewModelFactory(
                    context = applicationContext
                )
            )

            val idiomaState = languageViewModel.idiomaActual.collectAsState()

            // enum → Locale
            val currentLocale =
                if (idiomaState.value.name == "ES") Locale("es") else Locale("en")

            CompositionLocalProvider(
                LocalAppLocale provides currentLocale
            ) {
                MaterialTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {

                        val navController = rememberNavController()

                        val top10ViewModel: Top10Viewmodel = viewModel(
                            factory = Injeccion.provideTop10ViewModelFactory(
                                context = applicationContext
                            )
                        )

                        this@MainActivity.juegoViewModel = viewModel(
                            factory = Injeccion.provideJuegoViewModelFactory(
                                context = applicationContext,
                                top10ViewModel = top10ViewModel
                            )
                        )

                        val loginViewModel: LoginViewModel = viewModel(
                            factory = Injeccion.provideLoginViewModelFactory(
                                context = applicationContext
                            )
                        )

                        this@MainActivity.musicViewModel = viewModel()

                        BackgroundMusicPlayer(musicViewModel = musicViewModel)

                        val soundPlayer = remember { SoundPlayer(applicationContext) }

                        DisposableEffect(Unit) {
                            onDispose { soundPlayer.release() }
                        }

                        NavHost(
                            navController = navController,
                            startDestination = "principal"
                        ) {
                            composable("principal") {
                                PantallaPrincipal(
                                    navController = navController,
                                    musicViewModel = musicViewModel,
                                    soundPlayer = soundPlayer,
                                    languageViewModel = languageViewModel
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
                            composable("ayuda") {
                                PantallaAyuda(
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

    override fun onPause() {
        super.onPause()
        if (::musicViewModel.isInitialized) {
            musicViewModel.systemPauseMusic()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::musicViewModel.isInitialized) {
            if (musicViewModel.isMusicPlaying.value) {
                handler.postDelayed({
                    musicViewModel.startMusic(applicationContext)
                }, 250)
            }
        }
    }
}