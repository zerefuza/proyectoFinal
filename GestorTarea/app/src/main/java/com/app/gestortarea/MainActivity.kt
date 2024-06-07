package com.app.gestortarea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.gestortarea.nav.NavGraph
import com.app.gestortarea.ui.theme.GestorTareaTheme
import com.app.gestortarea.viewModel.SharedViewModel

/**
 * MainActivity es la actividad principal de la aplicación.
 * Se encarga de configurar la interfaz de usuario y la navegación.
 */
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private val sharedViewModel: SharedViewModel by viewModels()

    /**
     * Se llama cuando la actividad es creada por primera vez.
     * Configura la interfaz de usuario y el controlador de navegación.
     *
     * @param savedInstanceState Si la actividad se está re-inicializando después de
     * haber sido previamente cerrada, este paquete contiene los datos que más
     * recientemente
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GestorTareaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Inicializa el controlador de navegación
                    navController = rememberNavController()

                    // Configura el gráfico de navegación con el controlador y el ViewModel compartido
                    NavGraph(
                        navController = navController,
                        sharedViewModel = sharedViewModel
                    )
                }
            }
        }
    }
}