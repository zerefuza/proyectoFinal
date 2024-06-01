package com.app.gestortarea.vista

import android.content.Context
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.gestortarea.R
import com.app.gestortarea.nav.Vistas
import com.app.gestortarea.viewModel.SharedViewModel
import kotlinx.coroutines.delay
/**
 * Composable que representa la vista de pantalla de bienvenida de la aplicación.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 * @param sharedViewModel ViewModel compartido que gestiona las acciones de la aplicación.
 */
@Composable
fun InicioSplashVista(navController: NavController, sharedViewModel: SharedViewModel) {
    val scale = remember { Animatable(0f) }
    val context = LocalContext.current

    // Efecto lanzado una vez para la animación y la navegación
    LaunchedEffect(key1 = true) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        val email = sharedPreferences.getString("user_email", null)

        // Animación de escalado con retardo
        scale.animateTo(
            targetValue = 0.9f,
            animationSpec = tween(
                durationMillis = 500,
                easing = {
                    OvershootInterpolator(8f).getInterpolation(it)
                }
            )
        )
        delay(2000)

        // Navegación basada en el estado de inicio de sesión
        if (isLoggedIn && email != null) {
            sharedViewModel.setuserEmail(email)
            navController.navigate(Vistas.VistaTareas.route)
        } else {
            navController.navigate(Vistas.LoginVista.route)
        }
    }

    // Disposición de la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Surface(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxSize()
                .scale(scale.value)
                .weight(1f),
            color = Color.White,
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Imagen del logo centrada
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "logo",
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                )
            }
        }
    }
}
