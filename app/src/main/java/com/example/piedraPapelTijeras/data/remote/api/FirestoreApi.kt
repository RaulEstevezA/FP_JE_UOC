package com.example.piedraPapelTijeras.data.remote.api

import com.example.piedraPapelTijeras.data.remote.dto.BoteDto
import retrofit2.http.GET

interface FirestoreApi {

    @GET("projects/piedrapapeltijeras-2b616/databases/(default)/documents/premio_comun/bote")
    suspend fun obtenerBote(): BoteDto
}
