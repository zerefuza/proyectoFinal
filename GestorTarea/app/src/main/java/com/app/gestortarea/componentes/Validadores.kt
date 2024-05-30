package com.app.gestortarea.componentes

import java.util.Date
import java.util.regex.Pattern

//metodo para habilitar el boton de login
fun enableLogin(email: String, password: String): Boolean {
    val emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    return emailPattern.matcher(email).matches() && password.trim().isNotEmpty()
}

//metodo para habilitar el boton de registro
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
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"
    )
    val emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    return emailPattern.matcher(email).matches() && passwordPattern.matcher(password).matches() && nombre.isNotEmpty() && apellido.isNotEmpty() && day.isNotEmpty() && mes.isNotEmpty() && anio.isNotEmpty() && passwordConfirmacion.isNotEmpty()
}

// Método para habilitar el botón de registrar/modificar tarea
fun enableTarea(titulo: String, fecha: Date?): Boolean {
    val tituloValido = titulo.trim().isNotEmpty() && titulo.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ0-9 ]+$"))
    return tituloValido && fecha != null && fecha.after(Date())
}

fun enableTareaPorCalendario(fecha: Date?): Boolean {
    return fecha != null && fecha.after(Date())
}