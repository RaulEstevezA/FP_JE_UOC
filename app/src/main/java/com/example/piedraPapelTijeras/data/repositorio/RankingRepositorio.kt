package com.example.piedraPapelTijeras.data.repositorio

import android.util.Log
import com.example.piedraPapelTijeras.data.model.JugadorFirebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RankingRepositorio {

    private val db = FirebaseFirestore.getInstance()
    private val coleccionJugadores = db.collection("jugadores")
    private val docBote = db.collection("configuracion").document("bote")

    // --- FUNCIONES DE JUGADOR ---

    suspend fun subirPuntuacion(nombre: String, puntuacion: Int) {
        val jugador = JugadorFirebase(nombre, puntuacion)
        try {
            coleccionJugadores.document(nombre).set(jugador).await()
        } catch (e: Exception) {
            Log.e("RankingRepo", "Error al subir puntuación", e)
        }
    }

    suspend fun obtenerPuntuacion(nombre: String): Int? {
        return try {
            val document = coleccionJugadores.document(nombre).get().await()
            document.getLong("puntuacion")?.toInt()
        } catch (e: Exception) {
            null
        }
    }

    // NUEVO: Función para actualizar la ubicación en Firebase
    suspend fun actualizarUbicacion(nombre: String, latitud: Double, longitud: Double) {
        try {
            val updates = mapOf(
                "latitud" to latitud,
                "longitud" to longitud
            )
            coleccionJugadores.document(nombre).update(updates).await()
            Log.d("RankingRepo", "Ubicación actualizada para $nombre")
        } catch (e: Exception) {
            Log.e("RankingRepo", "Error al actualizar ubicación", e)
        }
    }

    // Escuchar a un jugador en tiempo real
    fun obtenerJugadorEnTiempoReal(nombre: String): Flow<JugadorFirebase?> = callbackFlow {
        val docRef = coleccionJugadores.document(nombre)
        val subscription = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val jugador = snapshot.toObject(JugadorFirebase::class.java)
                trySend(jugador)
            } else {
                trySend(null) // El jugador no existe en Firebase
            }
        }
        awaitClose { subscription.remove() }
    }

    // --- FUNCIONES DE RANKING ---

    fun obtenerTopJugadoresEnTiempoReal(): Flow<List<JugadorFirebase>> = callbackFlow {
        val listener = coleccionJugadores
            .orderBy("puntuacion", Query.Direction.DESCENDING)
            .limit(10)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val lista = snapshot?.toObjects(JugadorFirebase::class.java) ?: emptyList()
                trySend(lista)
            }
        awaitClose { listener.remove() }
    }

    // --- FUNCIONES DEL BOTE ---

    fun obtenerBoteEnTiempoReal(): Flow<Int> = callbackFlow {
        val subscription = docBote.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val puntos = snapshot.getLong("puntos")?.toInt() ?: 0
                trySend(puntos)
            } else {
                trySend(0)
            }
        }
        awaitClose { subscription.remove() }
    }

    suspend fun sumarAlBote(puntos: Int) {
        try {
            db.runTransaction { transaction ->
                val snapshot = transaction.get(docBote)
                val boteActual = snapshot.getLong("puntos") ?: 0
                val nuevoBote = boteActual + puntos
                transaction.update(docBote, "puntos", nuevoBote)
            }.await()
        } catch (e: Exception) {
            Log.e("RankingRepo", "Error al sumar al bote", e)
        }
    }

    suspend fun llevarseBote(): Int {
        return try {
            val premio = db.runTransaction { transaction ->
                val snapshot = transaction.get(docBote)
                val boteActual = snapshot.getLong("puntos") ?: 0
                if (boteActual > 0) {
                    transaction.update(docBote, "puntos", 0)
                    boteActual.toInt()
                } else {
                    0
                }
            }.await()
            premio ?: 0
        } catch (e: Exception) {
            Log.e("RankingRepo", "Error al ganar bote", e)
            0
        }
    }
}