package com.example.m08_elinwetterman.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Clase que gestiona los datos del usuario utilizando DataStore para almacenar el nombre de usuario y la contraseña.
 * @param context El contexto de la aplicación.
 */

class User(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")
        val STORE_USERNAME = stringPreferencesKey("store_username")
        val STORE_USERPASS = stringPreferencesKey("store_userpass")
    }

    /**
     * Flujo de datos que emite una lista de cadenas que contiene el nombre de usuario y la contraseña almacenados.
     */
    val getUserData: Flow<List<String>> = context.dataStore.data.map { prefs ->
        listOf(
            prefs[STORE_USERNAME] ?: "",
            prefs[STORE_USERPASS] ?: ""
        )
    }

    /**
     * Método suspendido que guarda el nombre de usuario y la contraseña en DataStore.
     * @param username El nombre de usuario a guardar.
     * @param userpass La contraseña a guardar.
     */
    suspend fun saveUserData(username: String, userpass: String) {
        context.dataStore.edit { prefs ->
            prefs[STORE_USERNAME] = username
            prefs[STORE_USERPASS] = userpass
        }
    }

    /**
     * Método suspendido que elimina el nombre de usuario y la contraseña de DataStore.
     */
    suspend fun deleteUserData() {
        context.dataStore.edit { prefs ->
            prefs[STORE_USERNAME] = ""
            prefs[STORE_USERPASS] = ""
        }
    }

    /**
     * Método suspendido que elimina solo la contraseña del usuario de DataStore.
     */
    suspend fun deleteUserPass() {
        context.dataStore.edit { prefs ->
            prefs[STORE_USERPASS] = ""
        }
    }
}