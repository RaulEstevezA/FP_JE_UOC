package com.example.piedraPapelTijeras.repositorio




import android.util.Log
import com.example.piedraPapelTijeras.data.model.JugadorFirebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose

class RankingRepositorio {

    // Instancia de la base de datos Firestore
    private val db = FirebaseFirestore.getInstance()
    // Referencia a la colección "jugadores"
    private val coleccionJugadores = db.collection("jugadores")
    //referencia al bote global
    private val docBote = db.collection("configuracion").document("bote")

    // Función para subir puntuación
    suspend fun subirPuntuacion(nombre: String, puntuacion: Int) {
        val jugador = JugadorFirebase(nombre, puntuacion)

        try {
            // Usamos el nombre como ID del documento.
            // set() crea el documento si no existe, o lo sobrescribe si ya existe.
            coleccionJugadores.document(nombre).set(jugador).await()

        } catch (e: Exception) {

        }
    }

    // Función para obtener el Top 10 de mejores jugadores
   /* suspend fun obtenerTopJugadores(): List<JugadorFirebase> {
        return try {
            val snapshot = coleccionJugadores
                .orderBy("puntuacion", Query.Direction.DESCENDING) // Ordenamos de mayor a menor puntuación
                .limit(10) // Solo queremos los 10 mejores
                .get()
                .await() // Esperamos el resultado (coroutine)

            // Convertimos los documentos JSON a objetos Kotlin
            snapshot.toObjects(JugadorFirebase::class.java)
        } catch (e: Exception) {

            emptyList() // Si falla, devolvemos lista vacía para que no cierra la app
        }
    }
    */


    // cambios en tiempo real para actulizar el ranking
    fun obtenerTopJugadoresEnTiempoReal(): Flow<List<JugadorFirebase>> =
        callbackFlow {

        // Creamos la suscripción a Firestore
        val suscripcion = coleccionJugadores
            .orderBy("puntuacion", Query.Direction.DESCENDING)
            .limit(10)
            .addSnapshotListener { snapshot, error ->
                //si algo va mal, cerramos el flujo y avisamos
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                // ??datos nuevos
                if (snapshot != null) {
                    val listaJugadores = snapshot.toObjects(JugadorFirebase::class.java)
                    // actualiza lista al ViewModel
                    trySend(listaJugadores)
                }
            }


        // cierra conexion al cerrar el programa
           awaitClose { suscripcion.remove() }
    }

    // Función para escuchar los cambios de un solo jugador en tiempo real
    fun obtenerJugadorEnTiempoReal(nombre: String): Flow<JugadorFirebase?> = callbackFlow {
        // Apuntamos al documento específico de este jugador en la colección "jugadores"
        val docRef = coleccionJugadores.document(nombre)

        // Firebase nos avisará cada vez que este documento cambie.
        val subscription = docRef.addSnapshotListener { snapshot, error ->
            // Si hay un error de red (por ejemplo, se pierde la conexión), cerramos el flujo para evitar problemas.
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            // Si recibimos datos del documento...
            if (snapshot != null && snapshot.exists()) {
                // Convertimos el documento de Firebase a nuestro objeto Kotlin `JugadorFirebase`.
                val jugador = snapshot.toObject(JugadorFirebase::class.java)
                // Enviamos el jugador actualizado a través del del Flow.
                trySend(jugador)
            } else {
                // Si el documento no existe en Firebase (por ejemplo, un jugador nuevo), enviamos `null`.
                trySend(null)
            }
        }

        //cierra comunicacion ahorradon datos al finalizar
        awaitClose { subscription.remove() }
    }



    //sumar puntos al botes con transacion segura
    suspend fun sumarAlBote(puntos: Int){
        try {
            db.runTransaction { transaction ->
                val snapshot = transaction.get(docBote)
                val boteActual = snapshot.getLong("puntos") ?: 0
                val nuevoBote = boteActual + puntos
                transaction.update(docBote, "puntos", nuevoBote)
            }.await()

        } catch (e: Exception) {

        }
    }

    // Ganar el bote (Transacción: lee, devuelve el valor, y lo pone a 0)
    suspend fun llevarseBote(): Int {
        return try {
            val premio = db.runTransaction { transaction ->
                val snapshot = transaction.get(docBote)
                val boteActual = snapshot.getLong("puntos") ?: 0

                if (boteActual > 0) {
                    // Si hay bote, lo vaciamos
                    transaction.update(docBote, "puntos", 0)
                    boteActual.toInt() // Retornamos lo que había
                } else {
                    0 // No había nada
                }
            }.await()

            premio ?: 0 // Por si acaso devuelve null la transacción
        } catch (e: Exception) {

            0
        }
    }
    // Escuchar el bote en tiempo real (Flow)
    fun obtenerBoteEnTiempoReal(): Flow<Int> = callbackFlow {
        // Nos suscribimos a cambios en el documento "configuracion/bote"
        val subscription = docBote.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                // Si el documento existe, sacamos el campo "puntos"
                val puntos = snapshot.getLong("puntos")?.toInt() ?: 0
                trySend(puntos)
            } else {
                // Si no existe el documento, asumimos que el bote es 0
                trySend(0)
            }
        }

        // Importante: cerrar la conexión al salir
        awaitClose { subscription.remove() }
    }

    suspend fun actualizarUbicacion(nombre: String, latitud: Double, longitud: Double) {
        try {
            val datosUbicacion = mapOf(
                "latitud" to latitud,
                "longitud" to longitud
            )
            // Actualizamos solo esos campos sin borrar el resto (nombre, puntos)
            coleccionJugadores.document(nombre).update(datosUbicacion).await()

        } catch (e: Exception) {

        }
    }


}