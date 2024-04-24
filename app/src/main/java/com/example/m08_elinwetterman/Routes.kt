package com.example.m08_elinwetterman


/**
 * Clase sellada que define las distintas rutas de la aplicación.
 */
sealed class Routes(val route: String) {
    /**
     * Objeto que representa la pantalla principal del mapa.
     */
    object MapScreen : Routes("map_screen")

    /**
     * Objeto que representa la pantalla de lista de marcadores.
     */
    object ListMarkersScreen : Routes("list_mark")

    /**
     * Objeto que representa la pantalla de la cámara (posiblemente un error tipográfico, debería ser `Camera`).
     */
    object Camara : Routes("camera_screen")

    /**
     * Objeto que representa la pantalla para tomar una foto.
     */
    object TakePhotoScreen : Routes("take_photo")

    /**
     * Objeto que representa la pantalla de edición de marcador.
     */
    object EditMarker : Routes("edit_marker")

    /**
     * Objeto que representa la pantalla de inicio de sesión.
     */
    object LogScreen : Routes("login_screen")

    /**
     * Objeto que representa la pantalla de registro de usuario.
     */
    object RegisterScreen : Routes("register_screen")

    /**
     * Objeto que representa la pantalla de perfil de usuario.
     */
    object ProfileScreen : Routes("user_screen")
}
