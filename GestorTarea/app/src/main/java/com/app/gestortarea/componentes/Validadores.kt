package com.app.gestortarea.componentes

import java.util.Date
import java.util.regex.Pattern

//metodo para habilitar el boton de login
fun enableLogin(email: String, password: String): Boolean {
    val emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    return emailPattern.matcher(email).matches() && password.length > 7
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
    val emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    return emailPattern.matcher(email)
        .matches() && password.length > 7 && nombre.isNotEmpty() && apellido.isNotEmpty() && day.isNotEmpty() && mes.isNotEmpty() && anio.isNotEmpty() && passwordConfirmacion.isNotEmpty()
}

// Método para habilitar el botón de registrar/modificar tarea
fun enableTarea(titulo: String, fecha: Date?): Boolean {
    return titulo.isNotEmpty() && fecha != null && fecha.after(Date())
}

// Método para habilitar el botón de registrar/modificar tarea
fun enableTareaMarcarHecha(titulo: String): Boolean {
    return titulo.isNotEmpty()
}