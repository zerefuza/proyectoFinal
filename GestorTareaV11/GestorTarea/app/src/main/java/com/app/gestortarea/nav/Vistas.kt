package com.app.gestortarea.nav

/**
 * Clase sellada que define los destinos de navegación de la aplicación.
 *
 * @property route La ruta asociada al destino de navegación.
 */
sealed class Vistas(val route:String) {
    /**
     * Objeto que representa la vista de inicio splash.
     */
    object  InicioSplashVista:Vistas(route="inicio_splash_vista")

    /**
     * Objeto que representa la vista de login.
     */
    object  LoginVista:Vistas(route="login_vista")

    /**
     * Objeto que representa la vista de registro de usuario.
     */
    object  RegistroUsuarioVista:Vistas(route="registro_usuario_vista")

    /**
     * Objeto que representa la vista de tareas.
     */
    object  VistaTareas:Vistas(route="vista_tareas")

    /**
     * Objeto que representa la vista de agregar tarea.
     */
    object  VistaTareasAgregar:Vistas(route="vista_tareas_agregar")

    /**
     * Objeto que representa la vista de modificar tarea.
     */
    object  VistaTareasModificar:Vistas(route="vista_tareas_modificar")

    /**
     * Objeto que representa la vista de calendario.
     */
    object  VistaCalendario:Vistas(route="vista_calendario")

    /**
     * Objeto que representa la vista de recuperación de contraseña.
     */
    object  RecuperarContraseniaVista:Vistas(route="recuperar_contrasenia_vista")
}
