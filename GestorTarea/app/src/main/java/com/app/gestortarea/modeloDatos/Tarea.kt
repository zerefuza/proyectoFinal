package com.app.gestortarea.modeloDatos

import java.util.Date

/**
 * Clase de datos que representa una tarea.
 *
 * @property nombre El nombre de la tarea (título).
 * @property descripcion La descripción de la tarea.
 * @property fecha La fecha de la tarea.
 * @property completada Indica si la tarea está completada o no.
 */
data class Tarea(
    val nombre: String = "",
    val descripcion: String = "",
    val fecha: Date? = null,
    val completada: Boolean=false
)