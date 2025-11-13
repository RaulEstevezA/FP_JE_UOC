package com.example.piedraPapelTijeras.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text

import androidx.compose.material3.TextField

import androidx.compose.material3.TextFieldDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.piedraPapelTijeras.R
import com.example.piedraPapelTijeras.ui.AgregarBoton
import com.example.piedraPapelTijeras.ui.componentes.CambiarBotonMusica
import com.example.piedraPapelTijeras.viewmodel.LoginViewModel
import com.example.piedraPapelTijeras.viewmodel.MusicViewModel
import com.example.piedraPapelTijeras.viewmodel.Top10Viewmodel

@Composable
//fun PantallaLogin()
fun PantallaLogin(loginViewModel: LoginViewModel, top10ViewModel: Top10Viewmodel, navController: NavHostController, musicViewModel: MusicViewModel)
{

    var textLogin by rememberSaveable { mutableStateOf("") }
    //  nuevos estados del ViewModel
    val showInvalidEmailDialog by loginViewModel.invalidoEmailDialogo.collectAsState()
    val showUserNotFoundDialog by loginViewModel.usuarioNoEncontrado.collectAsState()
    val dialogoTexto by loginViewModel.dialogoTexto.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
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
        modifier = Modifier.Companion.fillMaxSize()
            .padding(10.dp, 100.dp, 10.dp, 10.dp),

        horizontalAlignment = Alignment.Companion.CenterHorizontally,
    ) {

        TextField(

            value = textLogin,
            onValueChange = { textLogin = it },
            colors = TextFieldDefaults.colors(

                unfocusedContainerColor = Color.Unspecified,
                disabledContainerColor = Color.Gray,
                errorContainerColor = Color.Red,
            ),
            label = { stringResource(R.string.login_textField_label) },


            )



        Spacer(modifier = Modifier.Companion.height(50.dp))




        // boton Login salta a jugar si exste el usuario sino abre AlertDialog
        AgregarBoton(

            onclick = {
                loginViewModel.cargarJugador(
                    textLogin,
                    usuarioExiste = { navController.navigate("juego") },

                    )
            },
            icon = Icons.Default.PlayArrow,
            des = stringResource(R.string.jugar_desc),
            text = stringResource(R.string.login_text),
            modifier = Modifier.Companion.width(200.dp)
        )

        AgregarBoton(

            onclick = { navController.popBackStack() },
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            des = stringResource(R.string.volver_text_desc),
            text = stringResource(R.string.volver_text),
            modifier = Modifier.padding(top = 16.dp).width(150.dp)
        )

        // Diálogo 1: Para el error de formato de email
        if (showInvalidEmailDialog) {
            AlertDialog(
                onDismissRequest = { loginViewModel.cerrarDialogo() },
                title = { Text(stringResource(R.string.error_formato)) },
                text = { Text(dialogoTexto) },
                confirmButton = {
                    Button(onClick = { loginViewModel.cerrarDialogo() }) {
                        Text(stringResource(R.string.aceptar))
                    }
                }
            )
        }

        // Diálogo 2: Para cuando el usuario no se encuentra y se le ofrece crearlo
        if (showUserNotFoundDialog) {
            val currentTextLogin = textLogin
            AlertDialog(
                onDismissRequest = { loginViewModel.cerrarDialogo() },
                title = { Text(stringResource(R.string.usuario_no_registrado)) },
                text = { Text(dialogoTexto) },
                confirmButton = {
                    Button(onClick = {
                        loginViewModel.añadirJugador(
                            email = currentTextLogin,
                            onJuegoNavigate = { navController.navigate("juego") },
                            onTop10Refresh = { top10ViewModel.cargarTop10() }
                        )
                        loginViewModel.cerrarDialogo()
                    }) {
                        Text(stringResource(R.string.crear))
                    }
                },
                dismissButton = {
                    Button(onClick = { loginViewModel.cerrarDialogo() }) {
                        Text(stringResource(R.string.cancelar))
                    }
                }
            )
            }

        }

    }
}












@Preview(showBackground = true)
@Composable
fun PreviewPantallaLogin() {




}
