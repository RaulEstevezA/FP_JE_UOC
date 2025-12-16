package com.example.piedraPapelTijeras.data.remote

import com.example.piedraPapelTijeras.data.remote.api.FirestoreApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://firestore.googleapis.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: FirestoreApi by lazy {
        retrofit.create(FirestoreApi::class.java)
    }
}