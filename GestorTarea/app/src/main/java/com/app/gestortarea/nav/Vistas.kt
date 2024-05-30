package com.app.gestortarea.nav

sealed class Vistas(val route:String) {
    object  InicioSplashVista:Vistas(route="inicio_splash_vista")
    object  LoginVista:Vistas(route="login_vista")
    object  RegistroUsuarioVista:Vistas(route="registro_usuario_vista")
    object  VistaTareas:Vistas(route="vista_tareas")
    object  VistaTareasAgregar:Vistas(route="vista_tareas_agregar")
    object  VistaTareasModificar:Vistas(route="vista_tareas_modificar")
    object  VistaCalendario:Vistas(route="vista_calendario")
    object  RecuperarContraseniaVista:Vistas(route="recuperar_contrasenia_vista")

}