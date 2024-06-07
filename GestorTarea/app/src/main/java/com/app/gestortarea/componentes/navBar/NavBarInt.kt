package com.app.gestortarea.componentes.navBar

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.app.gestortarea.viewModel.SharedViewModel

/**
 * Interfaz que define la estructura de una barra de navegación.
 */
interface NavBarInt {
    /**
     * Función que muestra el contenido de la barra de navegación.
     *
     * @param navController El controlador de navegación.
     * @param sharedViewModel El ViewModel compartido.
     * @param contenidoUser La función que muestra el contenido de usuario.
     */
    @Composable
    fun Content(navController: NavController, sharedViewModel: SharedViewModel, contenidoUser: @Composable (NavController,SharedViewModel) -> Unit)
}