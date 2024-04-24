package com.example.m08_elinwetterman.view

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.m08_elinwetterman.R
import com.example.m08_elinwetterman.Routes
import com.example.m08_elinwetterman.model.User
import com.example.m08_elinwetterman.viewmodel.MapViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Pantalla de registro que permite al usuario crear una nueva cuenta.
 * @param navController El controlador de navegación para navegar entre pantallas.
 * @param mapViewModel El ViewModel que contiene la lógica y los datos relacionados con el registro de usuario.
 */

@SuppressLint("UnrememberedMutableState")
@Composable
fun RegisterScreen(navController: NavController, mapViewModel: MapViewModel) {
    // Controlador de teclado para ocultar el teclado virtual.
    val keyboardController = LocalSoftwareKeyboardController.current

    // Observa el estado isLoading LiveData para mostrar un indicador de progreso mientras se carga la pantalla.
    val isLoading: Boolean by mapViewModel.isLoading.observeAsState(true)
    // Observa el estado goToNext LiveData para navegar a la siguiente pantalla después del registro.
    val goToNext: Boolean by mapViewModel.goToNext.observeAsState(false)
    // Observa el estado de los campos de entrada del formulario.
    val emailState: String by mapViewModel.emailState.observeAsState("")
    val passwordState: String by mapViewModel.passwordState.observeAsState("")
    val nombreState: String by mapViewModel.nombreState.observeAsState("")
    val apellidoState: String by mapViewModel.apellidoState.observeAsState("")
    val ciudadState: String by mapViewModel.ciudadState.observeAsState("")
    // Observa el estado de los diálogos de error y visibilidad de contraseña.
    val showDialogPass: Boolean by mapViewModel.showDialogPass.observeAsState(false)
    val passwordProblem: Boolean by mapViewModel.passwordProblem.observeAsState(false)
    val showDialogAuth: Boolean by mapViewModel.showDialogAuth.observeAsState(false)
    val emailProblem: Boolean by mapViewModel.emailDuplicated.observeAsState(false)
    val passwordVisibility: Boolean by mapViewModel.passwordVisibility.observeAsState(false)
    val permanecerLogged: Boolean by mapViewModel.permanecerLogged.observeAsState(false)

    // Contexto actual.
    val context = LocalContext.current
    // Preferencias de usuario para recordar el inicio de sesión.
    val userPrefs = User(context)

    // Navega a la pantalla siguiente después del registro.
    if (goToNext) {
        navController.navigate(Routes.MapScreen.route)
    }

    // Si isLoading es false, muestra un indicador de progreso.
    if (!isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
        if (goToNext) {
            navController.navigate(Routes.MapScreen.route)
        }
    } else {
        // Muestra el formulario de registro.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo de la aplicación.
            Image(
                painter = painterResource(id = R.drawable.mascota),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit,
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campos de entrada para nombre, apellido, ciudad, correo electrónico y contraseña.
            OutlinedTextField(
                value = nombreState,
                onValueChange = { mapViewModel.modificarNombreState(it) },
                maxLines = 1,
                label = { Text(text = "Name") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { keyboardController?.hide() })
            )

            // Se repite la lógica anterior para los otros campos de entrada.

            // Botón para crear una nueva cuenta.
            OutlinedButton(
                onClick = {
                    if (passwordState.length < 6) {
                        mapViewModel.modificarShowDialogPass(true)
                        mapViewModel.modificarPasswordProblem(true)
                    } else if (emailState.contains("@")) {
                        if (permanecerLogged) {
                            CoroutineScope(Dispatchers.IO).launch {
                                userPrefs.saveUserData(emailState, passwordState)
                            }
                        }
                        mapViewModel.register(context, emailState, passwordState)
                    } else {
                        mapViewModel.modificarPasswordProblem(false)
                        mapViewModel.modificarShowDialogPass(true)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Create account",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White
                )
            }

            // Enlace para iniciar sesión.
            Row {
                Column {
                    Text(
                        text = "Login",
                        modifier = Modifier.clickable { navController.navigate(Routes.LogScreen.route) },
                        color = Color.Blue
                    )
                }
            }
        }

        // Diálogo para mostrar problemas con la contraseña o el correo electrónico.
        MyDialogPasswordOrEmail(
            showDialogPass,
            passwordProblem
        ) { mapViewModel.modificarShowDialogPass(false) }

        // Diálogo para mostrar problemas de autenticación con el correo electrónico duplicado.
        MyDialogPasswordAuth(
            showDialogAuth,
            emailProblem,
            userPrefs
        ) { mapViewModel.modificarShowDialogAuth(false) }
    }
}