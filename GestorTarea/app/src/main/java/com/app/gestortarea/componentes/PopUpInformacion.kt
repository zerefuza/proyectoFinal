package com.app.gestortarea.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Componente composable para mostrar un cuadro de diálogo de información.
 *
 * @param titulo           El título del cuadro de diálogo.
 * @param descripcion      La descripción o mensaje a mostrar en el cuadro de diálogo.
 * @param onSuccess        La acción a realizar al hacer clic en el botón "OK".
 * @param onDismissRequest La acción a realizar al cerrar el cuadro de diálogo.
 */
@Composable
fun PopUpInformacion(
    titulo: String,
    descripcion: String,
    onSuccess: () -> Unit,
    onDismissRequest: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AlertDialog(
            onDismissRequest = {
                onDismissRequest()
            },
            title = {
                Text(text = titulo)
            },
            text = {
                Text(descripcion)
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSuccess()
                    }) {
                    Text("OK")
                }
            }
        )
    }
}
