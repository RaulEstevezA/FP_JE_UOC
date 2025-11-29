package com.example.piedraPapelTijeras.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.example.piedraPapelTijeras.ui.util.LocalAppLocale
import com.example.piedraPapelTijeras.viewmodel.LanguageViewModel
import java.util.Locale

@Composable
fun AppContent(
    navController: NavHostController,
    languageViewModel: LanguageViewModel,
    content: @Composable () -> Unit
) {
    val idioma = languageViewModel.idiomaActual.collectAsState()

    val locale = when (idioma.value) {
        com.example.piedraPapelTijeras.data.model.Idioma.ES -> Locale("es")
        com.example.piedraPapelTijeras.data.model.Idioma.EN -> Locale("en")
    }

    CompositionLocalProvider(
        LocalAppLocale provides locale
    ) {
        content()
    }
}