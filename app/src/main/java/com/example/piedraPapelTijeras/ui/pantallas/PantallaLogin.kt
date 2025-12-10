@file:Suppress("DEPRECATION")

package com.example.piedraPapelTijeras.ui.pantallas

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.piedraPapelTijeras.R
import com.example.piedraPapelTijeras.ui.AgregarBoton
import com.example.piedraPapelTijeras.ui.util.CambiarBotonMusica
import com.example.piedraPapelTijeras.ui.mensajes.LoginMessage
import com.example.piedraPapelTijeras.ui.util.SoundPlayer
import com.example.piedraPapelTijeras.viewmodel.LoginViewModel
import com.example.piedraPapelTijeras.viewmodel.MusicViewModel
import com.example.piedraPapelTijeras.viewmodel.Top10Viewmodel
import com.example.piedraPapelTijeras.ui.util.localizedString

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PantallaLogin(
    loginViewModel: LoginViewModel,
    top10ViewModel: Top10Viewmodel,
    navController: NavHostController,
    musicViewModel: MusicViewModel,
    soundPlayer: SoundPlayer
) {

    // Logica inicio con google (Sign in)
    val context = LocalContext.current
    // actualiza el email cuando Google responda
    var textLogin by rememberSaveable { mutableStateOf("j@gmail.com") }

    // Configuración de Google
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) 
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    // Launcher para abrir la ventana de cuentas
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.result
                    val idToken = account?.idToken
                    if (idToken != null) {
                        val credential = GoogleAuthProvider.getCredential(idToken, null)
                        FirebaseAuth.getInstance().signInWithCredential(credential)
                            .addOnCompleteListener { authTask ->
                                if (authTask.isSuccessful) {
                                    val user = FirebaseAuth.getInstance().currentUser
                                    val email = user?.email ?: ""
                                    // Actualizamos el estado local
                                    textLogin = email

                                    // Usamos tu lógica existente
                                    loginViewModel.cargarJugador(
                                        email,
                                        usuarioExiste = { navController.navigate("juego") },
                                    )
                                } else {
                                    Log.e("Login", "Fallo Auth Firebase", authTask.exception)
                                }
                            }
                    }
                } catch (e: Exception) {
                    Log.e("Login", "Fallo Google Sign In", e)
                }
            }
        }
    // --- FIN LÓGICA GOOGLE SIGN IN ---


    //  ESTADOS
    val showInvalidEmailDialog by loginViewModel.invalidoEmailDialogo.collectAsState()
    val showUserNotFoundDialog by loginViewModel.usuarioNoEncontrado.collectAsState()
    val dialogoMensaje by loginViewModel.dialogoMensaje.collectAsState()


    //   MENSAJE TRADUCIDO
    val mensajeTraducido = when (dialogoMensaje) {
        is LoginMessage.EmailInvalido -> localizedString(R.string.error_correo_invalido)
        is LoginMessage.UsuarioNoRegistrado -> localizedString(R.string.error_usuario_no_registrado)
        is LoginMessage.UsuarioCreado -> localizedString(R.string.usuario_creado_exito)
        else -> ""
    }


    //   UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFA8E6CF))
    ) {

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 40.dp)
        ) {
            CambiarBotonMusica(musicViewModel = musicViewModel)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp, 140.dp, 10.dp, 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /*
            //Login manual
            //  CAMPO LOGIN
            TextField(
                value = textLogin,
                onValueChange = { textLogin = it },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Unspecified,
                    disabledContainerColor = Color.Gray,
                    errorContainerColor = Color.Red,
                ),
                label = { Text(localizedString(R.string.login_textField_label)) }
            )

            Spacer(modifier = Modifier.height(50.dp))


            // BOTÓN LOGIN
            AgregarBoton(
                onclick = {
                    soundPlayer.playSounds(soundPlayer.sonidoBotonId)
                    loginViewModel.cargarJugador(
                        textLogin,
                        usuarioExiste = { navController.navigate("juego") },
                    )
                },
                icon = Icons.Default.PlayArrow,
                des = localizedString(R.string.jugar_desc),
                text = localizedString(R.string.login_text),
                modifier = Modifier.width(200.dp)
            )
            Fin Login manual
             */
            
            // ICONO CIRCULAR ---
            
            Text(
                text = localizedString(R.string.login_text),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Blue,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .clickable { 
                        soundPlayer.playSounds(soundPlayer.sonidoBotonId)
                        launcher.launch(googleSignInClient.signInIntent)
                    },
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 8.dp // Sombra para que flote
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_google),
                        contentDescription = "Logo Google",
                        modifier = Modifier.size(35.dp) // Tamaño del logo dentro del círculo
                    )
                }
            }
            
            // ------------------------------------

            Spacer(modifier = Modifier.height(40.dp))

            // BOTÓN VOLVER
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


            // DIÁLOGO: email inválido
            if (showInvalidEmailDialog) {
                AlertDialog(
                    onDismissRequest = { loginViewModel.cerrarDialogo() },
                    title = { Text(localizedString(R.string.error_correo_invalido)) },
                    text = { Text(mensajeTraducido) },
                    confirmButton = {
                        Button(onClick = { loginViewModel.cerrarDialogo() }) {
                            Text(localizedString(R.string.aceptar))
                        }
                    }
                )
            }


            // DIÁLOGO: usuario no encontrado
            if (showUserNotFoundDialog) {
                val currentTextLogin = textLogin
                AlertDialog(
                    onDismissRequest = { loginViewModel.cerrarDialogo() },
                    title = { Text(localizedString(R.string.usuario_no_registrado)) },
                    text = { Text(mensajeTraducido) },
                    confirmButton = {
                        Button(onClick = {
                            loginViewModel.añadirJugador(
                                email = currentTextLogin,
                                onJuegoNavigate = { navController.navigate("juego") },
                                onTop10Refresh = { top10ViewModel.cargarTop10() }
                            )
                            loginViewModel.cerrarDialogo()
                        }) {
                            Text(localizedString(R.string.crear))
                        }
                    },
                    dismissButton = {
                        Button(onClick = { loginViewModel.cerrarDialogo() }) {
                            Text(localizedString(R.string.cancelar))
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPantallaLogin() {}