package com.example.piedraPapelTijeras


import android.os.Bundle
import androidx

.activity.ComponentActivity
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
import com.example.piedraPapelTijeras.ui.componentes.BackgroundMusicPlayer
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
import com.example.piedraPapelTijeras.ui.util.SoundPlayer
import androidx.activity.result.contract.ActivityResultContracts
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.Manifest
import android.os.Build
import android.util.Log
import android.os.Handler
import android.os.Looper
import com.example.piedraPapelTijeras.ui.pantallas.PantallaTop10

class MainActivity : ComponentActivity() {
    private lateinit var juegoViewModel: JuegoViewModel
    private lateinit var musicViewModel: MusicViewModel
    private val handler = Handler(Looper.getMainLooper())

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("MainActivity", "Permiso WRITE_CALENDAR concedido.")
        } else {
            Log.w("MainActivity", "Permiso WRITE_CALENDAR denegado.")
        }
    }

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

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_CALENDAR)
        }

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

            val languageViewModel: LanguageViewModel = viewModel(
                factory = Injeccion.provideLanguageViewModelFactory(
                    context = applicationContext
                )
            )

            val idiomaState = languageViewModel.idiomaActual.collectAsState()

            val currentLocale =
                if (idiomaState.value.name == "ES") Locale("es")
                else Locale("en")

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
                    }

                    NavHost(
                        navController = navController,
                        startDestination = "principal"
                    ) {
                        composable("principal") {
                            PantallaPrincipal(
                                navController = navController,
                                musicViewModel = this@MainActivity.musicViewModel,
                                soundPlayer = soundPlayer

                            )
                        }
                        composable("login") {
                            PantallaLogin(
                                loginViewModel = loginViewModel,
                                top10ViewModel = top10ViewModel,
                                navController = navController,
                                musicViewModel = this@MainActivity.musicViewModel,
                                soundPlayer = soundPlayer
                            )

                        }
                        composable("juego") {
                            PantallaJuego(
                                juegoViewModel = this@MainActivity.juegoViewModel,
                                navController = navController,
                                musicViewModel = this@MainActivity.musicViewModel,
                                soundPlayer = soundPlayer
                            )
                        }
                        composable("ayuda") {
                            PantallaAyuda(
                                navController = navController,
                                musicViewModel = this@MainActivity.musicViewModel,
                                soundPlayer = soundPlayer
                            )
                        }
                        composable("top10") {
                            PantallaTop10(
                                top10ViewModel = top10ViewModel,
                                navController = navController,
                                musicViewModel = this@MainActivity.musicViewModel,
                                soundPlayer = soundPlayer
                            )
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