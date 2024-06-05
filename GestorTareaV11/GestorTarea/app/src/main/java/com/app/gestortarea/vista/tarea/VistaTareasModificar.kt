package com.app.gestortarea.vista.tarea

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.gestortarea.componentes.InputComun
import com.app.gestortarea.componentes.MiDatePicker
import com.app.gestortarea.componentes.PopUpConfirmacion
import com.app.gestortarea.componentes.PopUpInformacion
import com.app.gestortarea.componentes.botonEnvio
import com.app.gestortarea.componentes.enableTarea
import com.app.gestortarea.componentes.navBar.MyNavBar
import com.app.gestortarea.modeloDatos.Tarea
import com.app.gestortarea.nav.Vistas
import com.app.gestortarea.viewModel.SharedViewModel
import java.util.Date

/**
 * Composable que representa la vista para modificar tareas.
 * Utiliza una barra de navegación personalizada y llama al contenido de la vista.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 * @param sharedViewModel ViewModel compartido que gestiona las acciones de la aplicación.
 */
@Composable
fun VistaTareasModificar(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    MyNavBar.Content(navController, sharedViewModel) { navController, sharedViewModel ->
        ContenidoVistaTareasModificar(navController, sharedViewModel)
    }
}

/**
 * Composable que contiene el contenido de la vista de modificación de tareas.
 * Permite editar, completar y eliminar una tarea existente.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 * @param sharedViewModel ViewModel compartido que gestiona las acciones de la aplicación.
 */
@Composable
fun ContenidoVistaTareasModificar(navController: NavController, sharedViewModel: SharedViewModel) {
    // Variables de estado
    var error by remember { mutableStateOf("") }
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fechaFinAntigua by remember { mutableStateOf<Date?>(null) }
    var fechaFin by remember { mutableStateOf<Date?>(null) }
    var completada by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showDialogError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Efecto lanzado una vez para obtener los datos de la tarea
    LaunchedEffect(Unit) {
        sharedViewModel.obtenerTareaPorTitulo(
            context,
            sharedViewModel.userEmail.value,
            sharedViewModel.tituloTarea.value
        ) { tareaObtenida ->
            if (tareaObtenida != null) {
                titulo = tareaObtenida.nombre
                descripcion = tareaObtenida.descripcion
                fechaFin = tareaObtenida.fecha
                fechaFinAntigua = tareaObtenida.fecha
                completada = tareaObtenida.completada
            }
        }
    }

    // Contenido principal
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            // Botón de retroceso
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { navController.navigate(Vistas.VistaTareas.route) }
            )

            // Icono de edición
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )
            }

            // Campo de entrada para el título
            InputComun(
                titulo = "Titulo:",
                placeholder = "Título",
                value = titulo,
                onvalueChange = { value -> titulo = value }
            )

            // Campo de entrada para la descripción
            InputComun(
                titulo = "Descripción:",
                placeholder = "Descripción",
                value = descripcion,
                onvalueChange = { value -> descripcion = value }
            )

            // Selector de fecha
            MiDatePicker(
                onFechaSeleccionada = { fecha -> fechaFin = fecha },
                fechaInicial = fechaFin
            )

            // Botón para editar la tarea
            botonEnvio(
                "editar tarea",
                "normal",
                inputValido = enableTarea(
                    titulo,
                    fechaFin
                )
            ) {
                sharedViewModel.obtenerTareaPorTitulo(
                    context,
                    sharedViewModel.userEmail.value,
                    titulo
                ) { tareaObtenida ->
                    if (tareaObtenida == null) {
                        val tareaData = Tarea(
                            nombre = titulo,
                            descripcion = descripcion,
                            fecha = fechaFin,
                            completada = false
                        )
                        sharedViewModel.modificarTarea(
                            context,
                            sharedViewModel.userEmail.value,
                            sharedViewModel.tituloTarea.value,
                            tareaData
                        ) {
                            sharedViewModel.setTituloTarea("")
                            navController.navigate(Vistas.VistaTareas.route)
                        }
                    } else {
                        showDialogError = true
                        error = "El título de la tarea ya existe"
                    }
                }
            }

            // Botón para marcar la tarea como completada
            if (!completada) {
                botonEnvio(
                    "Terminar tarea",
                    "hecho",
                    inputValido = true
                ) {
                    val tareaData = Tarea(
                        nombre = sharedViewModel.tituloTarea.value,
                        descripcion = descripcion,
                        fecha = fechaFinAntigua,
                        completada = true
                    )
                    sharedViewModel.modificarTarea(
                        context,
                        sharedViewModel.userEmail.value,
                        sharedViewModel.tituloTarea.value,
                        tareaData
                    ) {
                        sharedViewModel.setTituloTarea("")
                        navController.navigate(Vistas.VistaTareas.route)
                    }
                }
            }

            // Botón para eliminar la tarea
            botonEnvio(
                "borrar tarea",
                "borrado",
                inputValido = true
            ) {
                showDialog = true
            }
        }
    }

    // Diálogo de confirmación para eliminar la tarea
    if (showDialog) {
        PopUpConfirmacion(
            titulo = "¿Estas seguro que deseas eliminar esta tarea?",
            descripcion = "Esta a punto de eliminar la tarea ${sharedViewModel.tituloTarea.value}",
            onSuccess = {
                sharedViewModel.borrarTareasPorTitulo(
                    context,
                    sharedViewModel.userEmail.value,
                    sharedViewModel.tituloTarea.value
                ) {
                    if (it) {
                        sharedViewModel.setTituloTarea("")
                        navController.navigate(Vistas.VistaTareas.route)
                    }
                }
                showDialog = false
            },
            onError = {
                showDialog = false
            },
            onDismissRequest = {
                showDialog = false
            }
        )
    }

    // Diálogo de información de error
    if (showDialogError) {
        PopUpInformacion(
            titulo = "Error",
            descripcion = error,
            onSuccess = { showDialogError = false },
            onDismissRequest = { showDialogError = false }
        )
    }
}
