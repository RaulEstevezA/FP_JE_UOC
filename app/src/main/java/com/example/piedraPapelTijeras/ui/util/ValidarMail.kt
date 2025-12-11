package com.example.piedraPapelTijeras.ui.util

import android.util.Patterns

object ValidarMail {

    /**
     * Comprueba si un texto tiene la estructura de un email válido.
     * Esta es la función que corregirá tu bug.
     */
    fun esMailValido(email: String): Boolean {
        // 1. Quita los espacios de los bordes.
        val emailSinEspacios = email.trim()

        // 2. Si está vacío después de quitar espacios, no es válido.
        if (emailSinEspacios.isEmpty()) {
            return false
        }

        // 3. Usa la herramienta de Android (expresión regular) para comprobar la estructura.
        // Esto detectará errores como dobles '@', dominios sin punto, etc.
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