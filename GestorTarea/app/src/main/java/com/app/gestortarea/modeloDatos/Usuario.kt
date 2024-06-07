package com.app.gestortarea.modeloDatos

import java.util.Date

/**
 * Clase de datos que representa un usuario.
 *
 * @property email El correo electrónico del usuario.
 * @property nombre El nombre del usuario.
 * @property apellido El apellido del usuario.
 * @property fechaNacimiento La fecha de nacimiento del usuario.
 */
data class Usuario(
    val email: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val fechaNacimiento: Date? = null,
)