package com.example.piedraPapelTijeras.ui.pantallas

import androidx.compose.foundation.background
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
import com.example.piedraPapelTijeras.viewmodel.LoginViewModel
import com.example.piedraPapelTijeras.viewmodel.Top10Viewmodel

@Composable
//fun PantallaLogin()
fun PantallaLogin(loginViewModel: LoginViewModel, top10ViewModel: Top10Viewmodel, navController: NavHostController)
{

    var textLogin by rememberSaveable { mutableStateOf("a@b.es") }
    val registroNuevo by loginViewModel.registroNuevo.collectAsState()
    val dialogoTexto by loginViewModel.dialogoTexto.collectAsState()






    Column(
        modifier = Modifier.Companion.fillMaxSize()
            .background(Color(0xFFA8E6CF))
            .padding(10.dp, 100.dp, 10.dp, 10.dp),

        horizontalAlignment = Alignment.Companion.CenterHorizontally,
    ){

        TextField(

            value = textLogin,
            onValueChange = {textLogin = it},
            colors = TextFieldDefaults.colors(

                unfocusedContainerColor = Color.Unspecified,
                disabledContainerColor = Color.Gray,
                errorContainerColor = Color.Red,
            ),
            label = {stringResource(R.string.login_textField_label)},


        )



        Spacer(modifier = Modifier.Companion.height(50.dp))




        // boton Login salta a jugar si exste el ussuario sino abre AlertDialog
        AgregarBoton(

            onclick = {
                loginViewModel.cargarJugador(
                    textLogin,
                    usuarioExiste = {navController.navigate("juego")},

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
            des = "Volver",
            text = "Volver",
            modifier = Modifier.padding(top = 16.dp).width(150.dp)
        )

        if (registroNuevo) {
            val currentTextLogin = textLogin
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Usuario no encontrado") },
                text = { Text(dialogoTexto) },
                confirmButton = {
                    Button(onClick = {
                        loginViewModel.añadirJugador(
                            email = currentTextLogin,
                            onJuegoNavigate = { navController.navigate("juego") },
                            onTop10Refresh = {top10ViewModel.cargarTop10()}
                        )
                        loginViewModel.cerrarDialogo()
                    }) {
                        Text("Crear usuario")
                    }
                },
                dismissButton = {
                    Button(onClick = { loginViewModel.cerrarDialogo() }) {
                        Text("Cancelar")
                    }
                }
            )
        }




    }
}












@Preview(showBackground = true)
@Composable
fun PreviewPantallaLogin() {




}
