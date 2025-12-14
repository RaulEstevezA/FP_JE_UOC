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
import androidx.compose.runtime.LaunchedEffect
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import java.util.Locale
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {

    private lateinit var juegoViewModel: JuegoViewModel
    private lateinit var musicViewModel: MusicViewModel
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var loginViewModel: LoginViewModel

    // Google Sign-In
    private var pendingGoogleIdToken: String? = null
    private lateinit var googleSignInClient: com.google.android.gms.auth.api.signin.GoogleSignInClient
    private lateinit var googleSignInLauncher: androidx.activity.result.ActivityResultLauncher<android.content.Intent>

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

        loginViewModel = ViewModelProvider(
            this,
            Injeccion.provideLoginViewModelFactory(
                context = applicationContext
            )
        )[LoginViewModel::class.java]

        // Google Sign-In client
        googleSignInClient = GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )

        // Google Sign-In launcher
        googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {

                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

                    try {
                        val account = task.getResult(Exception::class.java)
                        val idToken = account.idToken

                        if (idToken != null) {
                            loginViewModel.loginConGoogle(
                                idToken = idToken,
                                onSuccess = {
                                    Log.d("GOOGLE_LOGIN", "Login Google correcto")
                                }
                            )
                        }

                    } catch (e: Exception) {
                        Log.e("GOOGLE_SIGN_IN", "Error en Google Sign-In", e)
                    }
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

                        // 🔑 PUENTE REAL Google → ViewModel
                        LaunchedEffect(pendingGoogleIdToken) {
                            pendingGoogleIdToken?.let { token ->
                                loginViewModel.loginConGoogle(
                                    idToken = token,
                                    onSuccess = { navController.navigate("juego") }
                                )
                                pendingGoogleIdToken = null
                            }
                        }

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
                                    soundPlayer = soundPlayer,
                                    onGoogleLoginClick = {
                                        googleSignInClient.signOut().addOnCompleteListener {
                                            googleSignInLauncher.launch(
                                                googleSignInClient.signInIntent
                                            )
                                        }
                                    }
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
        Log.d("MUSIC-DEBUG", "onResume() llamado")

        if (::musicViewModel.isInitialized) {
            Log.d("MUSIC-DEBUG", "isMusicPlaying = ${musicViewModel.isMusicPlaying.value}")
            Log.d("MUSIC-DEBUG", "wasSystemPaused = ${musicViewModel.wasSystemPaused.value}")

            if (musicViewModel.isMusicPlaying.value &&
                musicViewModel.wasSystemPaused.value
            ) {
                Log.d("MUSIC-DEBUG", "Reanudando música tras pausa del sistema")
                musicViewModel.resumeAfterSystemPause(applicationContext)
            }
        }
    }
}