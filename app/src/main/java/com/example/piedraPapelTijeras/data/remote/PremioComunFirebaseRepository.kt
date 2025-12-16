package com.example.piedraPapelTijeras.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PremioComunFirebaseRepository {

    private val db = FirebaseFirestore.getInstance()
    private val docRef = db.collection("premio_comun").document("bote")

    suspend fun obtenerBote(): Int {
        val snapshot = docRef.get().await()
        return snapshot.getLong("cantidad")?.toInt() ?: 0
    }

    suspend fun sumarAlBote(cantidad: Int) {
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            val actual = if (snapshot.exists()) {
                snapshot.getLong("cantidad") ?: 0
            } else {
                0
            }

            // 🔑 si no existe → set, si existe → update
            transaction.set(
                docRef,
                mapOf("cantidad" to (actual + cantidad))
            )
        }.await()
    }

    suspend fun reclamarBote(): Int {
        var premio = 0

        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            premio = snapshot.getLong("cantidad")?.toInt() ?: 0

            transaction.set(
                docRef,
                mapOf("cantidad" to 0)
            )
        }.await()

        return premio
    }

    suspend fun obtenerBotePorRest(): Int {
        val response = RetrofitInstance.api.obtenerBote()
        return response.fields.cantidad.integerValue.toInt()
    }
}