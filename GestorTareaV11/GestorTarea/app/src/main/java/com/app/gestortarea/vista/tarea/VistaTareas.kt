package com.app.gestortarea.vista.tarea

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.gestortarea.componentes.navBar.MyNavBar
import com.app.gestortarea.componentes.obtenerColorPorTiempoRestante
import com.app.gestortarea.componentes.obtenerTareasPorUrgencia
import com.app.gestortarea.modeloDatos.NivelUrgencia
import com.app.gestortarea.modeloDatos.Tarea
import com.app.gestortarea.nav.Vistas
import com.app.gestortarea.viewModel.SharedViewModel
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Composable que representa la vista de tareas.
 * Utiliza una barra de navegación personalizada y llama al contenido de la vista.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 * @param sharedViewModel ViewModel compartido que gestiona las acciones de la aplicación.
 */
@Composable
fun VistaTareas(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    MyNavBar.Content(navController, sharedViewModel) { navController, sharedViewModel ->
        ContenidoVistaTareas(navController, sharedViewModel)
    }
}

/**
 * Composable que contiene el contenido de la vista de tareas.
 * Muestra las tareas clasificadas por niveles de urgencia y permite filtrar las tareas.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 * @param sharedViewModel ViewModel compartido que gestiona las acciones de la aplicación.
 */
@Composable
fun ContenidoVistaTareas(navController: NavController, sharedViewModel: SharedViewModel) {
    val context = LocalContext.current
    var tareasUrgentes by remember { mutableStateOf(emptyList<Tarea>()) }
    var tareasPocoUrgentes by remember { mutableStateOf(emptyList<Tarea>()) }
    var tareasMuyUrgentes by remember { mutableStateOf(emptyList<Tarea>()) }
    var tareasPasadas by remember { mutableStateOf(emptyList<Tarea>()) }
    var tareasCompletadas by remember { mutableStateOf(emptyList<Tarea>()) }
    val urgenciaOptions = listOf("TODOS", NivelUrgencia.MUY_URGENTE.valor, NivelUrgencia.URGENTE.valor, NivelUrgencia.POCO_URGENTE.valor, NivelUrgencia.PASADAS.valor, NivelUrgencia.COMPLETADAS.valor)
    var expanded by remember { mutableStateOf(false) }
    var opcionFiltro by remember { mutableStateOf(urgenciaOptions.first()) }

    // Cargar las tareas al inicializar el componente
    LaunchedEffect(Unit) {
        sharedViewModel.obtenerTareas(context, sharedViewModel.userEmail.value) { tareasObtenidas ->
            tareasPasadas = obtenerTareasPorUrgencia(tareasObtenidas, NivelUrgencia.PASADAS.valor)
            tareasPocoUrgentes = obtenerTareasPorUrgencia(tareasObtenidas, NivelUrgencia.POCO_URGENTE.valor)
            tareasUrgentes = obtenerTareasPorUrgencia(tareasObtenidas, NivelUrgencia.URGENTE.valor)
            tareasMuyUrgentes = obtenerTareasPorUrgencia(tareasObtenidas, NivelUrgencia.MUY_URGENTE.valor)
            tareasCompletadas = obtenerTareasPorUrgencia(tareasObtenidas, NivelUrgencia.COMPLETADAS.valor)
        }
    }

    // Contenido principal de la vista
    LazyColumn {
        item {
            // Botón de filtro
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.TopEnd)
            ) {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (opcionFiltro == "TODOS") {
                            Icons.Default.FilterAlt
                        } else {
                            Icons.Default.FilterAltOff
                        },
                        contentDescription = "Filtros"
                    )
                }

                // Menú desplegable de filtros
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    urgenciaOptions.forEach { text ->
                        DropdownMenuItem(
                            text = { Text(text = text) },
                            onClick = {
                                opcionFiltro = text
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        // Mostrar las tareas según el filtro seleccionado
        if (opcionFiltro == "TODOS" || opcionFiltro == NivelUrgencia.MUY_URGENTE.valor) {
            item {
                if (tareasMuyUrgentes.isNotEmpty()) {
                    Text(
                        text = "Muy Urgentes",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                    TareaGrupo(
                        tareas = tareasMuyUrgentes,
                        navController,
                        sharedViewModel,
                    )
                }
            }
        }

        if (opcionFiltro == "TODOS" || opcionFiltro == NivelUrgencia.URGENTE.valor) {
            item {
                if (tareasUrgentes.isNotEmpty()) {
                    Text(
                        text = "Urgentes",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                    TareaGrupo(tareas = tareasUrgentes, navController, sharedViewModel)
                }
            }
        }

        if (opcionFiltro == "TODOS" || opcionFiltro == NivelUrgencia.POCO_URGENTE.valor) {
            item {
                if (tareasPocoUrgentes.isNotEmpty()) {
                    Text(
                        text = "Poco Urgentes",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                    TareaGrupo(
                        tareas = tareasPocoUrgentes,
                        navController,
                        sharedViewModel,
                    )
                }
            }
        }

        if (opcionFiltro == "TODOS" || opcionFiltro == NivelUrgencia.PASADAS.valor) {
            item {
                if (tareasPasadas.isNotEmpty()) {
                    Text(
                        text = "Tareas Pasadas de fecha",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                    TareaGrupo(tareas = tareasPasadas, navController, sharedViewModel)
                }
            }
        }

        if (opcionFiltro == "TODOS" || opcionFiltro == NivelUrgencia.COMPLETADAS.valor) {
            item {
                if (tareasCompletadas.isNotEmpty()) {
                    Text(
                        text = "Tareas Completadas",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                    TareaGrupo(
                        tareas = tareasCompletadas,
                        navController,
                        sharedViewModel,
                    )
                }
            }
        }
    }

    // Botón flotante para añadir una nueva tarea
    Row(
        modifier = Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom
    ) {
        FloatingActionButton(
            onClick = { navController.navigate(Vistas.VistaTareasAgregar.route) },
            modifier = Modifier
                .padding(16.dp),
            containerColor = Color(0xFF42A5F5)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Añadir",
            )
        }
    }
}

/**
 * Composable que muestra un grupo de tareas en una fila horizontal.
 *
 * @param tareas Lista de tareas a mostrar.
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 * @param sharedViewModel ViewModel compartido que gestiona las acciones de la aplicación.
 */
@Composable
fun TareaGrupo(
    tareas: List<Tarea>,
    navController: NavController,
    sharedViewModel: SharedViewModel,
) {
    Box(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Divider()
    }
    LazyRow {
        items(tareas) { tarea ->
            TareaRow(tarea = tarea, navController, sharedViewModel)
        }
    }
}

/**
 * Composable que representa una fila de tarea individual.
 *
 * @param tarea Tarea a mostrar.
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 * @param sharedViewModel ViewModel compartido que gestiona las acciones de la aplicación.
 */
@Composable
fun TareaRow(
    tarea: Tarea,
    navController: NavController,
    sharedViewModel: SharedViewModel,
) {
    val cardColor = obtenerColorPorTiempoRestante(tarea)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                sharedViewModel.setTituloTarea(tarea.nombre)
                navController.navigate(Vistas.VistaTareasModificar.route)
            },
        colors = CardDefaults.cardColors(cardColor),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = tarea.nombre,
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = tarea.descripcion,
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Fecha",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = SimpleDateFormat(
                        "dd-MM-yyyy hh:mm a",
                        Locale.getDefault()
                    ).format(tarea.fecha),
                    style = TextStyle(fontSize = 14.sp),
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Icon(
                    imageVector = if (tarea.completada) Icons.Default.Check else Icons.Default.Clear,
                    contentDescription = "Estado",
                    tint = Color.Blue,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
