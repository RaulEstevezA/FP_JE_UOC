package com.example.piedraPapelTijeras.ui.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

class LocationService(private val context: Context) {

    //Funcion que obtiene la ubicacion pero con la condicion de que tengamos la autorización
    @SuppressLint("MissingPermission")
    suspend fun getUserLocation(): Location? {
        //cliente de Google Location
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        return try {
            // Configuramos la precisión.
            // para ahorro de batería,  PRIORITY_BALANCED_POWER_ACCURACY.
            val priority = Priority.PRIORITY_HIGH_ACCURACY//precision máxima

            //cancelar la petición si tarda mucho
            val cancellationToken = CancellationTokenSource()

            //Esperamos la respuesta del satelite
            fusedLocationProviderClient.getCurrentLocation(
                priority,
                cancellationToken.token
            ).await()

        } catch (e: Exception) {
            e.printStackTrace()
            null // Si algo falla, (sin ubicación)
        }
    }
}
