package com.app.gestortarea.vista


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
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
import java.util.regex.Pattern

@Composable
fun RecuperarContraseñaVista(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    // Propiedades
    var errorMensaje by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    val emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")

    // Métodos
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo",
                modifier = Modifier
                    .size(250.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Usuario
            InputComun(
                titulo = "email:",
                placeholder = "Email del usuario",
                value = email,
                onvalueChange = { newEmail -> email = newEmail },
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de envío
            botonEnvio(
                titulo="Recuperar Contraseña",
                "normal",
                inputValido = emailPattern.matcher(email).matches()
            ) {
                sharedViewModel.emailRecuperacionContraseña(
                    email,
                    {
                        errorMensaje=""
                        navController.navigate(Vistas.LoginVista.route)
                    },
                    {errorMensaje="Fallo al enviar el correo"}
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = errorMensaje, color = Color.Red)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

