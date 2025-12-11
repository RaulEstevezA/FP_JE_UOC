package com.example.piedraPapelTijeras.ui.mensajes

sealed class LoginMessage {
    data object EmailInvalido : LoginMessage()
    data object UsuarioNoRegistrado : LoginMessage()
    data object UsuarioCreado : LoginMessage()
}