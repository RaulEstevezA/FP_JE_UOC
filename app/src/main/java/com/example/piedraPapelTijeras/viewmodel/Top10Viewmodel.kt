package com.example.piedraPapelTijeras.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.piedraPapelTijeras.data.model.Jugador
import com.example.piedraPapelTijeras.repositorio.JugadorRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class Top10Viewmodel(private val repositorio: JugadorRepositorio) : ViewModel() {

    private val _top10 = MutableStateFlow<List<Jugador>>(emptyList())
    val top10: StateFlow<List<Jugador>> = _top10

    // init {
    //    cargarTop10()
    //}

    fun cargarTop10() {
        viewModelScope.launch {
            val lista = repositorio.obtenerTop10()
            _top10.value = lista.filterNotNull()
        }
    }
}

