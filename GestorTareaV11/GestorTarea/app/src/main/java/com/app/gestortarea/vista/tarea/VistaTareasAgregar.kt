package com.app.gestortarea.vista.tarea

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.gestortarea.componentes.InputComun
import com.app.gestortarea.componentes.MiDatePicker
import com.app.gestortarea.componentes.PopUpInformacion
import com.app.gestortarea.componentes.botonEnvio
import com.app.gestortarea.componentes.enableTarea
import com.app.gestortarea.componentes.navBar.MyNavBar
import com.app.gestortarea.modeloDatos.Tarea
import com.app.gestortarea.nav.Vistas
import com.app.gestortarea.viewModel.SharedViewModel
import java.util.Date

/**
 * Composable que representa la vista para agregar nuevas tareas.
 * Utiliza una barra de navegación personalizada y llama al contenido de la vista.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 * @param sharedViewModel ViewModel compartido que gestiona las acciones de la aplicación.
 */
@Composable
fun VistaTareasAgregar(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    MyNavBar.Content(navController, sharedViewModel) { navController, sharedViewModel ->
        ContenidoVistaTareasAgregar(navController, sharedViewModel)
    }
}

/**
 * Composable que contiene el contenido de la vista de agregar nuevas tareas.
 * Permite agregar una nueva tarea, verificando que no exista una tarea con el mismo título.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 * @param sharedViewModel ViewModel compartido que gestiona las acciones de la aplicación.
 */
@Composable
fun ContenidoVistaTareasAgregar(navController: NavController, sharedViewModel: SharedViewModel) {
    // Contexto actual de la aplicación
    val context = LocalContext.current

    // Variables de estado para los campos de la tarea
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf<Date?>(sharedViewModel.fechaTarea.value) }
    var error by remember { mutableStateOf("") }
    var showDialog by rememberSaveable { mutableStateOf(false) }

    // Contenido principal de la vista
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            // Icono de retroceso
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { navController.navigate(Vistas.VistaTareas.route) }
            )

            // Icono de agregar persona
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PostAdd,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )
            }

            // Campo de entrada para el título
            InputComun(
                titulo = "Título:",
                placeholder = "Título",
                value = titulo,
                onvalueChange = { value -> titulo = value },
            )

            // Campo de entrada para la descripción
            InputComun(
                titulo = "Descripción:",
                placeholder = "Descripción",
                value = descripcion,
                onvalueChange = { value -> descripcion = value },
            )

            // Selector de fecha
            MiDatePicker(
                onFechaSeleccionada = { fecha -> fechaFin = fecha },
                fechaInicial = fechaFin,
            )

            // Botón de envío
            Spacer(modifier = Modifier.height(16.dp))
            botonEnvio(
                "Crear tarea",
                "normal",
                inputValido = enableTarea(titulo, fechaFin),
            ) {
                sharedViewModel.obtenerTareaPorTitulo(
                    context,
                    sharedViewModel.userEmail.value,
                    titulo = titulo
                ) { tareaObtenida ->
                    if (tareaObtenida == null) {
                        if (fechaFin != null) {
                            val tareaData = Tarea(
                                nombre = titulo,
                                descripcion = descripcion,
                                fecha = fechaFin
                            )
                            sharedViewModel.agregarTarea(
                                context,
                                sharedViewModel.userEmail.value,
                                tareaData
                            ) {
                                sharedViewModel.setFechaTarea(null)
                                navController.navigate(Vistas.VistaTareas.route)
                            }
                        } else {
                            showDialog = true
                            error = "Error en la fecha de la tarea"
                        }
                    } else {
                        showDialog = true
                        error = "El título de la tarea ya existe"
                    }
                }
            }
        }
    }

    // Diálogo de información de error
    if (showDialog) {
        PopUpInformacion(
            titulo = "Error",
            descripcion = error,
            onSuccess = { showDialog = false },
            onDismissRequest = { showDialog = false }
        )
    }
}
