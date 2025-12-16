package com.example.piedraPapelTijeras.data.remote

import com.example.piedraPapelTijeras.data.model.Jugador
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class Top10FirebaseRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("top10")

    // Guardar o actualizar jugador
    suspend fun guardarJugador(jugador: Jugador) {
        collection
            .document(jugador.mail)   // clave única
            .set(jugador)
            .await()
    }

    // Obtener top 10
    suspend fun obtenerTop10(): List<Jugador> {
        return collection
            .orderBy("puntuacion", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .await()
            .toObjects(Jugador::class.java)
    }
}
