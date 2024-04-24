package com.example.m08_elinwetterman.view

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.m08_elinwetterman.Routes
import com.example.m08_elinwetterman.model.Category
import com.example.m08_elinwetterman.model.MarkerFlurry
import com.example.m08_elinwetterman.viewmodel.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

/**
 * Composable que representa la pantalla para agregar un marcador en el mapa.
 * Permite al usuario ingresar información sobre el marcador, como título, fragmento, y tomar una foto.
 * @param mapViewModel El ViewModel que contiene la lógica y los datos relacionados con el mapa y los marcadores.
 * @param navController El controlador de navegación para navegar entre pantallas.
 * @param onCloseBottomSheet La función de callback para cerrar el BottomSheet.
 * @param estoyListScreen Un indicador booleano que indica si la pantalla actual es la pantalla de lista de marcadores.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddMarkerScreen(
    mapViewModel: MapViewModel,
    navController: NavController,
    onCloseBottomSheet: () -> Unit,
    estoyListScreen: Boolean
) {
    val categories: List<Category> by mapViewModel.categories.observeAsState(emptyList())
    val texto: String by mapViewModel.textoDropdownCategoria.observeAsState("Selecciona una categoría")
    val show: Boolean by mapViewModel.show.observeAsState(false)
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    Column(Modifier.fillMaxHeight(1f)) {
        if (!mapViewModel.userLogged()) {
            mapViewModel.signOut(context = LocalContext.current, navController)
        }
        if (show) {
            if (estoyListScreen) {
                navController.navigate(Routes.TakePhotoScreen.route)
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        TakePhotoScreen(
                            mapViewModel = mapViewModel
                        ) { photo ->
                            mapViewModel.modifyPhotoBitmap(photo)
                            mapViewModel.modifyShow(false)
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = mapViewModel.getTitle(),
                    onValueChange = { mapViewModel.modifyTitle(it) },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(15.dp))
                OutlinedTextField(
                    value = mapViewModel.getSnippet(),
                    onValueChange = { mapViewModel.modifySnippet(it) },
                    label = { Text("Snippet") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(15.dp))
                OutlinedButton(
                    onClick = { mapViewModel.modifyShow(true) },
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Icon(
                        Icons.Filled.Photo,
                        contentDescription = null,
                        tint = Color(0xFFFF914D)
                    )
                    Text(text = "  Take Photo", color = Color(0xFFFF914D))
                }

                val photoBitmap = mapViewModel.getPhotoBitmap()
                if (photoBitmap != null) {
                    Image(
                        bitmap = photoBitmap.asImageBitmap(), contentDescription = null,
                        contentScale = ContentScale.Crop, modifier = Modifier
                            .clip(CircleShape)
                            .size(200.dp)
                            .background(Color(0xFFFF914D))
                            .border(
                                width = 1.dp,
                                color = Color.White,
                                shape = CircleShape
                            )
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = texto,
                        onValueChange = {},
                        enabled = false,
                        readOnly = true,
                        modifier = Modifier
                            .clickable { mapViewModel.modifyExpanded(true) }
                            .fillMaxWidth()
                    )

                    DropdownMenu(
                        expanded = mapViewModel.pillarExpanded(),
                        onDismissRequest = { mapViewModel.modifyExpanded(false) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categories.forEach { categoria ->
                            DropdownMenuItem(text = {
                                Text(text = categoria.name)
                            }, onClick = {
                                mapViewModel.modifySelectedCategory(categoria)
                                mapViewModel.modifyExpanded(false)
                                mapViewModel.modificarTextoDropdownCat(categoria.name)
                            })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
                var show by remember { mutableStateOf(false) }
                OutlinedButton(
                    onClick = {
                        if (mapViewModel.getSelectedCategory() == null || !mapViewModel.getPhotoTaken() || mapViewModel.getTitle() == "") {
                            show = true
                        } else {
                            val categoryToAdd = mapViewModel.getSelectedCategory()!!
                            val latLng = if (mapViewModel.pillarEditingPosition() == null) {
                                mapViewModel.getPosition()
                            } else mapViewModel.pillarEditingPosition()

                            val photo = mapViewModel.getPhotoBitmap()
                            val markerToAdd =
                                photo?.let {
                                    MarkerFlurry(
                                        mapViewModel.pillarLoggedUser(),
                                        null,
                                        latLng!!.latitude,
                                        latLng.longitude,
                                        mapViewModel.getTitle(),
                                        mapViewModel.getSnippet(),
                                        categoryToAdd,
                                        it,
                                        null
                                    )
                                }
                            if (markerToAdd != null) {
                                mapViewModel.addMarkerToDatabase(markerToAdd)
                            }
                            onCloseBottomSheet()
                            resetearParametros(mapViewModel)
                            mapViewModel.modificarTextoDropdownCat("Category")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Filled.Save,
                        contentDescription = null,
                        tint = Color(0xFFFF914D)
                    )
                    Text(text = "  Add", color = Color(0xFFFF914D))
                }
                MyDialog(show) { show = false }
            }
        }
    }
}

/**
 * Composable que muestra un diálogo con un mensaje de advertencia.
 * @param show Un indicador booleano que indica si el diálogo debe mostrarse o no.
 * @param onDismiss La función de callback para cerrar el diálogo.
 */
@Composable
fun MyDialog(show: Boolean, onDismiss: () -> Unit) {
    if (show) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Column(
                Modifier
                    .background(Color.White)
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Termina de rellenar los valores")
            }
        }
    }
}