package com.example.piedraPapelTijeras.data.network

import com.squareup.moshi.Json


/**
 * Definimos la forma tiene que teneer la respuesta que esperamos de Firestore
 */
//Le decimos a Moshi(traductor) busca fields
data class FirestoreDocumentResponse(
    @Json(name = "fields") val fields: BoteFields?
)
//aqui dentro de fields que busque puntos
data class BoteFields(
    @Json( name= "puntos") val puntos: FirestoreIntegerValue?
)
//Y dentro de puntos coge el valor que esta en la etiquta "integerValue" que sera un String
data class FirestoreIntegerValue(
    @Json(name = "integerValue") val value: String?
)


