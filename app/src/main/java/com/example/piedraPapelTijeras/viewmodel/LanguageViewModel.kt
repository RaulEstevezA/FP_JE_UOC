package com.example.piedraPapelTijeras.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.piedraPapelTijeras.data.model.Idioma
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LanguageViewModel(context: Context) : ViewModel() {

    private val _idiomaActual = MutableStateFlow(Idioma.ES)
    val idiomaActual: StateFlow<Idioma> = _idiomaActual

    init {
        val locale = context.resources.configuration.locales[0]
        _idiomaActual.value = if (locale.language.lowercase() == "es") Idioma.ES else Idioma.EN
    }

    fun toggleLanguage() {
        _idiomaActual.value =
            if (_idiomaActual.value == Idioma.ES) Idioma.EN else Idioma.ES
    }
}