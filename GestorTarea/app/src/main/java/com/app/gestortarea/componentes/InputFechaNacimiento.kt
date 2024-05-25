package com.app.gestortarea.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


@Composable
fun FechaNacimientoInput(
    day : String,
    month : String,
    year : String,
    onDaySeleccionada: (String) -> Unit,
    onMesSeleccionada: (String) -> Unit,
    onYearSeleccionada: (String) -> Unit,
) {
    Column() {
        Text(text = "Fecha de Nacimiento", modifier = Modifier.padding(start = 25.dp))
        Row(modifier = Modifier.padding(end=24.dp, start = 24.dp)) {
            OutlinedTextField(
                value = day,
                onValueChange = { onDaySeleccionada(it) },
                label = { Text("Día") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )
            OutlinedTextField(
                value = month,
                onValueChange = { onMesSeleccionada(it) },
                label = { Text("Mes") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            )
            OutlinedTextField(
                value = year,
                onValueChange = { onYearSeleccionada(it) },
                label = { Text("Año") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
        }
    }
}