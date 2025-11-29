package com.example.piedraPapelTijeras.ui.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

class LocationService(private val context: Context) {

    @SuppressLint("MissingPermission")
    suspend fun getUserLocation(): Location? {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        return try {
            val cancellationToken = CancellationTokenSource()

            fusedLocationProviderClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken.token
            ).await()

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}