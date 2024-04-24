package com.example.m08_elinwetterman

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Clase sellada que define las diferentes pantallas de la aplicación y sus características.
 */
sealed class Screens(val route: String, val title: String) {

    /**
     * Clase sellada interna que representa las pantallas del drawer y sus iconos asociados.
     */
    sealed class DrawerScreens(
        route: String,
        val icon: ImageVector,
        title: String
    ) : Screens(route, title) {

        /**
         * Objeto que representa la pantalla principal del mapa en el drawer.
         */
        object Mapa : DrawerScreens(
            Routes.MapScreen.route,
            Icons.Filled.Home,
            "MapFlurry"
        )

        /**
         * Objeto que representa la pantalla de lista de marcadores en el drawer.
         */
        object Listar : DrawerScreens(
            Routes.ListMarkersScreen.route,
            Icons.Filled.List,
            "MapFlurryMarkers"
        )

        /**
         * Objeto que representa la pantalla de perfil de usuario en el drawer.
         */
        object ProfileScreen : DrawerScreens(
            Routes.ProfileScreen.route,
            Icons.Filled.PersonPin,
            "User"
        )

        /**
         * Objeto que representa la opción de cerrar sesión en el drawer.
         */
        object CerrarSesion : DrawerScreens(
            "logout",
            Icons.Filled.Close,
            "Log Out"
        )
    }
}

/**
 * Lista que contiene las pantallas que se mostrarán en el drawer de la aplicación.
 */
val screensFromDrawer = listOf(
    Screens.DrawerScreens.Mapa,
    Screens.DrawerScreens.Listar,
    Screens.DrawerScreens.ProfileScreen,
    Screens.DrawerScreens.CerrarSesion,
)