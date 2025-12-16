package com.example.piedraPapelTijeras.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.piedraPapelTijeras.data.remote.PremioComunFirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BoteComunViewModel(
    private val repo: PremioComunFirebaseRepository
) : ViewModel() {

    private val _bote = MutableStateFlow(0)
    val bote: StateFlow<Int> = _bote

    fun cargarBote() {
        viewModelScope.launch {
            _bote.value = repo.obtenerBotePorRest()
        }
    }
}