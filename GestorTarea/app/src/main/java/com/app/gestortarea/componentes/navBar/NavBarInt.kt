package com.app.gestortarea.componentes.navBar

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.app.gestortarea.viewModel.SharedViewModel

interface NavBarInt {
    @Composable
    fun Content(navController: NavController, sharedViewModel: SharedViewModel, contenidoUser: @Composable (NavController,SharedViewModel) -> Unit)
}