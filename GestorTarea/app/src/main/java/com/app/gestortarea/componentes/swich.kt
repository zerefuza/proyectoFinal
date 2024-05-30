package com.app.gestortarea.componentes

import androidx.compose.ui.graphics.Color
import com.app.gestortarea.modeloDatos.NivelUrgencia
import com.app.gestortarea.modeloDatos.Tarea
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

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
            NivelUrgencia.MUY_URGENTE.valor ->  (horasRestantes <= 24 && fechaTarea!!.isAfter(LocalDateTime.now()) && fechaTarea.isBefore(ahoraMas24Horas)) && !tarea.completada
            NivelUrgencia.URGENTE.valor -> horasRestantes in 24..47 && !tarea.completada
            NivelUrgencia.POCO_URGENTE.valor -> horasRestantes >= 48 && !tarea.completada
            NivelUrgencia.PASADAS.valor -> horasRestantes < 0 && !tarea.completada
            NivelUrgencia.COMPLETADAS.valor -> tarea.completada
            else -> false
        }
    }
}