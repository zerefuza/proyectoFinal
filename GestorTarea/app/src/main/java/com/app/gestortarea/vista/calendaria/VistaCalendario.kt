package com.app.gestortarea.vista.calendaria

import android.util.Log
import android.widget.CalendarView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.app.gestortarea.componentes.botonEnvio
import com.app.gestortarea.componentes.enableTarea
import com.app.gestortarea.componentes.navBar.MyNavBar
import com.app.gestortarea.modeloDatos.Tarea
import com.app.gestortarea.nav.Vistas
import com.app.gestortarea.viewModel.SharedViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun VistaCalendario(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    MyNavBar.Content(navController, sharedViewModel) { navController, sharedViewModel ->
        // Personaliza aquí el contenidoUser según sea necesario, utilizando navController y sharedViewModel
        ContenidoVistaCalendario(navController, sharedViewModel)
    }
}

@Composable
fun ContenidoVistaCalendario(navController: NavController, sharedViewModel: SharedViewModel) {
    var tareas by remember { mutableStateOf(emptyList<Tarea>()) }
    var fecha by remember { mutableStateOf("") }
    var tareasFiltradas by remember { mutableStateOf<List<Tarea>>(emptyList()) }
    val context = LocalContext.current

    // Cargar todas las tareas al iniciar
    LaunchedEffect(Unit) {
        sharedViewModel.obtenerTareas(sharedViewModel.userEmail.value ?: "") { tareasCargadas ->
            tareas = tareasCargadas
            Log.d("ContenidoVistaCalendario", "Tareas cargadas: ${tareas.size}")
        }
    }

    Column {
        // CalendarView integrado en el componente Compose
        AndroidView(
            factory = { CalendarView(context) },
            update = { calendarView ->
                calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                    // Actualizar la fecha seleccionada
                    fecha = "$dayOfMonth-${month + 1}-$year"
                    Log.d("ContenidoVistaCalendario", "Fecha seleccionada: $fecha")

                    // Convertir la fecha seleccionada a Date
                    val selectedDate = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth, 0, 0, 0)
                    }.time

                    // Filtrar tareas localmente por la fecha seleccionada
                    tareasFiltradas = tareas.filter { tarea ->
                        tarea.fecha?.let {
                            val tareaDate = Calendar.getInstance().apply { time = it }.time
                            val formattedTareaDate = SimpleDateFormat(
                                "dd-MM-yyyy",
                                Locale.getDefault()
                            ).format(tareaDate)
                            val formattedSelectedDate =
                                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(
                                    selectedDate
                                )
                            Log.d(
                                "ContenidoVistaCalendario",
                                "Comparando tarea: $formattedTareaDate con seleccionada: $formattedSelectedDate"
                            )
                            formattedTareaDate == formattedSelectedDate
                        } ?: false
                    }
                    Log.d(
                        "ContenidoVistaCalendario",
                        "Tareas filtradas: ${tareasFiltradas.size} para la fecha: $fecha"
                    )
                }
            }
            ,
            modifier = Modifier.fillMaxWidth()
        )

        // Mostrar la fecha seleccionada
        Text(
            text = "Fecha seleccionada: $fecha",
            modifier = Modifier.padding(16.dp)
        )

        // Mostrar la lista de tareas para la fecha seleccionada
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (tareasFiltradas.isEmpty() && fecha.isNotEmpty()) {
                item {
                    Text(text = "No hay tareas para esta fecha")
                    botonEnvio(
                        "Crear tarea",
                        "normal",
                        inputValido = true
                    ) {
                        navController.navigate(Vistas.VistaTareasAgregar.route)
                    }
                }
            } else {
                items(tareasFiltradas) { tarea ->
                    TareaItem(tarea = tarea, navController, sharedViewModel)
                }
            }
        }
    }
}

@Composable
fun TareaItem(tarea: Tarea, navController: NavController, sharedViewModel: SharedViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                if (!tarea.completada) {
                    sharedViewModel.setTituloTarea(tarea.nombre)
                    navController.navigate(Vistas.VistaTareasModificar.route)
                }
            }
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
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}
