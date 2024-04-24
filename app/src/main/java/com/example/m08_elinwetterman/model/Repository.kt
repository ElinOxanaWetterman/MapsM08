package com.example.m08_elinwetterman.model

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Clase que actúa como un repositorio para acceder a Firestore y obtener referencias a las colecciones relevantes.
 */
class Repository {
    // Instancia de la base de datos Firestore
    private val database = FirebaseFirestore.getInstance()

    /**
     * Método que devuelve una referencia a la colección "markers" en Firestore.
     * @return Una referencia a la colección "markers".
     */
    fun getMarkers(): CollectionReference {
        return database.collection("markers")
    }

    /**
     * Método que devuelve una referencia a la colección "user" en Firestore.
     * @return Una referencia a la colección "user".
     */
    fun getUserImageUri(): CollectionReference {
        return database.collection("user")
    }
}