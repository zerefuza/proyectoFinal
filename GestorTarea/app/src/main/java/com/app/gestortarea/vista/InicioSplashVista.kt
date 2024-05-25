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
import androidx.compose.foundation.magnifier
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

@Composable
fun InicioSplashVista(navController: NavController,sharedViewModel: SharedViewModel){
    val scale = remember { Animatable(0f) }
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        val email = sharedPreferences.getString("user_email", null)

        //animacion
        scale.animateTo(
            targetValue = 0.9f,
            animationSpec = tween(
                delayMillis = 2000,
                easing = {
                    OvershootInterpolator(8f).getInterpolation(it)
                }
            )
        )
        delay(2000)

        if (isLoggedIn && email != null) {
            sharedViewModel.setuserEmail(email)
            navController.navigate(Vistas.VistaTareas.route)
        } else {
            navController.navigate(Vistas.LoginVista.route)
        }
    }
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
                .weight(1f)
            ,
            color = Color.White,
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                ,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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