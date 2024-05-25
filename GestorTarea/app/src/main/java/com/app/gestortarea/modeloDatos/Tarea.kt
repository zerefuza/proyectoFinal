package com.app.gestortarea.modeloDatos

import java.util.Date

data class Tarea(
    val nombre: String = "",
    val descripcion: String = "",
    val fecha: Date? = null,
    val completada: Boolean=false
)