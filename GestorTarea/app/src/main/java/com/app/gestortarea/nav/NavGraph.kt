package com.app.gestortarea.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.gestortarea.viewModel.SharedViewModel
import com.app.gestortarea.vista.InicioSplashVista
import com.app.gestortarea.vista.LoginVista
import com.app.gestortarea.vista.RecuperarContraseñaVista
import com.app.gestortarea.vista.RegistroUsuarioVista
import com.app.gestortarea.vista.calendaria.VistaCalendario
import com.app.gestortarea.vista.tarea.VistaTareas
import com.app.gestortarea.vista.tarea.VistaTareasAgregar
import com.app.gestortarea.vista.tarea.VistaTareasModificar


@Composable
fun NavGraph(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Vistas.InicioSplashVista.route
    ) {
        //Inicio
        composable(
            route = Vistas.InicioSplashVista.route
        ) {
            InicioSplashVista(
                navController = navController,
                sharedViewModel=sharedViewModel
            )
        }

        //Login
        composable(
            route = Vistas.LoginVista.route
        ) {
            LoginVista(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }

        //vista de registro
        composable(
            route = Vistas.RegistroUsuarioVista.route
        ) {
            RegistroUsuarioVista(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }

        //vista de tarea
        composable(
            route = Vistas.VistaTareas.route
        ) {
            VistaTareas(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }

        //vista de agregar tarea
        composable(
            route = Vistas.VistaTareasAgregar.route
        ) {
            VistaTareasAgregar(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }

        //vista de modificar tarea
        composable(
            route = Vistas.VistaTareasModificar.route
        ) {
            VistaTareasModificar(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }

        //vista de calendario
        composable(
            route = Vistas.VistaCalendario.route
        ) {
            VistaCalendario(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }

        //recuper contraseña
        composable(
            route = Vistas.RecuperarContraseñaVista.route
        ) {
            RecuperarContraseñaVista(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }
    }
}
