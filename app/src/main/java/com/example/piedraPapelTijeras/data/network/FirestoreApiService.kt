package com.example.piedraPapelTijeras.data.network



import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface FirestoreApiService {

    //@Get es de retrofit y indica que es una peticion tipo get
    //dirrecion para acceder al proyecto de firebase
    @GET("v1/projects/{projectId}/databases/(default)/documents/configuracion/bote")
    suspend fun getBote(
        //Path le dice a Retrofic que reemplace "{projectId} en l URL con el valor que pasamos en la variable
        @Path("projectId") projectId: String
        //Devuelve la respuesta segun la composicion de la data class FirestoreDocumentResponse
    ): Response<FirestoreDocumentResponse>
}