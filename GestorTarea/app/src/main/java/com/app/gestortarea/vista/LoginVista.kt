package com.app.gestortarea.vista

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.gestortarea.R
import com.app.gestortarea.componentes.InputComun
import com.app.gestortarea.componentes.PasswordInput
import com.app.gestortarea.componentes.botonEnvio
import com.app.gestortarea.componentes.enableLogin
import com.app.gestortarea.nav.Vistas
import com.app.gestortarea.viewModel.SharedViewModel

/**
 * Composable que representa la vista de inicio de sesión del usuario.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 * @param sharedViewModel ViewModel compartido que gestiona las acciones de la aplicación.
 */
@Composable
fun LoginVista(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    // Propiedades
    val context = LocalContext.current
    var errorMensaje by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // Contenedor para la disposición de los elementos
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Imagen del logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo",
                modifier = Modifier
                    .size(250.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Campo de entrada de email
            InputComun(
                titulo = "Email:",
                placeholder = "Email del usuario",
                value = email,
                onvalueChange = { newEmail -> email = newEmail },
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de entrada de contraseña
            PasswordInput(
                titulo = "Password: ",
                password = password,
                onPasswordChange = { newPassword -> password = newPassword },
                passwordVisible = passwordVisible,
                onPasswordVisibilityToggle = { passwordVisible = !passwordVisible }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Texto para recordar la contraseña
            Text(
                text = "¿Olvidaste tu contraseña?",
                color = Color(0xFF0398FB),
                modifier = Modifier
                    .clickable {
                        navController.navigate(Vistas.RecuperarContraseniaVista.route)
                    }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de envío para iniciar sesión
            botonEnvio("Login", "normal", inputValido = enableLogin(email, password)) {
                sharedViewModel.loginUsuario(email, password, context,
                    onSuccess = {
                        errorMensaje = ""
                        sharedViewModel.setuserEmail(email)
                        navController.navigate(Vistas.VistaTareas.route)
                    },
                    onError = { errorMessage ->
                        errorMensaje = errorMessage
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar mensaje de error si hay alguno
            Text(text = errorMensaje, color = Color.Red)

            Spacer(modifier = Modifier.height(16.dp))

            // Apartado de registro
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "No tienes cuenta.")
                Text(
                    text = "Regístrate",
                    color = Color(0xFF0398FB),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { navController.navigate(Vistas.RegistroUsuarioVista.route) }
                        .padding(start = 8.dp)
                )
            }
        }
    }
}
