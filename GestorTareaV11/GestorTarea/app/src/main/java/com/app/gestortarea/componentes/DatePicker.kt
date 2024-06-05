package com.app.gestortarea.componentes

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Componente composable que muestra un selector de fecha y hora.
 *
 * @param onFechaSeleccionada La acción a realizar cuando se selecciona una fecha y hora.
 * @param fechaInicial La fecha y hora inicial del selector, si la hay.
 */
@Composable
fun MiDatePicker(
    onFechaSeleccionada: (Date) -> Unit,
    fechaInicial: Date? = null
) {
    var fecha: Date? by rememberSaveable { mutableStateOf(fechaInicial) }
    val mCalendar: Calendar = Calendar.getInstance()
    val context = LocalContext.current

    // Si hay una fecha inicial, configuramos el calendario en consecuencia
    fechaInicial?.let {
        mCalendar.time = it
        fecha=fechaInicial
    }

    val anio: Int = mCalendar.get(Calendar.YEAR)
    val mes: Int = mCalendar.get(Calendar.MONTH)
    val dia: Int = mCalendar.get(Calendar.DAY_OF_MONTH)
    val hora: Int = mCalendar.get(Calendar.HOUR_OF_DAY)
    val minuto: Int = mCalendar.get(Calendar.MINUTE)

    val mDatePickerDialog = DatePickerDialog(
        context,
        { _, selectedAnio: Int, selectedMes: Int, selectedDia: Int ->
            mCalendar.set(selectedAnio, selectedMes, selectedDia)
            val mTimePickerDialog = TimePickerDialog(
                context,
                { _, selectedHora: Int, selectedMinuto: Int ->
                    mCalendar.set(Calendar.HOUR_OF_DAY, selectedHora)
                    mCalendar.set(Calendar.MINUTE, selectedMinuto)
                    mCalendar.set(Calendar.SECOND, 0)
                    fecha = mCalendar.time
                    fecha?.let { onFechaSeleccionada(it) }
                },
                hora, minuto, true
            )
            mTimePickerDialog.show()
        },
        anio, mes, dia
    )

    Column {
        Text(text = "fecha finalizacion: ",
            modifier = Modifier.padding(start = 25.dp)
        )
        Row {
            OutlinedTextField(
                value = fecha?.let { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(it) } ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(text = "Seleccionar fecha y hora") },
                modifier = Modifier
                    .clickable { mDatePickerDialog.show() }
                    .padding(bottom = 10.dp, start = 25.dp, end = 25.dp)
            )
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clickable {
                        mDatePickerDialog.show()
                    }
            )
        }
    }
}

