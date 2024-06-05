package com.app.gestortarea.vista

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
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
import com.app.gestortarea.componentes.PasswordInput
import com.app.gestortarea.componentes.PopUpInformacion
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

/**
 * Composable que representa la vista de registro de usuario.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 * @param sharedViewModel ViewModel compartido que gestiona las acciones de la aplicación.
 */
@Composable
fun RegistroUsuarioVista(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    // Propiedades
    val context = LocalContext.current
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

    // Parseo de la fecha de nacimiento a partir de day, month, year
    fechaNacimiento = try {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormat.parse("$day/$month/$year")
    } catch (e: Exception) {
        null
    }

    var showDialog by rememberSaveable { mutableStateOf(false) }

    // Vista de formulario de registro
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        item {
            // Logo de la aplicación
            Icon(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "registrar",
                modifier = Modifier
                    .size(150.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Campo de entrada de email
            InputComun(
                titulo = "Email: ",
                placeholder = "Email del usuario",
                value = email,
                onvalueChange = { newEmail -> email = newEmail },
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Campo de entrada de nombre
            InputComun(
                titulo = "Nombre: ",
                placeholder = "Nombre del usuario",
                value = nombre,
                onvalueChange = { newNombre -> nombre = newNombre },
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Campo de entrada de apellidos
            InputComun(
                titulo = "Apellidos: ",
                placeholder = "Apellidos del usuario",
                value = apellidos,
                onvalueChange = { newApellidos -> apellidos = newApellidos },
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Campo de entrada de fecha de nacimiento
            FechaNacimientoInput(
                day = day,
                month = month,
                year = year,
                onDaySeleccionada = { value -> day = value },
                onMesSeleccionada = { value -> month = value },
                onYearSeleccionada = { value -> year = value }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Campo de entrada de contraseña
            PasswordInput(
                titulo = "Password (1 minúscula, 1 mayúscula, 1 digito, 1 carácter especial, longitud de 8 caracteres): ",
                password = password,
                onPasswordChange = { newPassword -> password = newPassword },
                passwordVisible = passwordVisible,
                onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Campo de entrada para confirmar la contraseña
            PasswordInput(
                titulo = "Confirmar Password: ",
                password = confirmPassword,
                onPasswordChange = { newPassword -> confirmPassword = newPassword },
                passwordVisible = confirmPasswordVisible,
                onPasswordVisibilityToggle = {
                    confirmPasswordVisible = !confirmPasswordVisible
                },
            )

            // Botón de envío para registrar al usuario
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
                // Revalidación de la fecha de nacimiento
                fechaNacimiento =analizarFechaNacimiento(day,month,year)

                if (fechaNacimiento != null) {
                    if (password == confirmPassword) {
                        // Registro del usuario a través del ViewModel compartido
                        sharedViewModel.registrarUsuario(email, password,
                            onSuccess = {
                                val fechaActual = Calendar.getInstance()
                                fechaActual.add(Calendar.HOUR_OF_DAY, 24)

                                val userData = Usuario(
                                    nombre = nombre,
                                    apellido = apellidos,
                                    email = email,
                                    fechaNacimiento = fechaNacimiento
                                )

                                val tareaData = Tarea(
                                    nombre = "Tarea Inicial",
                                    descripcion = "Tarea Inicial",
                                    fecha = fechaActual.time
                                )

                                // Agregar usuario y tarea inicial
                                sharedViewModel.agregarUsuario(context, userData)
                                sharedViewModel.agregarTarea(context, userData.email, tareaData) {
                                    navController.navigate(Vistas.LoginVista.route)
                                }
                            },
                            onError = { errorMessage ->
                                showDialog = true
                                errorMensaje = errorMessage
                            }
                        )
                    } else {
                        showDialog = true
                        errorMensaje = "Invalid password"
                    }
                } else {
                    showDialog = true
                    errorMensaje = "La fecha de nacimiento es invalida"
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sección de redirección a login
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

/**
 * Analiza una fecha de nacimiento representada por tres cadenas de texto (día, mes y año) y devuelve un objeto Date.
 *
 * @param day El día de nacimiento como cadena de texto.
 * @param month El mes de nacimiento como cadena de texto.
 * @param year El año de nacimiento como cadena de texto.
 * @return El objeto Date que representa la fecha de nacimiento si las cadenas de texto son válidas y representan una fecha válida; de lo contrario, devuelve null.
 */
fun analizarFechaNacimiento(day: String, month: String, year: String): Date? {
    try {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fecha = "$day/$month/$year"
        val fechaDate = dateFormat.parse(fecha)

        // Verificar si la fecha analizada es igual a la fecha ingresada
        val cal = Calendar.getInstance()
        cal.time = fechaDate
        val parsedDay = cal.get(Calendar.DAY_OF_MONTH).toString()
        val parsedMonth = (cal.get(Calendar.MONTH) + 1).toString()
        val parsedYear = cal.get(Calendar.YEAR).toString()

        // Si los datos analizados son iguales a los ingresados, la fecha es válida
        return if (parsedDay == day && parsedMonth == month && parsedYear == year) {
            fechaDate
        } else {
            null // La fecha analizada no es igual a la fecha ingresada
        }
    } catch (e: Exception) {
        // Error al analizar la fecha
        return null
    }
}