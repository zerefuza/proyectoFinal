package com.app.gestortarea.componentes

import java.util.Date
import java.util.regex.Pattern

/**
 * Método para habilitar el botón de inicio de sesión.
 *
 * @param email    El correo electrónico proporcionado.
 * @param password La contraseña proporcionada.
 * @return true si el correo electrónico y la contraseña son válidos, de lo contrario false.
 */
fun enableLogin(email: String, password: String): Boolean {
    val emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    return emailPattern.matcher(email).matches() && password.trim().isNotEmpty()
}

/**
 * Método para habilitar el botón de registro.
 *
 * @param email               El correo electrónico proporcionado.
 * @param nombre              El nombre proporcionado.
 * @param apellido            El apellido proporcionado.
 * @param day                 El día de nacimiento proporcionado.
 * @param mes                 El mes de nacimiento proporcionado.
 * @param anio                El año de nacimiento proporcionado.
 * @param password            La contraseña proporcionada.
 * @param passwordConfirmacion La confirmación de contraseña proporcionada.
 * @return true si todos los campos son válidos, de lo contrario false.
 */
fun enableRegistro(
    email: String,
    nombre: String,
    apellido: String,
    day:String,
    mes:String,
    anio:String,
    password: String,
    passwordConfirmacion:String
): Boolean {
    val passwordPattern = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&.,])[A-Za-z\\d@\$!%*?&.,]{8,}$"
    )
    val emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    return emailPattern.matcher(email).matches() && passwordPattern.matcher(password).matches() && nombre.isNotEmpty() && apellido.isNotEmpty() && day.isNotEmpty() && mes.isNotEmpty() && anio.isNotEmpty() && passwordConfirmacion.isNotEmpty()
}

/**
 * Método para habilitar el botón de agregar/modificar tarea.
 *
 * @param titulo El título de la tarea proporcionado.
 * @param fecha  La fecha de la tarea.
 * @return true si el título es válido y la fecha es posterior a la actual, de lo contrario false.
 */
fun enableTarea(titulo: String, fecha: Date?): Boolean {
    val tituloValido = titulo.trim().isNotEmpty() && titulo.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ0-9 ]+$")) && titulo.length<=60
    return tituloValido && fecha != null && fecha.after(Date())
}

/**
 * Método para habilitar el botón de agregar tarea desde el calendario.
 *
 * @param fecha La fecha seleccionada para la tarea.
 * @return true si la fecha es posterior a la actual, de lo contrario false.
 */
fun enableTareaPorCalendario(fecha: Date?): Boolean {
    return fecha != null && fecha.after(Date())
}