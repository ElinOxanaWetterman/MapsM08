package com.example.m08_elinwetterman

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.m08_elinwetterman.view.Camara
import com.example.m08_elinwetterman.view.EditMarkerScreen
import com.example.m08_elinwetterman.view.ListMarkersScreen
import com.example.m08_elinwetterman.view.LoginScreen
import com.example.m08_elinwetterman.view.MapScreen
import com.example.m08_elinwetterman.view.ProfileScreen
import com.example.m08_elinwetterman.view.RegisterScreen
import com.example.m08_elinwetterman.view.TakePhotoScreen
import com.example.m08_elinwetterman.viewmodel.MapViewModel
import kotlinx.coroutines.launch

/**
 * Actividad principal que aloja la navegación principal de la aplicación.
 */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navigationController = rememberNavController()
            val mapViewModel by viewModels<MapViewModel>()

            NavHost(
                navController = navigationController,
                startDestination = Routes.LogScreen.route
            ) {

                composable(Routes.MapScreen.route) {
                    MapScreen(
                        navigationController,
                        mapViewModel
                    )
                }
                composable(Routes.ListMarkersScreen.route) {
                    ListMarkersScreen(
                        navigationController,
                        mapViewModel
                    )
                }
                composable(Routes.Camara.route) {
                    Camara(
                        navigationController,
                        mapViewModel
                    )
                }
                composable(Routes.EditMarker.route) {
                    EditMarkerScreen(
                        navigationController,
                        mapViewModel
                    )
                }
                composable(Routes.LogScreen.route) {
                    LoginScreen(
                        navigationController,
                        mapViewModel
                    )
                }
                composable(Routes.RegisterScreen.route) {
                    RegisterScreen(
                        navigationController,
                        mapViewModel
                    )
                }
                composable(Routes.ProfileScreen.route) {
                    ProfileScreen(
                        navigationController,
                        mapViewModel
                    )
                }
                composable(Routes.TakePhotoScreen.route) {
                    TakePhotoScreen(
                        navigationController,
                        mapViewModel
                    )
                }
            }
        }
    }
}


/**
 * Composable que representa el drawer lateral de la aplicación.
 */

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MyDrawer(
    navController: NavController,
    mapViewModel: MapViewModel,
    content: @Composable () -> Unit,
) {
    // Se inicializan variables y estados necesarios para el drawer
    val scope = rememberCoroutineScope()
    val state: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val imageUrl: String? by mapViewModel.imageUrlForUser.observeAsState("")
    mapViewModel.getProfileImageUrlForUser()
    // Se define el contenido del drawer
    ModalNavigationDrawer(drawerState = state, gesturesEnabled = false, drawerContent = {
        ModalDrawerSheet {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { scope.launch { state.close() } }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Black
                    )
                }
            }
            Divider()
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val context = LocalContext.current
                Spacer(modifier = Modifier.padding(7.dp))
                if (mapViewModel.loggedUser.value != null) {
                    Text(
                        text = "${mapViewModel.nombreUsuario.value}",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Spacer(modifier = Modifier.padding(7.dp))
                Box(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .size(200.dp)
                        .clip(CircleShape)
                ) {
                    if (imageUrl != null) {
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
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(7.dp))
                screensFromDrawer.forEach { screen ->
                    OutlinedButton(
                        modifier = Modifier.width(200.dp),
                        onClick = {
                            if (screen.route == "logout") {
                                mapViewModel.signOut(context, navController)
                            } else {
                                navController.navigate(screen.route)
                                mapViewModel.modificarTextoDropdownCat("View all")
                                mapViewModel.modificarTextoDropdown("View all")
                                scope.launch { state.close() }
                            }
                        }
                    ) {
                        Text(text = screen.title)
                    }
                }
            }
        }
    }) {
        // Se incorpora el scaffold principal dentro del drawer
        MyScaffold(
            mapViewModel,
            state,
            navController,
            content
        )
    }
}

/**
 * Composable que representa el scaffold principal de la aplicación.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyScaffold(
    mapViewModel: MapViewModel,
    state: DrawerState,
    navController: NavController,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            MyTopAppBar(
                mapViewModel,
                state,
                navController
            )
        },
    ) {
        Box(Modifier.padding(it)) {
            content()
        }
    }
}

/**
 * Composable que representa la AppBar superior de la aplicación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    mapViewModel: MapViewModel,
    state: DrawerState,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "logo"
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    state.open()
                }
            }) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color.Black
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    navController.navigate(Routes.MapScreen.route)
                    mapViewModel.modificarTextoDropdownCat("View All")
                    mapViewModel.modificarTextoDropdown("View All")
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "volver",
                )
            }
        }
    )
}