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
import androidx.compose.material.icons.filled.PersonAdd
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

@Composable
fun VistaTareasAgregar(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    MyNavBar.Content(navController, sharedViewModel) { navController, sharedViewModel ->
        ContenidoVistaTareasAgregar(navController, sharedViewModel)
    }
}


@Composable
fun ContenidoVistaTareasAgregar(navController: NavController, sharedViewModel: SharedViewModel) {
    val context = LocalContext.current
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf<Date?>(null) }
    var error by remember { mutableStateOf("") }
    var showDialog by rememberSaveable { mutableStateOf(false) }

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
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )
            }


            // Título
            InputComun(
                titulo = "Título:",
                placeholder = "Título",
                value = titulo,
                onvalueChange = { value -> titulo = value },
            )


            // Descripción
            InputComun(
                titulo = "Descripción:",
                placeholder = "Descripción",
                value = descripcion,
                onvalueChange = { value -> descripcion = value },
            )


            // Fecha de fin
            MiDatePicker(
                onFechaSeleccionada = { fecha -> fechaFin = fecha },
                fechaInicial = if(fechaFin==null){sharedViewModel.fechaTarea.value}else{fechaFin},
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
                            ){
                                sharedViewModel.setFechaTarea(null)
                                navController.navigate(Vistas.VistaTareas.route)
                            }
                        }else{
                            showDialog=true
                            error = "Error en la fecha de la tarea"
                        }
                    } else {
                        showDialog=true
                        error = "El título de la tarea ya existe"
                    }
                }
            }
        }
    }
    if (showDialog){
        PopUpInformacion(
            titulo = "Error",
            descripcion = error,
            onSuccess = { showDialog = false },
            onDismissRequest = {showDialog = false}
        )
    }
}

