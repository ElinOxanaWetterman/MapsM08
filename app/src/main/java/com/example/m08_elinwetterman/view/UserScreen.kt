package com.example.m08_elinwetterman.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.m08_elinwetterman.MyDrawer
import com.example.m08_elinwetterman.R
import com.example.m08_elinwetterman.viewmodel.MapViewModel


/**
 * Pantalla de perfil que muestra la información del usuario y permite cambiar y guardar la foto de perfil.
 * @param navController El controlador de navegación para navegar entre pantallas.
 * @param mapViewModel El ViewModel que contiene la lógica y los datos relacionados con el perfil del usuario.
 */

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileScreen(navController: NavController, mapViewModel: MapViewModel) {
    // Observa el estado del imageUrlForUser LiveData para obtener la URL de la imagen del usuario.
    val imageUrl: String? by mapViewModel.imageUrlForUser.observeAsState(null)
    // Observa el estado del loggedUser LiveData para obtener el nombre de usuario.
    val loggedUser: String by mapViewModel.loggedUser.observeAsState("")
    val userName = loggedUser
    // Observa el estado del nombreUsuario LiveData para obtener el nombre de usuario personalizado.
    val nombre: String by mapViewModel.nombreUsuario.observeAsState(initial = "")
    // Obtiene la imagen de perfil del usuario.
    mapViewModel.getProfileImageUrlForUser()

    // Si el usuario no está conectado, cierra la sesión y redirige a la pantalla de inicio de sesión.
    if (!mapViewModel.userLogged()) {
        mapViewModel.signOut(context = LocalContext.current, navController)
    }

    // Observa el estado isLoadingMarkers LiveData para mostrar un indicador de progreso mientras se cargan los datos.
    val isLoading: Boolean by mapViewModel.isLoadingMarkers.observeAsState(initial = false)

    // Si isLoading es true, muestra un indicador de progreso.
    if (!isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary
            )
        }
    } else {
        // Si isLoading es false, muestra la pantalla de perfil.
        MyDrawer(navController = navController, mapViewModel = mapViewModel) {
            // Si showTakePhotoScreen es true, muestra la pantalla para tomar una foto de perfil.
            if (mapViewModel.showTakePhotoScreen) {
                // Lógica para mostrar la pantalla para tomar una foto de perfil.
                // Omitido por simplicidad.
            } else {
                // Muestra la información del usuario y la foto de perfil.
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = userName,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    Text(
                        text = nombre,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .size(200.dp)
                            .clip(CircleShape)
                    ) {
                        // Muestra la foto de perfil del usuario.
                        if (mapViewModel.editedProfilePhoto != null) {
                            Image(
                                bitmap = mapViewModel.editedProfilePhoto!!.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else if (imageUrl != null) {
                            GlideImage(
                                model = imageUrl,
                                contentDescription = "Profile",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.mascota),
                                contentDescription = "Profile",
                                modifier = Modifier.size(400.dp)
                                //contentScale = ContentScale.Crop,
                            )
                        }
                    }

                    // Botones para cambiar y guardar la foto de perfil.
                    Row {
                        OutlinedButton(
                            onClick = {
                                mapViewModel.modificarShowTakePhotoScreen(true)
                            },
                            modifier = Modifier
                                .width(150.dp)
                                .padding(5.dp)
                        ) {
                            Icon(
                                Icons.Filled.Image,
                                contentDescription = null,
                                tint = Color(0xFFFF914D)
                            )
                            Text(
                                "  Change",
                                color = Color(0xFFFF914D)
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                mapViewModel.updateUser()
                                mapViewModel.modificarEditedProfilePhoto(null)
                            },
                            modifier = Modifier
                                .width(150.dp)
                                .padding(5.dp)
                        ) {
                            Icon(
                                Icons.Filled.Save,
                                contentDescription = null,
                                tint = Color(0xFFFF914D)
                            )
                            Text(
                                "  Save",
                                color = Color(0xFFFF914D)
                            )
                        }
                    }
                }
            }
        }
    }
}