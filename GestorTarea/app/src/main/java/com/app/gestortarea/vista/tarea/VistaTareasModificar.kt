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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.gestortarea.componentes.InputComun
import com.app.gestortarea.componentes.MiDatePicker
import com.app.gestortarea.componentes.botonEnvio
import com.app.gestortarea.componentes.enableTarea
import com.app.gestortarea.componentes.enableTareaMarcarHecha
import com.app.gestortarea.componentes.navBar.MyNavBar
import com.app.gestortarea.modeloDatos.Tarea
import com.app.gestortarea.nav.Vistas
import com.app.gestortarea.viewModel.SharedViewModel
import java.util.Date

@Composable
fun VistaTareasModificar(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    MyNavBar.Content(navController, sharedViewModel) { navController, sharedViewModel ->
        ContenidoVistaTareasModificar(navController, sharedViewModel)
    }
}

@Composable
fun ContenidoVistaTareasModificar(navController: NavController, sharedViewModel: SharedViewModel) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf<Date?>(null) }
    var completada by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        sharedViewModel.obtenerTareaPorTitulo(
            sharedViewModel.userEmail.value,
            sharedViewModel.tituloTarea.value
        ) { tareaObtenida ->
            if (tareaObtenida != null) {
                titulo = tareaObtenida.nombre
                descripcion = tareaObtenida.descripcion
                fechaFin = tareaObtenida.fecha
                completada = tareaObtenida.completada
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { navController.navigate(Vistas.VistaTareas.route) }
            )

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
            // Título
            InputComun(
                titulo = "Titulo:",
                placeholder = "Título",
                value = titulo,
                onvalueChange = { value -> titulo = value }
            )

            // Descripción
            InputComun(
                titulo = "Descripción:",
                placeholder = "Descripción",
                value = descripcion,
                onvalueChange = { value -> descripcion = value }
            )

            // Fecha de fin
            MiDatePicker(
                onFechaSeleccionada = { fecha -> fechaFin = fecha },
                fechaInicial = fechaFin
            )

            // Botón de editar
            botonEnvio(
                "editar tarea",
                "normal",
                inputValido = enableTarea(
                    titulo,
                    fechaFin
                )
            ) {
                if (fechaFin != null) {
                    val tareaData = Tarea(
                        nombre = titulo,
                        descripcion = descripcion,
                        fecha = fechaFin
                    )
                    sharedViewModel.modificarTarea(
                        sharedViewModel.userEmail.value,
                        sharedViewModel.tituloTarea.value,
                        tareaData
                    ) {
                        sharedViewModel.setTituloTarea("")
                        navController.navigate(Vistas.VistaTareas.route)
                    }
                }
            }

            // Botón de hecho
            botonEnvio(
                "Terminar tarea",
                "hecho",
                inputValido = enableTareaMarcarHecha(
                    titulo
                )
            ) {
                if (fechaFin != null) {
                    val tareaData = Tarea(
                        nombre = titulo,
                        descripcion = descripcion,
                        fecha = fechaFin,
                        completada = true
                    )
                    sharedViewModel.modificarTarea(
                        sharedViewModel.userEmail.value,
                        sharedViewModel.tituloTarea.value,
                        tareaData
                    ) {
                        sharedViewModel.setTituloTarea("")
                        navController.navigate(Vistas.VistaTareas.route)
                    }
                }
            }

            botonEnvio(
                "borrar tarea",
                "borrado",
                inputValido = enableTareaMarcarHecha(
                    titulo
                )
            ) {
                if (fechaFin != null) {
                    sharedViewModel.borrarTareasPorTitulo(
                        sharedViewModel.userEmail.value,
                        titulo
                    ) {
                        if (it) {
                            sharedViewModel.setTituloTarea("")
                            navController.navigate(Vistas.VistaTareas.route)
                        }
                    }
                }
            }
        }
    }
}