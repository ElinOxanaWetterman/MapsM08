package com.example.m08_elinwetterman.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.m08_elinwetterman.MyDrawer
import com.example.m08_elinwetterman.Routes
import com.example.m08_elinwetterman.model.Category
import com.example.m08_elinwetterman.model.MarkerFlurry
import com.example.m08_elinwetterman.viewmodel.MapViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla que muestra una lista de marcadores en una cuadrícula, permitiendo al usuario interactuar con ellos.
 * @param navController El controlador de navegación para navegar entre pantallas.
 * @param mapViewModel El ViewModel que contiene la lógica y los datos relacionados con la pantalla de lista de marcadores.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListMarkersScreen(navController: NavController, mapViewModel: MapViewModel) {
    // Estado de la cuadrícula perezosa
    val lazyGridState = rememberLazyGridState()
    // Lista de categorías observadas desde el ViewModel
    val categories: List<Category> by mapViewModel.categories.observeAsState(emptyList())
    // Lista de marcadores observada desde el ViewModel
    val marcadores by mapViewModel.markers.observeAsState(emptyList())
    // Estado de la hoja inferior modal
    val sheetState = rememberModalBottomSheetState()
    // Ámbito de coroutine
    val scope = rememberCoroutineScope()

    // Estado de visualización de la hoja inferior modal
    val showBottomSheet by mapViewModel.showBottomSheet.observeAsState(false)
    // Texto seleccionado en el menú desplegable
    val texto: String by mapViewModel.textoDropdown.observeAsState("Mostrar Todos")

    // Estado de carga de los marcadores
    val isLoading: Boolean by mapViewModel.isLoadingMarkers.observeAsState(initial = true)
    // Carga de todos los marcadores
    mapViewModel.pillarTodosMarkers()

    // Verifica si el usuario está conectado
    if (!mapViewModel.userLogged()) {
        mapViewModel.signOut(context = LocalContext.current, navController)
    }

    // Muestra un indicador de carga si isLoading es falso
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
        // Muestra la interfaz de usuario principal
        MyDrawer(navController = navController, mapViewModel = mapViewModel) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box {
                        // Menú desplegable para seleccionar categorías de marcadores
                        OutlinedTextField(
                            value = texto,
                            onValueChange = { /* No permitimos cambios directos aquí */ },
                            enabled = false,
                            readOnly = true,
                            modifier = Modifier
                                .clickable { mapViewModel.modifyExpandedMapa(true) }
                                .fillMaxWidth()
                        )

                        DropdownMenu(
                            expanded = mapViewModel.pillarExpandedMapa(),
                            onDismissRequest = { mapViewModel.modifyExpandedMapa(false) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopStart)
                        ) {
                            DropdownMenuItem(text = {
                                Text(text = "Mostrar Todos")
                            }, onClick = {
                                mapViewModel.modifyExpandedMapa(false)
                                mapViewModel.pillarTodosMarkers()
                                mapViewModel.modificarTextoDropdown("Mostrar Todos")
                            })

                            categories.forEach { categoria ->
                                DropdownMenuItem(text = { Text(text = categoria.name) }, onClick = {
                                    mapViewModel.pillarTodosMarkersCategoria(categoria.name)
                                    mapViewModel.modifyExpandedMapa(false)
                                    mapViewModel.modificarTextoDropdown(categoria.name)
                                })
                            }
                        }
                    }

                    // Muestra un mensaje si no hay marcadores
                    if (marcadores.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No hay nada...",
                                fontSize = 33.sp,
                                color = Color.LightGray
                            )
                        }
                    } else {
                        // Muestra la cuadrícula de marcadores
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            state = lazyGridState,
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(marcadores) { marker ->
                                LocationItem(marker, navController, mapViewModel)
                            }
                        }
                    }

                    // Muestra la hoja inferior modal si es necesario
                    if (showBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                mapViewModel.modificarShowBottomSheet(false)
                            },
                            sheetState = sheetState
                        ) {
                            resetearParametros(mapViewModel)
                            AddMarkerScreen(
                                mapViewModel = mapViewModel,
                                navController,
                                onCloseBottomSheet = {
                                    scope.launch { sheetState.hide() }
                                        .invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                mapViewModel.modificarShowBottomSheet(false)
                                            }
                                        }
                                }, false
                            )
                        }
                    }
                }
                // Botón flotante para agregar un nuevo marcador
                OutlinedButton(
                    onClick = {
                        mapViewModel.modificarShowBottomSheet(true)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(20.dp),
                    border = BorderStroke(2.dp, Color(0xFFFF914D))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = null,
                            tint = Color(0xFFFF914D)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Composable que representa un elemento de ubicación en la lista de marcadores.
 * @param marker El marcador que se muestra en este elemento.
 * @param navController El controlador de navegación para navegar entre pantallas.
 * @param mapViewModel El ViewModel que contiene la lógica y los datos relacionados con la pantalla de lista de marcadores.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun LocationItem(
    marker: MarkerFlurry,
    navController: NavController,
    mapViewModel: MapViewModel
) {
    Card(
        border = BorderStroke(
            2.dp,
            when (marker.category.name) {
                "Info" -> Color(0xff59A5FF)
                "Likes" -> Color(0xffFFBD59)
                "Favoritos" -> Color(0xffF2A9A7)
                else -> Color.Black
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Muestra un indicador de carga si no hay una referencia de foto
            if (marker.photoReference == "") {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(100.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            } else {
                // Muestra la imagen del marcador usando Glide
                GlideImage(
                    model = marker.photoReference,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                )
            }
            // Botón de edición para editar el marcador
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterEnd)
            ) {
                IconButton(
                    onClick = {
                        mapViewModel.setEditingMarkers(marker)
                        mapViewModel.modificarEditedPhoto(null)
                        navController.navigate(Routes.EditMarker.route)
                    },
                    modifier = Modifier.padding(5.dp)
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Change",
                        tint = Color(0xFFFF914D)
                    )
                }
            }
        }
    }
}
