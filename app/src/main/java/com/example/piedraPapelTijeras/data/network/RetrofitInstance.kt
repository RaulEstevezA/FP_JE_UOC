package com.example.piedraPapelTijeras.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
/**
 * Singleton que gestiona la comunicacion de la app
 */
object RetrofitInstance {

    // La dirección base de Firestore repetible para todas las conexiones.

    private const val BASE_URL = "https://firestore.googleapis.com/"

    //Al traductor moshi
    // Le añadimos el `KotlinJsonAdapterFactory` para que traduzca a objetos Kotlin y viceversa

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()


    // Usamos 'by lazy' hasta que alguien no necesita por primera vez no se crea la instancia.
    //Creamos una variable que creara la conecion y traductor cuando se necesite
    private val retrofit by lazy {
        //contruimos Retrofit
        Retrofit.Builder()
            // Le damos la dirección principal.
            .baseUrl(BASE_URL)
            // Le asignamos al traductor Moshi.
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            //pulsamos el botón de construir!
            .build()
    }


    // Instancia pública de la API.
    // Usamos .create() para que Retrofit convierta nuestra interfaz 'FirestoreApiService'
    // en una clase Java/Kotlin ejecutable que realiza las peticiones HTTP definidas.
    val api: FirestoreApiService by lazy {

        retrofit.create(FirestoreApiService::class.java)
    }
}
