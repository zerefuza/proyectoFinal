package com.app.gestortarea.componentes

import androidx.compose.ui.graphics.Color
import com.app.gestortarea.modeloDatos.Tarea
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.concurrent.TimeUnit

fun obtenerColorPorTiempoRestante(tarea:Tarea): Color {
    val fechaTarea = tarea.fecha?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()

    val horasRestantes = ChronoUnit.HOURS.between(LocalDateTime.now(), fechaTarea)
    val ahoraMas24Horas = LocalDateTime.now().plusHours(24)

    return when {
        (horasRestantes <= 24 && fechaTarea!!.isAfter(LocalDateTime.now()) && fechaTarea.isBefore(ahoraMas24Horas)) && !tarea.completada -> Color.Red
        horasRestantes in 24..47 && !tarea.completada -> Color(0xFFFFA500)
        horasRestantes >= 48 && !tarea.completada -> Color.Cyan
        horasRestantes < 0 && !tarea.completada -> Color.Gray
        tarea.completada -> Color.Green
        else -> Color.Yellow
    }
}

fun obtenerTareasPorUrgencia(tareas: List<Tarea>, nivelUrgencia: String): List<Tarea> {
    return tareas.filter { tarea ->
        val fechaTarea = tarea.fecha?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
        val horasRestantes = ChronoUnit.HOURS.between(LocalDateTime.now(), fechaTarea)
        val ahoraMas24Horas = LocalDateTime.now().plusHours(24)

        when (nivelUrgencia) {
            "MUY_URGENTE" ->  (horasRestantes <= 24 && fechaTarea!!.isAfter(LocalDateTime.now()) && fechaTarea.isBefore(ahoraMas24Horas)) && !tarea.completada
            "URGENTE" -> horasRestantes in 24..47 && !tarea.completada
            "POCO_URGENTE" -> horasRestantes >= 48 && !tarea.completada
            "PASADAS" -> horasRestantes < 0 && !tarea.completada
            "COMPLETADAS" -> tarea.completada
            else -> false
        }
    }
}