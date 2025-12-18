package com.example.piedraPapelTijeras.ui.util

import android.util.Patterns

object ValidarMail {

    /**
     * Comprueba si un texto tiene la estructura de un email válido.
     *
     */
    fun esMailValido(email: String): Boolean {

        val emailSinEspacios = email.trim()


        if (emailSinEspacios.isEmpty()) {
            return false
        }

        //Usa la herramienta de Android (expresión regular) para comprobar la estructura.

        return Patterns.EMAIL_ADDRESS.matcher(emailSinEspacios).matches()
    }

    /**
     * Comprueba si un email es de Gmail.
     */
    fun esMailGmail(email: String): Boolean {
        // Comprueba si el email (sin espacios) termina en @gmail.com
        return email.trim().endsWith("@gmail.com", ignoreCase = true)
    }
}