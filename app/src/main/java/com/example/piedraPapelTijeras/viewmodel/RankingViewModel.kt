package com.example.piedraPapelTijeras.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.piedraPapelTijeras.data.model.JugadorFirebase
import com.example.piedraPapelTijeras.repositorio.RankingRepositorio
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.flow.MutableStateFlow

class RankingViewModel : ViewModel() {

    // Para hablar con Firebase
    private val repository = RankingRepositorio()

    // Variables para el Premio (configuracion remota Firesbase)
    private val _tituloPremio = MutableStateFlow("cargando...")
    val tituloPremio: StateFlow<String> = _tituloPremio

    private val _descripcionPremio = MutableStateFlow("")
    val descripcionPremio: StateFlow<String> = _descripcionPremio


    // Estado que la UI puede leer.

    val ranking: StateFlow<List<JugadorFirebase>> = repository.obtenerTopJugadoresEnTiempoReal()
        .stateIn(
            scope = viewModelScope, // acaba al si se cierra el viewmodel
            started = SharingStarted.WhileSubscribed(5000), // Espera 5s antes de cortar si sales de la pantalla
            initialValue = emptyList() // Empieza vacía mientras carga
        )

    //Carga los premios desde la funcion al inicio

    init {
        cargarConfiguracionPremio()
    }

    //Fucion que se comunica con Firebase

    private fun cargarConfiguracionPremio(){

        //obtenemos instancia de remote config
        val remoteConfig = Firebase.remoteConfig

        //Lo siguiente hace que se actualize cada vez que se abra la app ( intervalo 0  ya que es para pruebas)
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        //Solicita los datos
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    //si hay exito leemos los valores claves
                    val titulo = remoteConfig.getString("premio_titulo")
                    val descripcion = remoteConfig.getString("premio_descripcion")

                    //Actulizanmos variables
                    _tituloPremio.value = titulo
                    _descripcionPremio.value = descripcion
                }else{
                    //Si falla dejamos valores por defecto o mostramos error
                    _tituloPremio.value = "Error al cargar el premio"
                }


            }
    }
}
