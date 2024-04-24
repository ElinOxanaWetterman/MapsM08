package com.example.m08_elinwetterman.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.m08_elinwetterman.R
import com.example.m08_elinwetterman.Routes
import com.example.m08_elinwetterman.model.User
import com.example.m08_elinwetterman.viewmodel.MapViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Pantalla de inicio de sesión que permite a los usuarios iniciar sesión en la aplicación.
 * @param navController El controlador de navegación para navegar entre pantallas.
 * @param mapViewModel El ViewModel que contiene la lógica y los datos relacionados con la pantalla de inicio de sesión.
 */
@Composable
fun LoginScreen(navController: NavController, mapViewModel: MapViewModel) {
    // Controlador del teclado
    val keyboardController = LocalSoftwareKeyboardController.current
    // Observa el estado de carga
    val isLoading: Boolean by mapViewModel.isLoading.observeAsState(true)
    // Observa el estado de navegación a la siguiente pantalla
    val goToNext: Boolean by mapViewModel.goToNext.observeAsState(false)
    // Observa el estado del campo de correo electrónico
    val emailState: String by mapViewModel.emailState.observeAsState("")
    // Observa el estado del campo de contraseña
    val passwordState: String by mapViewModel.passwordState.observeAsState("")
    // Observa el estado del diálogo para contraseña incorrecta
    val showDialogPass: Boolean by mapViewModel.showDialogPass.observeAsState(false)
    // Observa el estado del problema de contraseña
    val passwordProblem: Boolean by mapViewModel.passwordProblem.observeAsState(false)
    // Observa el estado del diálogo de autenticación
    val showDialogAuth: Boolean by mapViewModel.showDialogAuth.observeAsState(false)
    // Observa el estado del problema de correo electrónico duplicado
    val emailProblem: Boolean by mapViewModel.emailDuplicated.observeAsState(false)
    // Observa el estado de inicio de sesión válido
    val validLogin: Boolean by mapViewModel.validLogin.observeAsState(true)
    // Observa el estado de visibilidad de la contraseña
    val passwordVisibility: Boolean by mapViewModel.passwordVisibility.observeAsState(false)
    // Observa el estado de permanecer conectado
    val permanecerLogged: Boolean by mapViewModel.permanecerLogged.observeAsState(false)

    // Contexto actual
    val context = LocalContext.current
    // Preferencias de usuario
    val userPrefs = User(context)
    // Datos de usuario almacenados
    val storedUserData = userPrefs.getUserData.collectAsState(initial = emptyList())

    // Lanzador para el inicio de sesión con Google
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                mapViewModel.signInWithGoogleCredential(credential) {
                    mapViewModel.modifyProcessing(true)
                    navController.navigate(Routes.MapScreen.route)
                }
                if (account.email != null) mapViewModel.modificarLoggedUser(account.email!!)
                if (permanecerLogged) {
                    CoroutineScope(Dispatchers.IO).launch {
                        userPrefs.saveUserData(mapViewModel.pillarLoggedUser(), "")
                    }
                }
            } catch (e: Exception) {
                Log.d("Error", "failed")
            }
        }

    // Si hay datos de usuario almacenados, los asigna a los campos de correo electrónico
    if (storedUserData.value.isNotEmpty() && storedUserData.value[0] != "") {
        mapViewModel.modificarEmailState(storedUserData.value[0])
    }

    // Si hay datos de usuario almacenados y el inicio de sesión es válido, intenta iniciar sesión automáticamente
    if (storedUserData.value.isNotEmpty() && storedUserData.value[0] != ""
        && storedUserData.value[1] != "" && validLogin
    ) {
        mapViewModel.modifyProcessing(false)
        mapViewModel.login(storedUserData.value[0], storedUserData.value[1])
        if (goToNext) {
            navController.navigate(Routes.MapScreen.route)
        }
    }

    // Muestra un indicador de progreso si isLoading es falso
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
        if (goToNext) {
            navController.navigate(Routes.MapScreen.route)
        }
    } else {
        // Muestra la pantalla de inicio de sesión
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.imagenn),
                contentDescription = "FlurryLogo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Fit,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de correo electrónico
            OutlinedTextField(
                value = emailState,
                onValueChange = { mapViewModel.modificarEmailState(it) },
                maxLines = 1,
                label = { Text(text = "email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { keyboardController?.hide() })
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de contraseña
            OutlinedTextField(
                value = passwordState,
                onValueChange = { mapViewModel.modificarPasswordState(it) },
                label = { Text(text = "Password") },
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (!passwordVisibility) {
                        Icons.Filled.VisibilityOff
                    } else {
                        Icons.Filled.Visibility
                    }
                    IconButton(onClick = { mapViewModel.cambiarPassVisibility(!passwordVisibility) }) {
                        Icon(
                            imageVector = image,
                            contentDescription = "visibility"
                        )
                    }
                },
                visualTransformation = if (passwordVisibility) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                }
            )

            // Checkbox para permanecer conectado
            Row(Modifier.wrapContentSize()) {
                Checkbox(
                    checked = permanecerLogged,
                    onCheckedChange = { isChecked ->
                        mapViewModel.cambiarPermanecerLogged(isChecked)
                    })
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "Recordar contraseña",
                    Modifier.align(CenterVertically),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(3.dp))
                // Texto para navegar a la pantalla de registro
                Text(
                    text = "    Registrarse",
                    modifier = Modifier.clickable { navController.navigate(Routes.RegisterScreen.route) },
                    color = Color(0xFFFF914D)
                )
            }

            // Botón de inicio de sesión
            OutlinedButton(
                onClick = {
                    if (passwordState.length < 6) {
                        mapViewModel.modificarShowDialogPass(true)
                        mapViewModel.modificarPasswordProblem(true)
                    } else if (emailState.contains("@")) {
                        mapViewModel.login(emailState, passwordState)
                        if (permanecerLogged) {
                            CoroutineScope(Dispatchers.IO).launch {
                                userPrefs.saveUserData(emailState, passwordState)
                            }
                        }
                    } else {
                        mapViewModel.modificarPasswordProblem(false)
                        mapViewModel.modificarShowDialogPass(true)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Login",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Light,
                    color = Color(0xFFFF914D)
                )
            }
        }
        // Diálogo para contraseña incorrecta o problema de correo electrónico
        MyDialogPasswordOrEmail(
            showDialogPass,
            passwordProblem
        ) { mapViewModel.modificarShowDialogPass(false) }

        // Diálogo para problemas de autenticación
        MyDialogPasswordAuth(
            showDialogAuth,
            emailProblem,
            userPrefs
        ) { mapViewModel.modificarShowDialogAuth(false) }

    }
}

/**
 * Muestra un diálogo para problemas relacionados con la contraseña o el correo electrónico.
 * @param show Indica si se debe mostrar el diálogo.
 * @param password Indica si hay un problema con la contraseña.
 * @param onDismiss La función de callback para cerrar el diálogo.
 */
@Composable
fun MyDialogPasswordOrEmail(show: Boolean, password: Boolean, onDismiss: () -> Unit) {
    if (show) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Column(
                Modifier
                    .background(Color.White)
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                if (password) {
                    Text(text = "La contraseña debe tener al menos 6 caracteres")
                } else {
                    Text(text = "El email es incorrecto, debe contener al menos el @")
                }
            }
        }
    }
}

/**
 * Muestra un diálogo para problemas de autenticación.
 * @param show Indica si se debe mostrar el diálogo.
 * @param emailProblem Indica si hay un problema con el correo electrónico.
 * @param userPrefs Las preferencias del usuario para manejar los datos del usuario.
 * @param onDismiss La función de callback para cerrar el diálogo.
 */
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MyDialogPasswordAuth(
    show: Boolean,
    emailProblem: Boolean,
    userPrefs: User,
    onDismiss: () -> Unit,
) {
    if (show) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Column(
                Modifier
                    .background(Color.White)
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                if (emailProblem) {
                    Text(
                        text = "¡Email ya registrado o incorrecto!",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = "Credenciales incorrectas",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        userPrefs.deleteUserData()
                    }
                }
            }
        }
    }
}