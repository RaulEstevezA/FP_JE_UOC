package com.example.piedraPapelTijeras.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.piedraPapelTijeras.R
import com.example.piedraPapelTijeras.ui.AgregarBoton
import com.example.piedraPapelTijeras.ui.componentes.CambiarBotonMusica
import com.example.piedraPapelTijeras.ui.mensajes.LoginMessage
import com.example.piedraPapelTijeras.ui.util.SoundPlayer
import com.example.piedraPapelTijeras.viewmodel.LoginViewModel
import com.example.piedraPapelTijeras.viewmodel.MusicViewModel
import com.example.piedraPapelTijeras.viewmodel.Top10Viewmodel
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.piedraPapelTijeras.ui.util.localizedString

@Composable
fun PantallaLogin(
    loginViewModel: LoginViewModel,
    top10ViewModel: Top10Viewmodel,
    navController: NavHostController,
    musicViewModel: MusicViewModel,
    soundPlayer: SoundPlayer
) {


    //  ESTADOS
    var textLogin by rememberSaveable { mutableStateOf("j@gmail.com") }

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
                .padding(10.dp, 100.dp, 10.dp, 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


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
