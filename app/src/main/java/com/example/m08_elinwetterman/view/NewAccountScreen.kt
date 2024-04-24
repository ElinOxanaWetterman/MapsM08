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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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

@SuppressLint("UnrememberedMutableState")
@Composable
fun RegisterScreen(navController: NavController, mapViewModel: MapViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val isLoading: Boolean by mapViewModel.isLoading.observeAsState(true)
    val goToNext: Boolean by mapViewModel.goToNext.observeAsState(false)
    val emailState: String by mapViewModel.emailState.observeAsState("")
    val passwordState: String by mapViewModel.passwordState.observeAsState("")
    val nombreState: String by mapViewModel.nombreState.observeAsState("")
    val apellidoState: String by mapViewModel.apellidoState.observeAsState("")
    val ciudadState: String by mapViewModel.ciudadState.observeAsState("")
    val showDialogPass: Boolean by mapViewModel.showDialogPass.observeAsState(false)
    val passwordProblem: Boolean by mapViewModel.passwordProblem.observeAsState(false)
    val showDialogAuth: Boolean by mapViewModel.showDialogAuth.observeAsState(false)
    val emailProblem: Boolean by mapViewModel.emailDuplicated.observeAsState(false)
    val passwordVisibility: Boolean by mapViewModel.passwordVisibility.observeAsState(false)
    val permanecerLogged: Boolean by mapViewModel.permanecerLogged.observeAsState(false)

    val context = LocalContext.current
    val userPrefs = User(context)

    if (goToNext) {
        navController.navigate(Routes.MapScreen.route)
    }

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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.mascota),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit,
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombreState,
                onValueChange = { mapViewModel.modificarNombreState(it) },
                maxLines = 1,
                label = { Text(text = "Nombre") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { keyboardController?.hide() })
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = apellidoState,
                onValueChange = { mapViewModel.modificarApellidoState(it) },
                maxLines = 1,
                label = { Text(text = "Apellido") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { keyboardController?.hide() })
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = ciudadState,
                onValueChange = { mapViewModel.modificarCiudadState(it) },
                maxLines = 1,
                label = { Text(text = "Ciudad") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { keyboardController?.hide() })
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = emailState,
                onValueChange = { mapViewModel.modificarEmailState(it) },
                maxLines = 1,
                label = { Text(text = "Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { keyboardController?.hide() })
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                        Icon(imageVector = image, contentDescription = "Password visibility")
                    }
                },
                visualTransformation = if (passwordVisibility) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                }
            )

            Row(Modifier.wrapContentSize()) {
                Checkbox(
                    checked = permanecerLogged,
                    onCheckedChange = { isChecked ->
                        mapViewModel.cambiarPermanecerLogged(isChecked)
                    })
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Recordar",
                    Modifier.align(CenterVertically),
                    color = Color.Black
                )
            }
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
                    text = "Registrar",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light,
                    color = Color(0xFFFF914D)
                )
            }

            Row {
                Column {
                    Text(
                        text = "Iniciar Sesión",
                        modifier = Modifier.clickable { navController.navigate(Routes.LogScreen.route) },
                        color = Color(0xFFFF914D)
                    )
                }
            }
        }
        MyDialogPasswordOrEmail(
            showDialogPass,
            passwordProblem
        ) { mapViewModel.modificarShowDialogPass(false) }

        MyDialogPasswordAuth(
            showDialogAuth,
            emailProblem,
            userPrefs
        ) { mapViewModel.modificarShowDialogAuth(false) }
    }
}