package com.app.gestortarea.modeloDatos

import java.util.Date

data class Usuario(
    val email: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val fechaNacimiento: Date? = null,
)