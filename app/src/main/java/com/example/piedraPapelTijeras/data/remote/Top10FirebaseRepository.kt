package com.example.piedraPapelTijeras.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class ScoreFirebase(
    val email: String = "",
    val puntos: Int = 0,
    val timestamp: Long = 0L
)

class Top10FirebaseRepository {

    private val db = FirebaseFirestore.getInstance()

    fun guardarPuntuacion(email: String, puntos: Int) {
        val data = hashMapOf(
            "email" to email,
            "puntos" to puntos,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("top10")
            .add(data)
    }

    fun obtenerTop10(onResult: (List<ScoreFirebase>) -> Unit) {
        db.collection("top10")
            .orderBy("puntos", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { snapshot ->
                onResult(snapshot.toObjects(ScoreFirebase::class.java))
            }
    }
}