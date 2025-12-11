package com.example.piedraPapelTijeras.ui.util

import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

val LocalAppLocale = staticCompositionLocalOf<Locale> {
    Locale("es")
}