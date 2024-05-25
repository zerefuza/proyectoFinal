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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.app.gestortarea.componentes.FechaNacimientoInput
import com.app.gestortarea.componentes.InputComun
import com.app.gestortarea.componentes.MiDatePicker
import com.app.gestortarea.componentes.PasswordInput
import com.app.gestortarea.componentes.botonEnvio
import com.app.gestortarea.componentes.enableRegistro
import com.app.gestortarea.modeloDatos.Tarea
import com.app.gestortarea.modeloDatos.Usuario
import com.app.gestortarea.nav.Vistas
import com.app.gestortarea.viewModel.SharedViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun RegistroUsuarioVista(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    // Propiedades
    var email by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf<Date?>(null) }
    var apellidos by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var errorMensaje by rememberSaveable { mutableStateOf("") }

    var day by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }

    fechaNacimiento = try {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormat.parse("$day/$month/$year")
    } catch (e: Exception) {
        null
    }

    // Métodos

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        item {
            Icon(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "registrar",
                modifier = Modifier
                    .size(150.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Email
            InputComun(
                titulo = "Email: ",
                placeholder = "Email del usuario",
                value = email,
                onvalueChange = { newEmail -> email = newEmail },
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Nombre
            InputComun(
                titulo = "Nombre: ",
                placeholder = "Nombre del usuario",
                value = nombre,
                onvalueChange = { newNombre -> nombre = newNombre },
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Apellidos
            InputComun(
                titulo = "Apellidos: ",
                placeholder = "Apellidos del usuario",
                value = apellidos,
                onvalueChange = { newApellidos -> apellidos = newApellidos },
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Fecha de nacimiento
            FechaNacimientoInput(
                day = day,
                month = month,
                year = year,
                onDaySeleccionada = { value -> day = value },
                onMesSeleccionada = { value -> month = value },
                onYearSeleccionada = { value -> year = value }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Contraseña
            PasswordInput(
                titulo = "Password: ",
                password = password,
                onPasswordChange = { newPassword -> password = newPassword },
                passwordVisible = passwordVisible,
                onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Confirmar Contraseña
            PasswordInput(
                titulo = "Confirmar Password: ",
                password = confirmPassword,
                onPasswordChange = { newPassword -> confirmPassword = newPassword },
                passwordVisible = confirmPasswordVisible,
                onPasswordVisibilityToggle = {
                    confirmPasswordVisible = !confirmPasswordVisible
                },
            )
            Text(text = errorMensaje, color = Color.Red)

            // Botón de envío
            botonEnvio(
                "Registrar",
                "normal",
                inputValido = enableRegistro(
                    email,
                    nombre,
                    apellidos,
                    day,
                    month,
                    year,
                    password,
                    confirmPassword
                )
            ) {
                fechaNacimiento = try {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    dateFormat.parse("$day/$month/$year")
                } catch (e: Exception) {
                    null
                }
                if (fechaNacimiento != null) {
                    if (password == confirmPassword) {
                        sharedViewModel.registrarUsuario(email, password,
                            onSuccess = {
                                val userData = Usuario(
                                    nombre = nombre,
                                    apellido = apellidos,
                                    email = email,
                                    fechaNacimiento = fechaNacimiento
                                )

                                val tareaData = Tarea(
                                    nombre = "Tarea Inicial",
                                    descripcion = "Tarea Inicial",
                                    fecha = Calendar.getInstance().time
                                )

                                sharedViewModel.agregarUsuario(userData)
                                sharedViewModel.agregarTarea(userData.email, tareaData){
                                    navController.navigate(Vistas.LoginVista.route)
                                }
                            },
                            onError = { errorMessage ->
                                errorMensaje = errorMessage
                            }
                        )
                    } else {
                        errorMensaje = "Invalid password"
                    }
                } else {
                    errorMensaje = "La fecha de nacimiento es invalida"
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Apartado de login
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "¿Ya tienes cuenta?")
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
}

