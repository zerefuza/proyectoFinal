package com.app.gestortarea.vista


import android.widget.Toast
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
import com.app.gestortarea.componentes.PopUpInformacion
import com.app.gestortarea.componentes.botonEnvio
import com.app.gestortarea.nav.Vistas
import com.app.gestortarea.viewModel.SharedViewModel
import java.util.regex.Pattern

/**
 * Composable que representa la vista para recuperar la contraseña del usuario.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 * @param sharedViewModel ViewModel compartido que gestiona las acciones de la aplicación.
 */
@Composable
fun RecuperarContraseniaVista(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    // Propiedades
    var errorMensaje by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    val emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    // Contenedor perezoso para la disposición de los elementos
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
                titulo = "email:",
                placeholder = "Email del usuario",
                value = email,
                onvalueChange = { newEmail -> email = newEmail },
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de envío para recuperar la contraseña
            botonEnvio(
                titulo = "Recuperar Contraseña",
                "normal",
                inputValido = emailPattern.matcher(email).matches()
            ) {
                sharedViewModel.isUsuarioRegistrado(context, email) { isRegistered ->
                    if (isRegistered) {
                        sharedViewModel.emailRecuperacionContrasenia(
                            email,
                            {
                                errorMensaje = ""
                                Toast.makeText(context, "email enviado", Toast.LENGTH_SHORT).show()
                                navController.navigate(Vistas.LoginVista.route)
                            },
                            {
                                errorMensaje = "Fallo al enviar el correo"
                                showDialog = true
                            }
                        )
                    } else {
                        errorMensaje = "Error este usuario no esta registrado en la base de datos"
                        showDialog = true
                    }
                }
            }

            // Sección de redirección a login
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Inicia sesión",
                    color = Color(0xFF0398FB),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { navController.navigate(Vistas.LoginVista.route) }
                        .padding(start = 8.dp)
                )
            }
        }
    }

    // Diálogo de error
    if (showDialog) {
        PopUpInformacion(
            titulo = "Error",
            descripcion = errorMensaje,
            onSuccess = { showDialog = false },
            onDismissRequest = { showDialog = false }
        )
    }
}

