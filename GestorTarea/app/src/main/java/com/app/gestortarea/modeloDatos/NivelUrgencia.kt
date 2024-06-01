package com.app.gestortarea.modeloDatos

/**
 * Enumeración que define los niveles de urgencia de las tareas.
 *
 * @property valor El valor asociado al nivel de urgencia.
 */
enum class NivelUrgencia(val valor: String) {
    /**
     * Tareas pasadas.
     */
    PASADAS("PASADAS"),

    /**
     * Tareas poco urgentes.
     */
    POCO_URGENTE("POCO_URGENTE"),

    /**
     * Tareas urgentes.
     */
    URGENTE("URGENTE"),

    /**
     * Tareas muy urgentes.
     */
    MUY_URGENTE("MUY_URGENTE"),

    /**
     * Tareas completadas.
     */
    COMPLETADAS("COMPLETADAS")
}
